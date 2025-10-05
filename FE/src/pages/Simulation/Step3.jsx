import { useLocation, useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import "./Step3.css";
import { useState, useEffect } from "react";
import axios from "axios";

function Step3() {
    const location = useLocation();
    const navigate = useNavigate();

    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const isAuthed = !!user.memberId;

    console.log("=== Step3 페이지 진입 ===");
    console.log("로그인 여부:", isAuthed ? "로그인 O" : "로그인 X");
    console.log("유저 정보:", user);

    const selectedDate = location.state?.date;
    const formattedDate = selectedDate
        ? new Date(selectedDate).toLocaleDateString("ko-KR")
        : "2024. 12. 23.";

    const [balance, setBalance] = useState(isAuthed ? user.balance ?? 0 : 600000);
    const [ownedGold, setOwnedGold] = useState(0);
    const [goldPrice, setGoldPrice] = useState(0);
    const [priceChange, setPriceChange] = useState(0.0);

    // ✅ BE에서 balance 가져오기
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

    // ✅ 금 시세 불러오기
    useEffect(() => {
        if (!selectedDate) return;

        const end = new Date(selectedDate);
        const start = new Date(end);
        start.setDate(start.getDate() - 30);

        axios
            .get("/api/metrics/series", {
                params: {
                    metric: "krw_g_close",
                    from: start.toISOString().split("T")[0],
                    to: end.toISOString().split("T")[0],
                },
            })
            .then((res) => {
                if (Array.isArray(res.data) && res.data.length > 0) {
                    const last = res.data[res.data.length - 1];
                    setGoldPrice(last.value ?? 0);
                }
            })
            .catch((err) => console.error("❌ 금 시세 불러오기 실패:", err));
    }, [selectedDate]);

    // ✅ 입력 상태 (문자열로 관리)
    const [buyAmount, setBuyAmount] = useState("");
    const [sellAmount, setSellAmount] = useState("");

    const buyNum = parseFloat(buyAmount) || 0;
    const sellNum = parseFloat(sellAmount) || 0;

    const buyValue = buyNum * goldPrice;
    const sellValue = sellNum * goldPrice;
    const expectedBalance = balance - buyValue + sellValue;

    // ✅ 거래 버튼 로직
    const handleTrade = async () => {
        if (buyNum < 0 || sellNum < 0) {
            alert("음수 값은 입력할 수 없습니다.");
            return;
        }

        if (buyValue > balance) {
            alert("보유 자산보다 많은 금액은 매수할 수 없습니다.");
            return;
        }

        if (sellNum > ownedGold) {
            alert(`보유 금(${ownedGold}g)보다 많이 매도할 수 없습니다.`);
            return;
        }

        const newBalance = balance - buyValue + sellValue;
        const newOwnedGold = ownedGold + buyNum - sellNum;

        setBalance(newBalance);
        setOwnedGold(newOwnedGold);

        const tradeData = {
            memberNo: user.memberNo,
            date: formattedDate,
            buyAmount: buyNum,
            sellAmount: sellNum,
            buyValue,
            sellValue,
            balance: newBalance,
            goldPrice,
            priceChange,
            ownedGold: newOwnedGold,
        };

        console.log("=== 거래 버튼 클릭 ===");
        console.log("거래 데이터:", tradeData);

        try {
            // ✅ 서버에 거래 데이터 저장 요청
            const res = await axios.post("/api/trade/record", tradeData);
            console.log("✅ 거래 저장 성공:", res.data);
            alert("거래가 성공적으로 저장되었습니다.");

            // ✅ 결과 페이지로 이동
            navigate("/simulation/result", { state: tradeData });
        } catch (err) {
            console.error("❌ 거래 저장 실패:", err);
            alert("거래 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    };

    return (
        <>
            <Navigation
                isAuthed={isAuthed}
                memberId={user.memberId}
                memberName={user.memberName}
                memberEmail={user.memberEmail}
                balance={balance}
                onLogout={() => {
                    localStorage.removeItem("user");
                    window.location.reload();
                }}
            />

            <div className="step3-container">
                <div className="step3-box">
                    <h1 className="step3-title">주문하기</h1>

                    {/* 상단 요약 */}
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
                            <h3>
                                {goldPrice ? goldPrice.toLocaleString() : "로딩 중..."} 원/g
                            </h3>
                        </div>
                    </div>

                    {/* 💰 금 계산기 */}
                    <div className="calc-box">
                        <h2>금 시세 시뮬레이터</h2>

                        <div className="order-box">
                            <div className="order-section">
                                <h3 style={{ color: "#007bff" }}>매수</h3>
                                <input
                                    type="text"
                                    inputMode="decimal"
                                    placeholder="매수 g 입력"
                                    value={buyAmount}
                                    onChange={(e) => setBuyAmount(e.target.value)}
                                    onFocus={(e) => e.target.select()}
                                />
                                <p className="price-text">
                                    = {buyValue ? buyValue.toLocaleString() : 0} 원
                                </p>
                            </div>

                            <div className="order-section">
                                <h3 style={{ color: "#dc3545" }}>매도</h3>
                                <input
                                    type="text"
                                    inputMode="decimal"
                                    placeholder="매도 g 입력"
                                    value={sellAmount}
                                    onChange={(e) => setSellAmount(e.target.value)}
                                    onFocus={(e) => e.target.select()}
                                />
                                <p className="price-text">
                                    = {sellValue ? sellValue.toLocaleString() : 0} 원
                                </p>
                            </div>
                        </div>

                        <div className="balance-box">
                            <p>
                                💰 보유 자산 변동 예상:{" "}
                                <strong>{balance.toLocaleString()} 원</strong> →{" "}
                                <strong>{expectedBalance.toLocaleString()} 원</strong>
                            </p>
                            <p style={{ color: "#555", fontSize: "14px" }}>
                                🪙 현재 보유 금: {ownedGold.toFixed(2)} g
                            </p>
                        </div>
                    </div>

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