// src/components/MetricMini.jsx
import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip } from "recharts";
import "./MetricMini.css";
import InfoTooltip from "./InfoTooltip";

const toISO = (d) => {
  const x = new Date(d);
  x.setHours(12, 0, 0, 0);
  return `${x.getFullYear()}-${String(x.getMonth() + 1).padStart(2, "0")}-${String(
    x.getDate()
  ).padStart(2, "0")}`;
};

export default function MetricMini({ title, metric, selectedDate, onClick, onExpand }) {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  const fire = useMemo(() => onClick || onExpand, [onClick, onExpand]);
  const payload = useMemo(() => ({ metric, title }), [metric, title]);
  const normMetric = useMemo(
    () => (metric === "gold_close" ? "krw_g_close" : metric),
    [metric]
  );
  const isPred = normMetric === "pred_close";

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

    const end = new Date(selectedDate);

    const periodDays = 1095;
    const start = new Date(end);
    start.setDate(start.getDate() - periodDays);

    const url = isPred ? "/api/lstm/series-all" : "/api/metrics/series";
    const params = isPred
      // LSTM: to 기준 전체 받아온 뒤, 아래에서 3년 구간만 필터
      ? { to: toISO(end) }
      // 일반 지표: 3년(from) ~ 선택일(to) 범위 요청
      : { metric: normMetric, from: toISO(start), to: toISO(end) };

    const ctrl = new AbortController();

    axios
      .get(url, { withCredentials: true, params, signal: ctrl.signal })
      .then(({ data }) => {
        const arr = Array.isArray(data) ? data : [];

        let sorted = arr
          .filter((v) => v?.date && v?.value != null)
          .sort((a, b) => new Date(a.date) - new Date(b.date))
          .map((v, i) => ({
            x: v.date ?? i,
            y: Number(v.value),
          }));

        if (isPred) {
          const startTime = start.getTime();
          const endTime = end.getTime();
          sorted = sorted.filter((p) => {
            const t = new Date(p.x).getTime();
            return t >= startTime && t <= endTime;
          });
        }

        const MAX_POINTS = 120;
        const step = Math.max(1, Math.ceil(sorted.length / MAX_POINTS));
        const reduced = sorted.filter((_, i) => i % step === 0);

        setRows(reduced);
      })
      .catch(() => setRows([]))
      .finally(() => setLoading(false));

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
            <LineChart data={rows} margin={{ top: 6, right: 16, bottom: 2, left: 8 }}>
              <XAxis dataKey="x" hide />
              <YAxis hide />
              <Tooltip
                wrapperStyle={{ zIndex: 1000 }}
                contentStyle={{ fontSize: "12px", padding: "6px 8px", lineHeight: "1.4em" }}
                labelFormatter={(l) => `날짜: ${l}`}
                formatter={(v) => [`${Number(v).toLocaleString()} ${unit}`, title]}
              />
              <Line type="monotone" dataKey="y" dot={false} />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  );
}