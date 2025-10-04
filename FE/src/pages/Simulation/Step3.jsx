// src/pages/Simulation/Step3.jsx
import { useLocation, useNavigate } from "react-router-dom";
import "./Step3.css";
import { useState, useEffect } from "react";
import axios from "axios";

function Step3() {
  const location = useLocation();
  const navigate = useNavigate();

  // ✅ 로그인 정보 가져오기
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isAuthed = !!user.memberId; // 로그인 여부

  // ✅ 페이지 진입 로그
  console.log("=== Step3 페이지 진입 ===");
  console.log("로그인 여부:", isAuthed ? "로그인 O" : "로그인 X");
  console.log("유저 정보:", user);

  // 날짜
  const selectedDate = location.state?.date;
  const formattedDate = selectedDate
    ? new Date(selectedDate).toLocaleDateString("ko-KR")
    : "2024. 9. 26.";

  // ✅ 상태: balance를 state로 관리
  const [balance, setBalance] = useState(isAuthed ? user.balance ?? 0 : 600000);

  // ✅ 로그인된 경우, BE에서 최신 balance 가져오기
  useEffect(() => {
    if (isAuthed && user.memberNo) {
      axios
        .get(`/api/asset/${user.memberNo}`)
        .then((res) => {
          console.log("서버에서 불러온 balance:", res.data);
          setBalance(res.data);
        })
        .catch((err) => {
          console.error("자산 불러오기 실패:", err);
        });
    }
  }, [isAuthed, user.memberNo]);

  // 금 시세 (임시 하드코딩 → BE gold_price 연동 예정)
  const goldPrice = 152860;
  const priceChange = 0.9;

  // 상태
  const [buyAmount, setBuyAmount] = useState(0);
  const [sellAmount, setSellAmount] = useState(0);

  const buyValue = buyAmount * goldPrice;
  const sellValue = sellAmount * goldPrice;

  // 거래 버튼
  const handleTrade = () => {
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

    console.log("=== 거래 버튼 클릭 ===");
    console.log("거래 데이터:", tradeData);

    if (isAuthed) {
      console.log("로그인 O → 결과 저장 API 호출 예정");
    } else {
      console.log("로그인 X → 결과 저장 불가 (체험 모드)");
    }

    navigate("/simulation/result", { state: tradeData });
  };

  return (
    <>
        isAuthed={isAuthed}
        memberId={user.memberId}
        memberName={user.memberName}
        memberEmail={user.memberEmail}
        balance={balance}
        onLogout={() => {
          localStorage.removeItem("user");
          window.location.reload();
        }}
      <div className="step3-container">
        <div className="step3-box">
          <h1 className="step3-title">주문하기</h1>

          {/* 요약 */}
          <div className="summary-box">
            <div className="summary-item">
              <p>보유 자산</p>
              <h3>{balance.toLocaleString()} 원</h3>
              {!isAuthed && (
                <p style={{ color: "#888", fontSize: "12px" }}>(체험 모드)</p>
              )}
            </div>
            <div className="summary-item">
              <p>{formattedDate} 금 시세</p>
              <h3>{goldPrice.toLocaleString()} 원/g</h3>
              <span
                className={`price-change ${
                  priceChange >= 0 ? "up" : "down"
                }`}
              >
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
                onChange={(e) => setBuyAmount(Number(e.target.value))}
                placeholder="0"
              />
              <span>g =</span>
              <p>{buyValue.toLocaleString()} 원</p>
            </div>
            <div className="calc-row">
              <input
                type="number"
                value={sellAmount}
                onChange={(e) => setSellAmount(Number(e.target.value))}
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
                onChange={(e) => setBuyAmount(Number(e.target.value))}
              />
              <button className="buy-btn">매수하기</button>
            </div>
            <div className="order-section">
              <h3>매도</h3>
              <input
                type="number"
                placeholder="매도 g 입력"
                value={sellAmount}
                onChange={(e) => setSellAmount(Number(e.target.value))}
              />
              <button className="sell-btn">매도하기</button>
            </div>
          </div>

          {/* 버튼 */}
          <div className="btn-group">
            <button className="back-btn" onClick={() => navigate(-1)}>
              뒤로 가기
            </button>
            <button className="trade-btn" onClick={handleTrade}>
              거래 하기
            </button>
          </div>
        </div>
      </div>
    </>
  );
}

export default Step3;
