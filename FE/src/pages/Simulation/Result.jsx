// src/pages/Simulation/Result.jsx
import { useLocation, useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import "./Result.css";
import { useState } from "react";
import axios from "axios";

function Result() {
  const location = useLocation();
  const navigate = useNavigate();

  const {
    date = "2024. 12. 17.",
    goldPrice = 152860,
    priceChange = 0.9,
    buyAmount = 3.5,
    buyValue = 573225,
    balance = 600000,
  } = location.state || {};

  const buyUnitPrice = 151500;
  const currentValue = goldPrice * buyAmount;
  const profit = currentValue - buyValue;

  const [analysis, setAnalysis] = useState("");
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisError, setAnalysisError] = useState("");

  const handleAnalysisClick = () => {
    if (isAnalyzing) return;
    setIsAnalyzing(true);
    setAnalysis("");
    setAnalysisError("");

    const resultText = `
      - 거래 날짜: ${date}
      - 당시 금 시세: ${goldPrice.toLocaleString()} 원/g (${priceChange > 0 ? '+' : ''}${priceChange}%)
      - 매입 수량: ${buyAmount}g
      - 총 매입 금액: ${buyValue.toLocaleString()} 원
      - 평가 손익: ${profit.toLocaleString()} 원
    `;

    axios.post("http://localhost:8080/api/gpt/analyze", { resultText })
      .then(response => {
        setAnalysis(response.data.analysis);
      })
      .catch(error => {
        console.error("AI 분석 요청 실패:", error);
        setAnalysisError("AI 분석을 가져오는 데 실패했습니다. 잠시 후 다시 시도해주세요.");
      })
      .finally(() => {
        setIsAnalyzing(false);
      });
  };

  return (
    <>
      <Navigation />
      <div className="result-container">
        <div className="result-box">
          <h1 className="result-title">거래 결과</h1>

          <div className="result-summary">
            <p className="result-date">{date} 금 시세</p>
            <h2 className="result-price">
              {goldPrice.toLocaleString()} 원/g
              <span className={`price-change ${priceChange >= 0 ? "up" : "down"}`}>
                {priceChange >= 0 ? "▲" : "▼"} {Math.abs(priceChange)}%
              </span>
            </h2>
          </div>

          {/* ✅ AI 분석 섹션 UI 수정 */}
          <div className="analysis-box">
            <h3>AI 분석</h3>
            {analysis ? (
              // Case 1: 분석 성공 시, 결과 텍스트만 보여줌
              <p>{analysis}</p>
            ) : isAnalyzing ? (
              // Case 2: 분석 중일 때, 로딩 메시지를 보여줌
              <p>AI가 결과를 분석하고 있습니다...</p>
            ) : analysisError ? (
              // Case 3: 분석 실패 시, 에러 메시지와 '다시 시도' 버튼을 보여줌
              <>
                <p style={{ color: 'red' }}>{analysisError}</p>
                <button className="trade-btn" onClick={handleAnalysisClick}>
                  다시 분석하기
                </button>
              </>
            ) : (
              // Case 4: 초기 상태일 때, '분석하기' 버튼만 보여줌
              <button className="trade-btn" onClick={handleAnalysisClick}>
                결과 분석하기
              </button>
            )}
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