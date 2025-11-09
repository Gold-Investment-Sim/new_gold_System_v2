import {useLocation, useNavigate} from "react-router-dom";
import Navigation from "../../components/Navigation";
import "./Step3.css";
import {useState, useEffect} from "react";
import axios from "axios";

const fmt = (dObj) => {
    const d = new Date(dObj); //
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const D = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${D}`;
};

function Step3() {
    const location = useLocation();
    const navigate = useNavigate();

    // ✅ 로그인 상태 확인
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const isAuthed = !!user.memberId;

    console.log("=== Step3 페이지 진입 ===");
    console.log("로그인 여부:", isAuthed ? "로그인 O" : "로그인 X");
    console.log("유저 정보:", user);

    // ✅ 선택한 날짜 정보
    const selectedDate = location.state?.date;
    const formattedDate = selectedDate
        ? new Date(selectedDate).toLocaleDateString("ko-KR")
        : "2024. 12. 23.";

    // ✅ 주요 상태 관리
    const [balance, setBalance] = useState(isAuthed ? user.balance ?? 0 : 600000);
    const [ownedGold, setOwnedGold] = useState(0);
    const [goldPrice, setGoldPrice] = useState(0);


    useEffect(() => {
        if (!selectedDate) return;

        const end = new Date(selectedDate);
        const start = new Date(end);
        start.setDate(start.getDate() - 30);

        axios
            .get("/api/metrics/series", {
                params: {
                    metric: "krw_g_close",
                    from: fmt(start), //
                    to: fmt(end),     //
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
    // ✅ (1) 현재 잔액 DB에서 불러오기
    useEffect(() => {
        if (isAuthed && user.memberNo) {
            axios
                .get(`/api/asset/${user.memberNo}`)
                .then((res) => {
                    console.log("서버에서 불러온 balance:", res.data);
                    if (res.data) setBalance(res.data.balance ?? res.data ?? 0);
                })
                .catch((err) => console.error("자산 불러오기 실패:", err));
        }
    }, [isAuthed, user.memberNo]);

    // ✅ (2) 현재 보유 금(g) 불러오기
    useEffect(() => {
        if (isAuthed && user.memberNo) {
            axios
                .get(`/api/trade/owned/${user.memberNo}`)
                .then((res) => {
                    if (res.data && res.data.ownedGold !== undefined) {
                        setOwnedGold(res.data.ownedGold);
                    }
                })
                .catch((err) => console.error("보유 금량 불러오기 실패:", err));
        }
    }, [isAuthed, user.memberNo]);

    // ✅ (3) 금 시세 불러오기
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

    // ✅ 입력 상태
    const [buyAmount, setBuyAmount] = useState("");
    const [sellAmount, setSellAmount] = useState("");

    const buyNum = parseFloat(buyAmount) || 0;
    const sellNum = parseFloat(sellAmount) || 0;
    const buyValue = buyNum * goldPrice;
    const sellValue = sellNum * goldPrice;
    const expectedBalance = balance - buyValue + sellValue;

    // ✅ 거래 버튼 클릭
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

        // 거래 데이터
        const tradeData = {
            memberNo: user.memberNo,
            tradeType: buyNum > 0 ? "매수" : "매도",
            goldPrice,
            quantity: buyNum > 0 ? buyNum : sellNum,
            currentBalance: balance,
            predict: "예측없음",
            tradeDate: fmt(selectedDate)
        };

        console.log("=== 거래 버튼 클릭 ===");
        console.log("거래 데이터:", tradeData);

        try {
            const res = await axios.post("/api/trade/record", tradeData);

            console.log("✅ 거래 저장 성공:", res.data);
            const newBalanceFromServer = res.data.newBalance ?? balance;
            const ownedGoldFromServer =
                res.data.ownedGold !== undefined ? res.data.ownedGold : ownedGold;

            // ✅ (수정) 로컬스토리지 업데이트
            const updatedUser = {...user, balance: newBalanceFromServer};
            localStorage.setItem("user", JSON.stringify(updatedUser));

            // ✅ (수정) 상태 업데이트 (pnl 추가)
            setBalance(newBalanceFromServer);
            setOwnedGold(ownedGoldFromServer);

            alert("거래가 성공적으로 저장되었습니다.");

            // ✅ (수정) navigate로 이동 시 pnl을 포함한 모든 데이터를 state로 전달
            navigate("/simulation/result", {
                state: {
                    ...tradeData, // memberNo, tradeType, goldPrice, quantity, tradeDate 등
                    newBalanceFromServer: newBalanceFromServer,
                    ownedGoldFromServer: ownedGoldFromServer,
                    pnl: res.data.pnl, // <-- 🚨 이 부분이 누락되었습니다.
                },
            });
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
                                <p style={{color: "#888", fontSize: "12px"}}>(체험 모드)</p>
                            )}
                        </div>
                        <div className="summary-item">
                            <p>{formattedDate} 금 시세</p>
                            <h3>
                                {goldPrice ? goldPrice.toLocaleString() : "로딩 중..."} 원/g
                            </h3>
                        </div>
                    </div>

                    {/* 💰 금 시세 시뮬레이터 */}
                    <div className="calc-box">
                        <h2>금 시세 시뮬레이터</h2>

                        <div className="order-box">
                            <div className="order-section">
                                <h3 style={{color: "#007bff"}}>매수</h3>
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
                                <h3 style={{color: "#dc3545"}}>매도</h3>
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

                        {/* 보유 자산 변화 */}
                        <div className="balance-box">
                            <p>
                                💰 보유 자산 변동 예상:{" "}
                                <strong>{balance.toLocaleString()} 원</strong> →{" "}
                                <strong>{expectedBalance.toLocaleString()} 원</strong>
                            </p>
                            <p style={{color: "#555", fontSize: "14px"}}>
                                🪙 현재 보유 금: {ownedGold.toFixed(2)} g
                            </p>
                        </div>
                    </div>

                    {/* 버튼 영역 */}
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
