import { useLocation, useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import { useEffect, useState } from "react";
import axios from "axios";
import "./Step2.css";

function Step2() {
  const location = useLocation();
  const navigate = useNavigate();
  const selectedDate = location.state?.date;

  const [articles, setArticles] = useState([]);

  // 날짜를 DB와 맞는 포맷으로 변환 (YYYYMMDD)
  const formatDateForDB = (dateObj) => {
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, "0");
    const day = String(dateObj.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;  // ← 하이픈 포함
  };
  useEffect(() => {
    if (selectedDate) {
      const dbDate = formatDateForDB(new Date(selectedDate));
      axios.get(`http://localhost:8080/api/news/${dbDate}`)
        .then((res) => {
          setArticles(res.data);
        })
        .catch((err) => {
          console.error("뉴스 가져오기 실패:", err);
        });
    }
  }, [selectedDate]);

  const handleNext = () => {
    navigate("/simulation/step3", { state: { date: selectedDate } });
  };

  return (
    <>
      <Navigation />
      <div className="step2">
        <h1 className="step2-title">
          {selectedDate ? new Date(selectedDate).toLocaleDateString("ko-KR") : ""} 뉴스 기사
        </h1>
        <p className="step2-subtitle">
          기사를 읽고 매수할지 매도할지 선택해보세요!
        </p>
        <div className="article-list">
          {articles.length > 0 ? (
            articles.map((news, index) => (
              <div key={index} className="article-card">
                <h3>{news.articleTitle}</h3>
                <p>{news.summaryFull ? news.summaryFull : news.articleContent}</p>
                <a href={news.url} target="_blank" rel="noreferrer">원문 보기</a>
              </div>
            ))
          ) : (
            <p>뉴스가 없습니다.</p>
          )}
        </div>
        <button className="step2-btn" onClick={handleNext}>
          매수 혹은 매도 하러가기
        </button>
      </div>
    </>
  );
}

export default Step2;
