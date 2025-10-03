import { useLocation, useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import MetricMini from "../../components/MetricMini";
import MetricCard from "../../components/MetricCard";
import "./Step2.css";

export default function Step2() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const selectedDate = state?.date;

  const [articles, setArticles] = useState([]);
  const [active, setActive] = useState(null); // { metric, title }

  const fmt = (dObj) => {
    const y = dObj.getFullYear();
    const m = String(dObj.getMonth() + 1).padStart(2, "0");
    const d = String(dObj.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  };

  useEffect(() => {
    if (!selectedDate) return;
    const dbDate = fmt(new Date(selectedDate));
    axios.get(`http://localhost:8080/api/news/${dbDate}`, { withCredentials: true })
      .then((r) => setArticles(r.data ?? []))
      .catch(() => setArticles([]));
  }, [selectedDate]);

  const titleDate = useMemo(
    () => (selectedDate ? new Date(selectedDate).toLocaleDateString("ko-KR") : ""),
    [selectedDate]
  );

  const handleNext = () => navigate("/simulation/step3", { state: { date: selectedDate } });

  return (
    <>
      <div className="step2 page-with-docks">
        {/* 좌측 도크: 3개 */}
        <aside className="side-dock left">
          <div className="dock-inner">
            <MetricMini title="환율(원/USD)"  metric="fx_rate"    selectedDate={selectedDate}
              onClick={() => setActive({ metric: "fx_rate", title: "환율(원/USD)" })} />
            <MetricMini title="VIX(pt)"        metric="vix"        selectedDate={selectedDate}
              onClick={() => setActive({ metric: "vix", title: "VIX(pt)" })} />
            <MetricMini title="ETF 거래량(주)" metric="etf_volume" selectedDate={selectedDate}
              onClick={() => setActive({ metric: "etf_volume", title: "ETF 거래량(주)" })} />
          </div>
        </aside>

        {/* 우측 도크: 2개 */}
        <aside className="side-dock right">
          <div className="dock-inner">
            <MetricMini title="금 시세(원/g)"  metric="krw_g_close" selectedDate={selectedDate}
              onClick={() => setActive({ metric: "krw_g_close", title: "금 시세(원/g)" })} />
            <MetricMini title="LSTM 예측(원/g)" metric="pred_close" selectedDate={selectedDate}
              onClick={() => setActive({ metric: "pred_close", title: "LSTM 예측(원/g)" })} />
          </div>
        </aside>

        {/* 본문 */}
        <h1 className="step2-title">{titleDate} 뉴스 기사</h1>
        <p className="step2-subtitle">기사를 읽고 매수할지 매도할지 선택해보세요!</p>

        <div className="article-list">
          {articles?.length ? (
            articles.map((news, i) => (
              <div key={i} className="article-card">
                <h3>{news.articleTitle}</h3>
                <p>{news.summaryFull ? news.summaryFull : news.articleContent}</p>
                <a href={news.url} target="_blank" rel="noreferrer">원문 보기</a>
              </div>
            ))
          ) : (
            <p>뉴스가 없습니다.</p>
          )}
        </div>

        <button className="step2-btn" onClick={handleNext}>매수 혹은 매도 하러가기</button>
      </div>

      {active && (
        <MetricCard
          title={active.title}
          metric={active.metric}
          selectedDate={selectedDate}
          onClose={() => setActive(null)}
          defaultUnit="1m"
        />
      )}
    </>
  );
}
