// src/components/MetricCard.jsx
import { useEffect, useState, useMemo } from "react";
import { ResponsiveContainer, LineChart, Line, Tooltip, CartesianGrid, XAxis } from "recharts";
import axios from "axios";
import InfoTooltip from "./InfoTooltip";
import "./MetricCard.css";

const toISO = (d) => {
  const x = new Date(d);
  x.setHours(12, 0, 0, 0);
  return `${x.getFullYear()}-${String(x.getMonth() + 1).padStart(2, "0")}-${String(
    x.getDate()
  ).padStart(2, "0")}`;
};

const fmt = (dObj) => {
    const d = new Date(dObj);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const D = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${D}`;
};

const KEYS = ["All", "7y", "5y", "3y", "1y", "6m", "3m", "1m", "15d"];
const DAYS = {
  "15d": 15,
  "1m": 30,
  "3m": 90,
  "6m": 182,
  "1y": 365,
  "3y": 1095,
  "5y": 1825,
  "7y": 2555,
};

export default function MetricCard({
  title,
  metric,
  selectedDate,
  onClose,
  defaultUnit = "1m",
}) {
  const [unit, setUnit] = useState(defaultUnit);
  const [data, setData] = useState([]);
  const [allData, setAllData] = useState([]);
  const [loading, setLoading] = useState(true);

  const normMetric = useMemo(
    () => (metric === "gold_close" ? "krw_g_close" : metric),
    [metric]
  );
  const isPred = normMetric === "pred_close";

  const ulabel = useMemo(() => {
    if (title.includes("환율")) return "원/USD";
    if (title.includes("금")) return "원/g";
    if (title.includes("VIX")) return "pt";
    if (title.includes("ETF")) return "주";
    return "";
  }, [title]);

  // LSTM 전체 구간 로드 (selectedDate 기준)
  useEffect(() => {
    if (!selectedDate || !isPred) return;

    setLoading(true);
    setAllData([]);

    const end = new Date(selectedDate);
    const ctrl = new AbortController();

    axios
      .get("/api/lstm/series-all", {
        withCredentials: true,
        params: { to: fmt(end) },
        signal: ctrl.signal,
      })
      .then(({ data }) => {
        const rows = Array.isArray(data) ? data : [];
        const sorted = rows
          .filter((v) => v?.date && v?.value != null)
          .sort((a, b) => new Date(a.date) - new Date(b.date))
          .map((r, i) => ({
            x: r.date ?? i,
            y: Number(r.value),
          }));
        setAllData(sorted);
      })
      .catch(() => setAllData([]))
      .finally(() => setLoading(false));

    return () => ctrl.abort();
  }, [selectedDate, isPred]);

  // 일반 지표: 선택한 날짜 기준 범위 로드
  useEffect(() => {
    if (!selectedDate || isPred) return;

    setLoading(true);
    setData([]);

    const end = new Date(selectedDate);
    const start = new Date(end);
    const k = unit.toLowerCase();

    if (k === "all") {
      // 임의로 10년. 필요시 서버쪽 전체 응답 사용 가능.
      start.setFullYear(end.getFullYear() - 10);
    } else if (k.endsWith("y")) {
      const y = parseInt(k.replace("y", ""), 10);
      start.setFullYear(end.getFullYear() - y);
    } else {
      const d = DAYS[k] ?? 30;
      start.setDate(end.getDate() - d);
    }

    const ctrl = new AbortController();

    axios
      .get("/api/metrics/series", {
        withCredentials: true,
        params: {
          metric: normMetric,
          from: fmt(start),
          to: fmt(end),
        },
        signal: ctrl.signal,
      })
      .then(({ data }) => {
        const rows = Array.isArray(data) ? data : [];
        const sorted = rows
          .filter((v) => v?.date && v?.value != null)
          .sort((a, b) => new Date(a.date) - new Date(b.date))
          .map((r, i) => ({
            x: r.date ?? i,
            y: Number(r.value),
          }));
        setData(sorted);
      })
      .catch(() => setData([]))
      .finally(() => setLoading(false));

    return () => ctrl.abort();
  }, [normMetric, selectedDate, unit, isPred]);

  // LSTM: unit에 따라 selectedDate 기준으로 슬라이스
  const sliced = useMemo(() => {
    if (!isPred) return data;
    if (!allData.length) return [];

    const end = selectedDate
      ? new Date(selectedDate)
      : new Date(allData[allData.length - 1].x);

    const k = unit.toLowerCase();
    let start;

    if (k === "all") {
      start = new Date(allData[0].x);
    } else if (k.endsWith("y")) {
      const y = parseInt(k.replace("y", ""), 10);
      start = new Date(end);
      start.setFullYear(end.getFullYear() - y);
    } else {
      const d = DAYS[k] ?? 30;
      start = new Date(end);
      start.setDate(end.getDate() - d);
    }

    return allData.filter((r) => {
      const dx = new Date(r.x);
      return dx >= start && dx <= end;
    });
  }, [allData, unit, isPred, data, selectedDate]);

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <header className="metric-head">
          <h3 className="metric-title">
            {title}
            <InfoTooltip title={title} isPred={isPred} />
          </h3>

          <div className="metric-range">
            <div
              className="segmented"
              role="radiogroup"
              aria-label="기간"
            >
              {KEYS.map((key) => {
                const active =
                  unit.toLowerCase() === key.toLowerCase();
                return (
                  <button
                    key={key}
                    role="radio"
                    aria-checked={active}
                    className={`seg-btn ${
                      active ? "is-active" : ""
                    }`}
                    onClick={() =>
                      setUnit(key.toLowerCase())
                    }
                  >
                    {key.toUpperCase()}
                  </button>
                );
              })}
            </div>
            <button
              className="close-btn"
              onClick={onClose}
              aria-label="닫기"
            >
              X
            </button>
          </div>
        </header>

        <div className="metric-body">
          {loading || sliced.length === 0 ? (
            <div className="loader-wrap h320">
              <div className="spinner" />
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={320}>
              <LineChart
                data={sliced}
                margin={{
                  top: 12,
                  right: 16,
                  bottom: 8,
                  left: 12,
                }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="x" tick={false} />
                <Line
                  type="monotone"
                  dataKey="y"
                  strokeWidth={2}
                  dot={false}
                />
                <Tooltip
                  wrapperStyle={{ zIndex: 1000 }}
                  contentStyle={{
                    fontSize: "12px",
                    padding: "6px 8px",
                  }}
                  labelFormatter={(l) => `날짜: ${l}`}
                  formatter={(v) => [
                    `${Number(v).toLocaleString()} ${ulabel}`,
                    title,
                  ]}
                />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>
    </div>
  );
}
