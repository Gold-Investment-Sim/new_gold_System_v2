import { useEffect, useState } from "react";
import { ResponsiveContainer, LineChart, Line, Tooltip, CartesianGrid, XAxis } from "recharts";
import axios from "axios";
import "./MetricCard.css";

const toISO = (d) => {
    const x = new Date(d);
    x.setHours(12, 0, 0, 0);
    return `${x.getFullYear()}-${String(x.getMonth() + 1).padStart(2, "0")}-${String(
        x.getDate()
    ).padStart(2, "0")}`;
};

// 기간 키
const KEYS = ["All", "7y", "5y", "3y", "1y", "6m", "3m", "1m", "15d"];
const DAYS = { "15d": 15, "1m": 30, "3m": 90, "6m": 182, "1y": 365, "3y": 1095, "5y": 1825, "7y": 2555 };

export default function MetricCard({ title, metric, selectedDate, onClose, defaultUnit = "1m" }) {
    const [unit, setUnit] = useState(defaultUnit);
    const [data, setData] = useState([]);

    useEffect(() => {
        if (!selectedDate) return;

        const end = new Date(selectedDate);
        end.setDate(end.getDate() - 1); // t-1

        const start = new Date(end);
        const k = unit.toLowerCase();

        if (k === "all") {
            // All: 데이터 시작 연도부터 (예: 2015)
            start.setFullYear(end.getFullYear() - 10);
        } else if (k.endsWith("y")) {
            const years = parseInt(k.replace("y", ""), 10);
            start.setFullYear(end.getFullYear() - years);
        } else {
            const days = DAYS[k] ?? 30;
            start.setDate(end.getDate() - days);
        }

        axios
            .get("http://localhost:8080/api/metrics/series", {
                withCredentials: true,
                params: { metric, from: toISO(start), to: toISO(end) },
            })
            .then(({ data }) => {
                const rows = Array.isArray(data) ? data : [];
                const sorted = rows
                    .filter((v) => v?.date && v?.value != null)
                    .sort((a, b) => new Date(a.date) - new Date(b.date))
                    .map((r, i) => ({ x: r.date ?? i, y: Number(r.value) }));
                setData(sorted);
            })
            .catch(() => setData([]));
    }, [metric, selectedDate, unit]);

    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal-card" onClick={(e) => e.stopPropagation()}>
                <header className="metric-head">
                    <h3 className="metric-title">{title}</h3>
                    <div className="metric-range">
                        <div className="segmented" role="radiogroup" aria-label="기간">
                            {KEYS.map((key) => {
                                const active = unit.toLowerCase() === key.toLowerCase();
                                return (
                                    <button
                                        key={key}
                                        role="radio"
                                        aria-checked={active}
                                        className={`seg-btn ${active ? "is-active" : ""}`}
                                        onClick={() => setUnit(key.toLowerCase())}
                                    >
                                        {key.toUpperCase()}
                                    </button>
                                );
                            })}
                        </div>
                        <button className="close-btn" onClick={onClose}>
                            X
                        </button>
                    </div>
                </header>

                <div className="metric-body">
                    <ResponsiveContainer width="100%" height={320}>
                        <LineChart data={data} margin={{ top: 12, right: 16, bottom: 8, left: 12 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="x" tick={false} />
                            <Line type="monotone" dataKey="y" strokeWidth={2} dot={false} />
                            <Tooltip
                                wrapperStyle={{ zIndex: 1000 }}
                                contentStyle={{ fontSize: "12px", padding: "4px 6px" }}
                                labelFormatter={(l) => `날짜: ${l}`}
                                formatter={(v) => [`${v.toLocaleString()} ${title.includes("환율") ? "원/USD" :
                                    title.includes("금") ? "원/g" :
                                        title.includes("VIX") ? "pt" :
                                            title.includes("ETF") ? "주" :
                                                ""}`, title]}
                            />

                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </div>
        </div>
    );
}
