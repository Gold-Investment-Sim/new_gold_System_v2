// src/components/MetricMini.jsx
import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip } from "recharts";
import "./MetricMini.css";

const toISO = (d) => {
  const x = new Date(d);
  x.setHours(12, 0, 0, 0);
  return `${x.getFullYear()}-${String(x.getMonth() + 1).padStart(2, "0")}-${String(x.getDate()).padStart(2, "0")}`;
};

export default function MetricMini({
  title,
  metric,           // fx_rate | vix | etf_volume | gold_close | pred_close
  selectedDate,     // t일
  onClick,          // 선택적
  onExpand,         // 선택적
}) {
  const [rows, setRows] = useState([]);
  const fire = useMemo(() => onClick || onExpand, [onClick, onExpand]);
  const payload = useMemo(() => ({ metric, title }), [metric, title]);

  useEffect(() => {
    if (!selectedDate) return;

    const end = new Date(selectedDate);
    end.setDate(end.getDate() - 1);
    const start = new Date(end);
    start.setDate(start.getDate() - 30);

    // ✅ 여기 수정됨
    axios.get("/api/metrics/series", {
      params: { metric, from: toISO(start), to: toISO(end) },
    })
      .then(({ data }) => {
        const sorted = (Array.isArray(data) ? data : [])
          .filter(v => v?.date && v?.value != null)
          .sort((a, b) => new Date(a.date) - new Date(b.date))
          .slice(-30)
          .map((v, i) => ({ x: v.date ?? i, y: Number(v.value) }));
        setRows(sorted);
      })
      .catch(() => setRows([]));
  }, [selectedDate, metric]);

  return (
    <div className="metric-card mini" onClick={() => fire?.(payload)}>
      <div className="metric-head">
        <h4 className="metric-title">{title}</h4>
        <button
          className="expand-btn"
          type="button"
          aria-label="확대"
          onClick={(e) => {
            e.stopPropagation();
            fire?.(payload);
          }}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
              e.preventDefault();
              e.stopPropagation();
              fire?.(payload);
            }
          }}
        >
          ⛶
        </button>
      </div>
      <div className="metric-body">
        {rows.length ? (
          <ResponsiveContainer width="100%" height={80}>
            <LineChart data={rows} margin={{ top: 6, right: 8, bottom: 2, left: 8 }}>
              <XAxis dataKey="x" hide />
              <YAxis hide />
              <Tooltip
                wrapperStyle={{ zIndex: 1000 }}
                contentStyle={{ fontSize: "12px", padding: "4px 6px" }}
                labelFormatter={(l) => `날짜: ${l}`}
                formatter={(v) => [
                  `${v.toLocaleString()} ${
                    title.includes("환율") ? "원/USD" :
                    title.includes("금") ? "원/g" :
                    title.includes("VIX") ? "pt" :
                    title.includes("ETF") ? "주" : ""
                  }`,
                  title
                ]}
              />
              <Line type="monotone" dataKey="y" dot={false} />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div
            style={{
              height: 80,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "#999"
            }}
          >
            데이터 없음
          </div>
        )}
      </div>
    </div>
  );
}
