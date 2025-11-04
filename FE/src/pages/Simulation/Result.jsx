// src/pages/Simulation/Result.jsx
import { useLocation, useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import "./Result.css";
import { useState, useEffect } from "react"; // useEffect 추가
import axios from "axios";

function Result() {
    const location = useLocation();
    const navigate = useNavigate();
    const { state } = location; // Step3에서 보낸 state 객체

    // --- 1. 데이터 유효성 검사 ---
    // state가 없으면(ex. 새로고침) Step3로 돌려보냄
    useEffect(() => {
        if (!state) {
            alert("거래 정보가 없습니다. 시뮬레이션 페이지로 돌아갑니다.");
            navigate("/simulation/step3", { replace: true });
        }
    }, [state, navigate]);

    // --- 2. state에서 실제 데이터 추출 ---
    const {
        tradeDate,            // "2023-01-01"
        tradeType,            // "매수" or "매도"
        goldPrice,            // 거래 당시 시세
        quantity,             // 거래 수량(g)
        pnl,                  // 실현 손익률(%) (매도일 때만 의미 있음)
        newBalanceFromServer, // 거래 후 잔액
    } = state || {}; // state가 null일 경우를 대비한 기본값

    // --- 3. 파생 데이터 계산 ---
    const formattedDate = tradeDate ? new Date(tradeDate).toLocaleDateString("ko-KR") : "알 수 없음";
    const totalAmount = (goldPrice || 0) * (quantity || 0); // 총 거래 금액
    const isSellTrade = tradeType === "매도"; // 매도 거래 여부

    // (참고: priceChange(% 변동률) 값은 Step3에서 넘어오지 않았습니다.
    // 이 값을 표시하려면 Step3에서 금 시세 조회 시 전일 대비 변동률도 함께 가져와야 합니다.)

    // --- AI 분석 관련 상태 ---
    const [analysis, setAnalysis] = useState("");
    const [isAnalyzing, setIsAnalyzing] = useState(false);
    const [analysisError, setAnalysisError] = useState("");

    const handleAnalysisClick = () => {
        if (isAnalyzing || !state) return; // state 없으면 실행 방지
        setIsAnalyzing(true);
        setAnalysis("");
        setAnalysisError("");

        // --- 4. AI 분석용 텍스트를 실제 데이터로 구성 ---
        const resultText = `
      - 거래 날짜: ${formattedDate}
      - 거래 타입: ${tradeType}
      - 체결 당시 금 시세: ${goldPrice?.toLocaleString()} 원/g
      - 거래 수량: ${quantity} g
      - 총 거래 금액: ${totalAmount.toLocaleString()} 원
      ${isSellTrade ? `- 실현 손익률(ROI): ${pnl?.toFixed(2)}%` : ''}
      - 거래 후 보유 자산: ${newBalanceFromServer?.toLocaleString()} 원
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

    // --- 5. 렌더링 ---
    // state가 없으면(ex. 새로고침) 렌더링을 피함
    if (!state) {
        return (
            <>
                <Navigation />
                <div className="result-container">
                    <p>거래 정보를 불러오는 중이거나, 잘못된 접근입니다...</p>
                </div>
            </>
        );
    }

    return (
        <>
            <div className="result-container">
                <div className="result-box">
                    <h1 className="result-title">
                        {tradeType === "매수" ? "매수 완료" : "매도 완료"}
                    </h1>

                    <div className="result-summary">
                        <p className="result-date">{formattedDate} 거래 기준</p>
                        <h2 className="result-price">
                            {goldPrice.toLocaleString()} 원/g
                            {/* priceChange가 없으므로 해당 UI는 제거합니다.
                <span className={`price-change ${priceChange >= 0 ? "up" : "down"}`}>
                  ...
                </span>
              */}
                        </h2>
                    </div>

                    {/* AI 분석 섹션 */}
                    <div className="analysis-box">
                        <h3>AI 분석</h3>
                        {analysis ? (
                            <p>{analysis}</p>
                        ) : isAnalyzing ? (
                            <p>AI가 결과를 분석하고 있습니다...</p>
                        ) : analysisError ? (
                            <>
                                <p style={{ color: 'red' }}>{analysisError}</p>
                                <button className="trade-btn" onClick={handleAnalysisClick}>
                                    다시 분석하기
                                </button>
                            </>
                        ) : (
                            <button className="trade-btn" onClick={handleAnalysisClick}>
                                거래 결과 분석하기
                            </button>
                        )}
                    </div>

                    {/* --- 6. 실제 거래 내역 표시 --- */}
                    <div className="result-detail">
                        <div className="detail-row">
                            <span>거래 종류</span>
                            <strong>{tradeType}</strong>
                        </div>
                        <div className="detail-row">
                            <span>거래 수량</span>
                            <strong>{quantity} g</strong>
                        </div>
                        <div className="detail-row">
                            <span>체결 단가</span>
                            <strong>{goldPrice.toLocaleString()} 원/g</strong>
                        </div>
                    </div>

                    <hr className="divider" />

                    <div className="result-detail">
                        <div className="detail-row">
                            <span>총 거래 금액</span>
                            <strong>{totalAmount.toLocaleString()} 원</strong>
                        </div>

                        {/* 매도일 경우에만 실현 손익률(pnl) 표시 */}
                        {isSellTrade && pnl !== undefined && (
                            <div className="detail-row">
                                <span>실현 손익률 (ROI)</span>
                                <strong className={(pnl || 0) >= 0 ? "profit" : "loss"}>
                                    {pnl?.toFixed(2) ?? 0.00} %
                                </strong>
                            </div>
                        )}

                        <div className="detail-row">
                            <span>거래 후 보유 자산</span>
                            <strong>{newBalanceFromServer.toLocaleString()} 원</strong>
                        </div>
                    </div>

                    <div className="btn-group">
                        <button className="back-btn" onClick={() => navigate("/simulation")}>
                            시뮬레이션 홈
                        </button>
                        <button className="trade-btn" onClick={() => navigate("/simulation/step1")}>
                            다른 날짜로 거래하기
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}

export default Result;