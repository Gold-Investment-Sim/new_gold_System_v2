// src/pages/Simulation/Step3.jsx
import { useLocation, useNavigate } from "react-router-dom";
import "./Step3.css";
import { useState } from "react";

function Step3() {
  const location = useLocation();
  const navigate = useNavigate();

  const selectedDate = location.state?.date;
  const formattedDate = selectedDate
    ? new Date(selectedDate).toLocaleDateString("ko-KR")
    : "2024. 9. 26.";

  const balance = 600000;
  const goldPrice = 152860;
  const priceChange = 0.9;

  const [buyAmount, setBuyAmount] = useState(0);
  const [sellAmount, setSellAmount] = useState(0);

  const buyValue = buyAmount * goldPrice;
  const sellValue = sellAmount * goldPrice;

  const handleTrade = () => {
    // 거래 결과 데이터
    const tradeData = {
      date: formattedDate,
      buyAmount,
      sellAmount,
      buyValue,
      sellValue,
      balance,
      goldPrice,
      priceChange,
    };

    // Result 페이지로 이동하면서 state 전달
    navigate("/simulation/result", { state: tradeData });
  };

  return (
    <>
      <div className="step3-container">
        <div className="step3-box">
          <h1 className="step3-title">주문하기</h1>

          {/* 요약 */}
          <div className="summary-box">
            <div className="summary-item">
              <p>보유 자산</p>
              <h3>{balance.toLocaleString()} 원</h3>
            </div>
            <div className="summary-item">
              <p>{formattedDate} 금 시세</p>
              <h3>{goldPrice.toLocaleString()} 원/g</h3>
              <span className={`price-change ${priceChange >= 0 ? "up" : "down"}`}>
                {priceChange >= 0 ? "▲" : "▼"} {Math.abs(priceChange)}%
              </span>
            </div>
          </div>

          {/* 계산기 */}
          <div className="calc-box">
            <h2>금 계산기</h2>
            <div className="calc-row">
              <input
                type="number"
                value={buyAmount}
                onChange={(e) => setBuyAmount(e.target.value)}
                placeholder="0"
              />
              <span>g =</span>
              <p>{buyValue.toLocaleString()} 원</p>
            </div>
            <div className="calc-row">
              <input
                type="number"
                value={sellAmount}
                onChange={(e) => setSellAmount(e.target.value)}
                placeholder="0"
              />
              <span>g =</span>
              <p>{sellValue.toLocaleString()} 원</p>
            </div>
          </div>

          {/* 매수/매도 */}
          <div className="order-box">
            <div className="order-section">
              <h3>매수</h3>
              <input
                type="number"
                placeholder="매수 g 입력"
                value={buyAmount}
                onChange={(e) => setBuyAmount(e.target.value)}
              />
              <button className="buy-btn">매수하기</button>
            </div>
            <div className="order-section">
              <h3>매도</h3>
              <input
                type="number"
                placeholder="매도 g 입력"
                value={sellAmount}
                onChange={(e) => setSellAmount(e.target.value)}
              />
              <button className="sell-btn">매도하기</button>
            </div>
          </div>

          {/* 버튼 */}
          <div className="btn-group">
            <button className="back-btn" onClick={() => navigate(-1)}>뒤로 가기</button>
            <button className="trade-btn" onClick={handleTrade}>거래 하기</button>
          </div>
        </div>
      </div>
    </>
  );
}

export default Step3;
