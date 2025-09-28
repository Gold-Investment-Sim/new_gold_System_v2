// src/pages/Simulation/Result.jsx
import { useLocation, useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import "./Result.css";
import { useEffect, useState } from "react";
import axios from "axios";

function Result() {
  const location = useLocation();
  const navigate = useNavigate();

  // Step3에서 전달받은 값 (더미 fallback 추가)
  const {
    date = "2024. 12. 17.",
    goldPrice = 152860,
    priceChange = 0.9,
    buyAmount = 3.5,
    buyValue = 573225,
    balance = 600000,
  } = location.state || {};

  // 더미 계산
  const buyUnitPrice = 151500; // 매입 단가
  const currentValue = goldPrice * buyAmount; // 현재 시세 금액
  const profit = currentValue - buyValue; // 손익


  const [analysis, setAnalysis] = useState("AI가 결과를 분석하고 있습니다...");

  useEffect(() => {
    const resultText = `
      - 거래 날짜: ${date}
      - 당시 금 시세: ${goldPrice.toLocaleString()} 원/g (${priceChange > 0 ? '+' : ''}${priceChange}%)
      - 매입 수량: ${buyAmount}g
      - 총 매입 금액: ${buyValue.toLocaleString()} 원
      - 평가 손익: ${profit.toLocaleString()} 원
    `;

    // 백엔드의 API 주소만 /api/gemini/analyze로 변경합니다.
    axios.post("http://localhost:8080/api/gemini/analyze", { resultText })
      .then(response => {
        setAnalysis(response.data.analysis);
      })
      .catch(error => {
        console.error("AI 분석 요청 실패:", error);
        setAnalysis("AI 분석을 가져오는 데 실패했습니다.");
      });
  }, []);
  return (
    <>
      <Navigation />
      <div className="result-container">
        <div className="result-box">
          <h1 className="result-title">거래 결과</h1>

          {/* 시세 요약 */}
          <div className="result-summary">
            <p className="result-date">{date} 금 시세</p>
            <h2 className="result-price">
              {goldPrice.toLocaleString()} 원/g
              <span className={`price-change ${priceChange >= 0 ? "up" : "down"}`}>
                {priceChange >= 0 ? "▲" : "▼"} {Math.abs(priceChange)}%
              </span>
            </h2>
          </div>
          <div className="analysis-box">
            <h3>AI 분석</h3>
            <p>
              {analysis}
            </p>
          </div>
          <div className="result-detail">
            <div className="detail-row">
              <span>매입 단가</span>
              <strong>{buyUnitPrice.toLocaleString()} 원</strong>
            </div>
            <div className="detail-row">
              <span>매입 수량</span>
              <strong>{buyAmount} g</strong>
            </div>
            <div className="detail-row">
              <span>현재 시세</span>
              <strong>{currentValue.toLocaleString()} 원</strong>
            </div>
          </div>

          <hr className="divider" />

          <div className="result-detail">
            <div className="detail-row">
              <span>평가 금액</span>
              <strong>{buyValue.toLocaleString()} 원</strong>
            </div>
            <div className="detail-row">
              <span>손익</span>
              <strong className={profit >= 0 ? "profit" : "loss"}>
                {profit.toLocaleString()} 원
              </strong>
            </div>
          </div>

          {/* GPT 분석 섹션 자리 */}
          <div className="analysis-box">
            <h3>AI 분석</h3>
            <p>
              이번 거래 결과는 <strong>{priceChange}%</strong> 변동의 영향을 받았습니다.
              금 가격이 최근 글로벌 금리와 경제 지표로 인해 움직인 것으로 보입니다.
              (→ 나중에 GPT 분석 결과로 교체)
            </p>
          </div>

          {/* 버튼 */}
          <div className="btn-group">
            <button className="back-btn" onClick={() => navigate(-1)}>뒤로 가기</button>
            <button className="trade-btn" onClick={() => navigate("/simulation/step3")}>
              다시 거래하기
            </button>
          </div>
        </div>
      </div>
    </>
  );
}

export default Result;
