// src/components/MetricMini.jsx
import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
} from "recharts";
import "./MetricMini.css";
import InfoTooltip from "./InfoTooltip";

// API 파라미터용 YYYY-MM-DD (시간은 12:00 고정)
const toISO = (d) => {
  const x = new Date(d);
  x.setHours(12, 0, 0, 0);
  const y = x.getFullYear();
  const m = String(x.getMonth() + 1).padStart(2, "0");
  const D = String(x.getDate()).padStart(2, "0");
  return `${y}-${m}-${D}`;
};

// 날짜 비교용: Date | string → YYYY-MM-DD
const toISODateOnly = (v) => {
  if (!v) return "";
  if (typeof v === "string") return v.slice(0, 10);
  return toISO(v);
};

export default function MetricMini({
  title,
  metric,
  selectedDate,
  onClick,
  onExpand,
}) {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  const fire = useMemo(() => onClick || onExpand, [onClick, onExpand]);
  const payload = useMemo(() => ({ metric, title }), [metric, title]);

  // gold_close → BE 표준 컬럼명
  const normMetric = useMemo(
    () => (metric === "gold_close" ? "krw_g_close" : metric),
    [metric]
  );
  const isPred = normMetric === "pred_close";

  // 단위 자동 설정
  const unit = useMemo(() => {
    if (title.includes("환율")) return "원/USD";
    if (title.includes("금")) return "원/g";
    if (title.includes("VIX")) return "pt";
    if (title.includes("ETF")) return "주";
    return "";
  }, [title]);

  useEffect(() => {
    if (!selectedDate) return;

    setLoading(true);
    setRows([]);

    const endDate = new Date(selectedDate);
    const periodDays = 1095;

    const startDate = new Date(endDate);
    startDate.setDate(startDate.getDate() - periodDays);

    const url = isPred ? "/api/lstm/series-all" : "/api/metrics/series";

    // ✅ 여기 부분 문법 수정
    const params = isPred
      ? { to: toISO(endDate) } // LSTM: to 기준 전체 받아옴
      : {
          // 일반 지표: 3년 구간 요청
          metric: normMetric,
          from: toISO(startDate),
          to: toISO(endDate),
        };

    const startTime = startDate.getTime();
    const endTime = endDate.getTime();

    const ctrl = new AbortController();

    axios
      .get(url, {
        withCredentials: true,
        params,
        signal: ctrl.signal,
      })
      .then(({ data }) => {
        const arr = Array.isArray(data) ? data : [];

        let sorted = arr
          .filter((v) => v?.date && v?.value != null)
          .sort(
            (a, b) =>
              new Date(a.date).getTime() - new Date(b.date).getTime()
          )
          .map((v, i) => ({
            x: v.date ?? i,
            y: Number(v.value),
          }));

        // 예측 데이터는 받은 전체 중 3년 구간만 필터
        if (isPred) {
          sorted = sorted.filter((p) => {
            const t = new Date(p.x).getTime();
            return t >= startTime && t <= endTime;
          });
        }

        // 포인트 수 축소 (최대 120개)
        const MAX_POINTS = 120;
        const step = Math.max(1, Math.ceil(sorted.length / MAX_POINTS));
        const reduced = sorted.filter((_, i) => i % step === 0);

        setRows(reduced);
      })
      .catch(() => {
        setRows([]);
      })
      .finally(() => {
        setLoading(false);
      });

    return () => ctrl.abort();
  }, [selectedDate, normMetric, isPred]);

  return (
    <div className="metric-card mini" onClick={() => fire?.(payload)}>
      <div className="metric-head">
        <h4 className="metric-title">
          <InfoTooltip title={title} isPred={normMetric === "pred_close"} />
          <span>{title}</span>
        </h4>
        <button
          className="expand-btn"
          type="button"
          aria-label="확대"
          onClick={(e) => {
            e.stopPropagation();
            fire?.(payload);
          }}
        >
          ⛶
        </button>
      </div>

      <div className="metric-body mini">
        {loading || rows.length === 0 ? (
          <div className="loader-wrap h80">
            <div className="spinner" />
          </div>
        ) : (
          <ResponsiveContainer width="100%" height={80}>
            <LineChart
              data={rows}
              margin={{ top: 6, right: 16, bottom: 2, left: 8 }}
            >
              <XAxis dataKey="x" hide />
              <YAxis hide />
              <Tooltip
                wrapperStyle={{ zIndex: 1000 }}
                contentStyle={{
                  fontSize: "12px",
                  padding: "6px 8px",
                  lineHeight: "1.4em",
                }}
                labelFormatter={(l) => `날짜: ${l}`}
                formatter={(v) => [
                  `${Number(v).toLocaleString()} ${unit}`,
                  title,
                ]}
              />
              <Line type="monotone" dataKey="y" dot={false} />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  );
}
