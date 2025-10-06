// src/pages/Simulation/Result.jsx
import { useLocation, useNavigate } from "react-router-dom";
import "./Result.css";
import { useState } from "react";
import axios from "axios";

function Result() {
    const location = useLocation();
    const navigate = useNavigate();

    const {
        date = "날짜 정보 없음",
        goldPrice = 0,
        priceChange = 0,
        buyAmount = 0,
        buyValue = 0,
    } = location.state || {};

    const profit = (goldPrice * buyAmount) - buyValue;

    // AI 분석 상태를 관리할 새로운 state 추가
    const [analysis, setAnalysis] = useState(""); // 초기값은 비워둠
    const [isLoading, setIsLoading] = useState(false); // 로딩 상태 추가

    // 버튼 클릭 시 실행될 AI 분석 함수
    const handleAnalysisClick = () => {
        setIsLoading(true); // 로딩 시작
        setAnalysis("AI가 결과를 분석하고 있습니다...");

        const resultText = `
      - 거래 날짜: ${date}
      - 당시 금 시세: ${goldPrice.toLocaleString()} 원/g (${priceChange >= 0 ? '+' : ''}${priceChange}%)
      - 매입 수량: ${buyAmount}g
      - 총 매입 금액: ${buyValue.toLocaleString()} 원
      - 평가 손익: ${profit.toLocaleString()} 원
    `;

        axios.post(
            "/api/ai/analyze", // 프록시 설정을 사용하므로 전체 URL 생략
            { resultText },
            { withCredentials: true }
        )
            .then(response => {
                setAnalysis(response.data.analysis);
            })
            .catch(error => {
                console.error("AI 분석 요청 실패:", error);
                setAnalysis("AI 분석을 가져오는 데 실패했습니다. 백엔드 서버 상태와 API 키를 확인해주세요.");
            })
            .finally(() => {
                setIsLoading(false); // 로딩 종료
            });
    };

    return (
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

                {/* 거래 상세 정보 */}
                <div className="result-detail">
                    <div className="detail-row">
                        <span>매입 수량</span>
                        <strong>{buyAmount} g</strong>
                    </div>
                    <div className="detail-row">
                        <span>총 매입 금액</span>
                        <strong>{buyValue.toLocaleString()} 원</strong>
                    </div>
                </div>

                <hr className="divider" />

                {/* 최종 손익 */}
                <div className="result-detail">
                    <div className="detail-row">
                        <span>평가 손익</span>
                        <strong className={profit >= 0 ? "profit" : "loss"}>
                            {profit.toLocaleString()} 원
                        </strong>
                    </div>
                </div>

                {/* AI 분석 섹션 */}
                <div className="analysis-box">
                    <h3>AI 분석</h3>
                    {analysis ? (
                        <p style={{ whiteSpace: 'pre-wrap' }}>
                            {analysis}
                        </p>
                    ) : (
                        <p>버튼을 눌러 AI 분석을 받아보세요.</p>
                    )}

                    {/* AI 분석 시작 버튼 */}
                    <button
                        className="analysis-btn" // CSS에서 꾸밀 수 있도록 클래스 이름 추가
                        onClick={handleAnalysisClick}
                        disabled={isLoading} // 로딩 중에는 버튼 비활성화
                        style={{marginTop: '15px', padding: '10px 15px', cursor: 'pointer'}}
                    >
                        {isLoading ? "분석 중..." : "AI 분석하기"}
                    </button>
                </div>

                {/* 하단 버튼 */}
                <div className="btn-group">
                    <button className="back-btn" onClick={() => navigate(-1)}>뒤로 가기</button>
                    <button className="trade-btn" onClick={() => navigate("/simulation")}>
                        다른 날짜로 시작하기
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Result;