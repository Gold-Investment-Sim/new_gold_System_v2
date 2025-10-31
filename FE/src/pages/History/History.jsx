// src/pages/HistoryDashboard.jsx
import { useEffect, useMemo, useState } from "react";
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from "recharts";
import "../History/History.css";
import InfoTooltip from "../../components/InfoTooltip";

const RANGE_MIN = "2023-01-01";
const RANGE_MAX = "2024-12-31";
const LEGEND_W = 160;

// 파이와 범례 고정 순서: 손익 → 손실
const PIE_ORDER = ["correct", "wrong"];

const clampDate = (v) => {
  if (!v) return v;
  if (v < RANGE_MIN) return RANGE_MIN;
  if (v > RANGE_MAX) return RANGE_MAX;
  return v;
};

const qs = (obj) =>
  Object.entries(obj)
    .filter(([, v]) => v !== undefined && v !== null && v !== "")
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`)
    .join("&");

const fmt = (n, d = 1) =>
  new Intl.NumberFormat(undefined, { maximumFractionDigits: d, minimumFractionDigits: d }).format(n);

const fmtSign = (n, d = 1) => {
  if (n === null || n === undefined || isNaN(n)) return "-";
  const v = Number(n);
  const body = Math.abs(v).toLocaleString(undefined, { minimumFractionDigits: d, maximumFractionDigits: d });
  if (v > 0) return `▲ +${body}`;
  if (v < 0) return `▼ -${body}`;
  return "0.0";
};

const fmtPnl = (v) => fmtSign(v, 1);

const dash = (v, d = 0) =>
  v == null || v === "" ? "-" : Number(v).toLocaleString(undefined, { minimumFractionDigits: d, maximumFractionDigits: d });

const won = (v) =>
  v == null || isNaN(v)
    ? "-"
    : "₩" + Number(v).toLocaleString(undefined, { maximumFractionDigits: 0 });

async function getHistory(filters) {
  const url = `http://localhost:8080/api/history?${qs(filters)}`;
  const res = await fetch(url, { credentials: "include" });
  if (!res.ok) throw new Error("시작일과 종료일을 다시 설정해주세요.");
  return res.json();
}
async function getStats(filters) {
  const url = `http://localhost:8080/api/history/stats?${qs({ from: filters.from, to: filters.to, type: filters.type })}`;
  const res = await fetch(url, { credentials: "include" });
  if (!res.ok) throw new Error(`stats:${res.status}`);
  return res.json();
}
async function getSummaryDto(filters) {
  const url = `http://localhost:8080/api/history/summary?${qs({ from: filters.from, to: filters.to })}`;
  const res = await fetch(url, { credentials: "include" });
  if (!res.ok) throw new Error(`summary:${res.status}`);
  return res.json();
}

// 범례 컴포넌트: 파이 순서대로 정렬하여 표시
const LegendWithPercent = ({ payload }) => {
  const sorted = [...(payload ?? [])].sort(
    (a, b) => PIE_ORDER.indexOf(a?.payload?.key) - PIE_ORDER.indexOf(b?.payload?.key)
  );
  const total = sorted.reduce((s, p) => s + (p?.payload?.value ?? 0), 0);
  return (
    <ul style={{ listStyle: "none", padding: 0, margin: 0, width: LEGEND_W }}>
      {sorted.map((e, i) => {
        const v = e?.payload?.value ?? 0;
        const pct = total ? (v / total) * 100 : 0;
        return (
          <li key={i} style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 6 }}>
            <span style={{ width: 14, height: 14, background: e.color }} />
            <span style={{ fontSize: 14 }}>
              {e.value} {v} ({fmt(pct, 1)}%)
            </span>
          </li>
        );
      })}
    </ul>
  );
};

export default function HistoryDashboard() {
  const [filters, setFilters] = useState({
    from: RANGE_MIN,
    to: RANGE_MAX,
    type: "",               // "" = 전체(=매수+매도)
    sort: "date,desc",
    page: 1,
    size: 20,
  });

  const [list, setList] = useState({ items: [], page: 1, size: 20, total: 0 });
  const [stats, setStats] = useState({ total: 0, correct: 0, wrong: 0, unsolved: 0, accuracy: 0 });

  const [summary, setSummary] = useState({
    total: 0,
    buy: 0,
    sell: 0,
    avgAmount: 0,
    totalPnl: 0,
    avgPnl: 0,
    maxPnl: 0,
    minPnl: 0,
  });
  const [summaryLoading, setSummaryLoading] = useState(true);

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setErr("");

    const t = setTimeout(async () => {
      try {
        const l = await getHistory(filters);
        if (!alive) return;
        setList(l);

        const s = await getStats(filters);
        if (!alive) return;
        setStats(s);
      } catch (e) {
        if (!alive) return;
        setErr(e?.message || "데이터 불러오기 실패");
        setStats({ total: 0, correct: 0, wrong: 0, unsolved: 0, accuracy: 0 });
      } finally {
        if (alive) setLoading(false);
      }
    }, 150);

    return () => {
      alive = false;
      clearTimeout(t);
    };
  }, [filters.from, filters.to, filters.type, filters.sort, filters.page, filters.size]);

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        setSummaryLoading(true);
        const s = await getSummaryDto(filters);
        if (!alive) return;
        setSummary(s || {});
      } catch {
        if (!alive) return;
        setSummary({
          total: 0, buy: 0, sell: 0,
          avgAmount: 0, totalPnl: 0, avgPnl: 0, maxPnl: 0, minPnl: 0
        });
      } finally {
        if (alive) setSummaryLoading(false);
      }
    })();
    return () => { alive = false; };
  }, [filters.from, filters.to]);

  const accuracyPct = useMemo(
    () => (stats.total - stats.unsolved > 0 ? stats.accuracy * 100 : 0),
    [stats]
  );

  // 파이 데이터: 항상 '미풀이' 제외. 손익 → 손실 순서 고정.
  const pieData = useMemo(() => {
    return [
      { name: "손익",  value: stats.correct, key: "correct" },
      { name: "손실",  value: stats.wrong,   key: "wrong"   },
    ]
      .filter(d => d.value > 0)
      .sort((a, b) => PIE_ORDER.indexOf(a.key) - PIE_ORDER.indexOf(b.key));
  }, [stats]);

  const COLOR_VAR = {
    correct: "var(--color-correct)",
    wrong: "var(--color-wrong)",
  };

  const setPage = (p) => setFilters((f) => ({ ...f, page: p }));
  const setSize = (s) => setFilters((f) => ({ ...f, size: s, page: 1 }));

  return (
    <div className="hd hd-page">
      <div className="hd-main">
        {/* Left */}
        <div>
          <div className="hd-filters">
            <div>
              <label className="hd-label">시작일</label>
              <input
                type="date"
                min={RANGE_MIN}
                max={filters.to || RANGE_MAX}
                value={filters.from || ""}
                onChange={(e) => {
                  const v = clampDate(e.target.value);
                  setFilters(f => {
                    const from = v;
                    const to = f.to && f.to < from ? from : f.to;
                    return { ...f, from, to, page: 1 };
                  });
                }}
              />
            </div>
            <div>
              <label className="hd-label">종료일</label>
              <input
                type="date"
                min={filters.from || RANGE_MIN}
                max={RANGE_MAX}
                value={filters.to || ""}
                onChange={(e) => {
                  const v = clampDate(e.target.value);
                  setFilters(f => {
                    const to = v;
                    const from = f.from && to < f.from ? to : f.from;
                    return { ...f, from, to, page: 1 };
                  });
                }}
              />
            </div>
            <div>
              <label className="hd-label">타입</label>
              <select
                value={filters.type || ""}
                onChange={(e) => setFilters((f) => ({ ...f, type: e.target.value, page: 1 }))}
              >
                <option value="">전체</option>
                <option value="매수">매수</option>
                <option value="매도">매도</option>
              </select>
            </div>
          </div>

          <div className="hd-card">
            <div className="hd-card-header">
              <div>이력</div>
              <div className="hd-toolbar">
                <select value={filters.size} onChange={(e) => setSize(Number(e.target.value))}>
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                  <option value={50}>50</option>
                </select>
                <select value={filters.sort} onChange={(e) => setFilters((f) => ({ ...f, sort: e.target.value }))}>
                  <option value="date,desc">날짜 최신순</option>
                  <option value="date,asc">날짜 과거순</option>
                </select>
              </div>
            </div>

            <div className="hd-card-body">
              <div className="hd-table-scroll">
                <table className="hd-table">
                  <colgroup>
                    <col className="hd-col" />
                    <col className="hd-col" />
                    <col className="hd-col" />
                    <col className="hd-col" />
                    <col className="hd-col" />
                    <col className="hd-col" />
                    <col className="hd-col" />
                  </colgroup>

                  <thead>
                    <tr>
                      <th>날짜</th>
                      <th>매수/매도</th>
                      <th>금 시세(원/g)</th>
                      <th>거래 수량(g)</th>
                      <th>거래 금액(원)</th>
                      <th className="right">
                        <div style={{ display: "inline-flex", alignItems: "center" }}>
                          <InfoTooltip title="수익률(%)" />
                          <span>수익률(%)</span>
                        </div>
                      </th>
                      <th>손익/손실</th>
                    </tr>
                  </thead>
                  <tbody>
                    {loading ? (
                      <tr>
                        <td colSpan={7} className="hd-empty-cell">불러오는 중</td>
                      </tr>
                    ) : list.items.length === 0 ? (
                      <tr>
                        <td colSpan={7} className="hd-empty-cell">데이터 없음</td>
                      </tr>
                    ) : (
                      list.items.map((r) => (
                        <tr key={r.id}>
                          <td>{r.date}</td>
                          <td>{r.type}</td>
                          <td>{dash(r.goldPrice, 2)}</td>
                          <td>{dash(r.quantity, 3)}</td>
                          <td>{dash(r.amount, 0)}</td>
                          <td className="right">
                            {r.pnl == null ? "-" : (
                              <span className={"pnl-badge " + (r.pnl > 0 ? "pos" : r.pnl < 0 ? "neg" : "zero")}>
                                {fmtPnl(r.pnl)}
                              </span>
                            )}
                          </td>
                          <td>
                            {r.result === "손익" && <span className="hd-result-correct">손익</span>}
                            {r.result === "손실" && <span className="hd-result-wrong">손실</span>}
                            {r.result === "미풀이" && <span className="hd-result-unsolved">미풀이</span>}
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              <div className="hd-row hd-pagination" style={{ marginTop: 12 }}>
                <div className="hd-muted">
                  총 {list.total}건 • {list.page}/{Math.max(1, Math.ceil(list.total / list.size))} 페이지
                </div>
                <div style={{ display: "flex", gap: 8 }}>
                  <button className="hd-btn" onClick={() => setPage(1)} disabled={list.page <= 1}>처음</button>
                  <button className="hd-btn" onClick={() => setPage(list.page - 1)} disabled={list.page <= 1}>이전</button>
                  <button
                    className="hd-btn"
                    onClick={() => setPage(list.page + 1)}
                    disabled={list.page >= Math.ceil(list.total / list.size)}
                  >다음</button>
                  <button
                    className="hd-btn"
                    onClick={() => setPage(Math.max(1, Math.ceil(list.total / list.size)))}
                    disabled={list.page >= Math.ceil(list.total / list.size)}
                  >마지막</button>
                </div>
              </div>

              {err && <div style={{ marginTop: 8, color: "#dc2626", fontSize: 13 }}>{err}</div>}
            </div>
          </div>
        </div>

        {/* Right */}
        <div className="hd-right">
          <div className="hd-card chart-card">
            <div className="hd-card-header">
              <div>손익/손실 분포</div>
              <span className="hd-small">성과율 {fmt(accuracyPct)}%</span>
            </div>
            <div className="hd-card-body">
              <div className="chart-wrapper">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      dataKey="value"
                      data={pieData}
                      innerRadius={0}
                      outerRadius={120}
                      paddingAngle={3}
                      label={false}
                      labelLine={false}
                      stroke="none"
                      cx="45%"
                    >
                      {pieData.map((entry) => (
                        <Cell key={`c-${entry.key}`} fill={COLOR_VAR[entry.key]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(v) => `${v}건`} />
                    <Legend
                      layout="vertical"
                      align="right"
                      verticalAlign="middle"
                      wrapperStyle={{ width: LEGEND_W }}
                      content={<LegendWithPercent />}
                    />
                  </PieChart>
                </ResponsiveContainer>

                {/* 미풀이 제외 안내문(원하면 삭제) */}
                {stats.unsolved > 0 && (
                  <div className="hd-muted" style={{ marginTop: 8, fontSize: 12 }}>
                    미풀이 {stats.unsolved}건은 차트에서 제외됨
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Summary KPI */}
          <div className="hd-card summary-card">
            <div className="hd-card-header">
              <div>성과 요약</div>
              <div className="hd-range">
                {filters.from || RANGE_MIN} ~ {filters.to || RANGE_MAX}
              </div>
            </div>

            <div className="hd-card-body">
              {summaryLoading ? (
                <div className="hd-empty-cell">분석 중</div>
              ) : (
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(3, minmax(0,1fr))",
                    gap: 12,
                    justifyItems: "center",
                    textAlign: "center",
                    alignItems: "center",
                  }}
                >
                  <KPI title="총 거래 횟수" value={`${summary.total ?? 0}건`} />
                  <KPI title="매수/매도 비율" value={`${summary.buy ?? 0} / ${summary.sell ?? 0}`} hint="" />
                  <KPI title="평균 거래 금액" value={won(summary.avgAmount)} />
                  <KPI title="전체 손익" value={`${fmtSign(summary.totalPnl)}%`.replace("  ", " ")} />
                  <KPI
                    title="평균 수익률"
                    value={
                      (summary.avgPnl ?? 0) === 0
                        ? "0.0%"
                        : (summary.avgPnl ?? 0) > 0
                        ? `+${fmt(summary.avgPnl ?? 0, 1)}%`
                        : `${fmt(summary.avgPnl ?? 0, 1)}%`
                    }
                  />
                  <KPI
                    title="최대 이익 / 손실"
                    value={`${(summary.maxPnl ?? 0) > 0 ? "+" : ""}${fmt(summary.maxPnl ?? 0, 1)}% / ${fmt(summary.minPnl ?? 0, 1)}%`}
                  />
                </div>
              )}
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}

function KPI({ title, value, hint }) {
  return (
    <div className="kpi">
      <div className="kpi-title">{title}</div>
      <div className="kpi-value">{value}</div>
      {hint ? <div className="kpi-hint">{hint}</div> : null}
    </div>
  );
}
