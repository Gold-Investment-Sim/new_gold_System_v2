// src/pages/Simulation/Simulation.jsx
import { useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import "./Simulation.css";

function Simulation() {
  const navigate = useNavigate();  

  // localStorage에서 로그인 정보 꺼내기
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isAuthed = !!user.memberId; // 로그인 여부 체크

  const handleStart = () => {
    if (isAuthed) {
      // ✅ 로그인했을 때 → 정상적으로 Step1으로 이동
      console.log("로그인됨 → 시뮬레이션 시작 (결과 저장 가능)");
      navigate("/simulation/step1");  
    } else {
      // ✅ 로그인 안 했을 때 → 그래도 Step1으로 이동 (체험만 가능, 결과 저장 불가)
      console.log("로그인 안 됨 → 시뮬레이션 체험만 가능 (결과 저장 불가)");
      navigate("/simulation/step1");
    }
  };

  return (
    <>
      <Navigation 
        isAuthed={isAuthed}
        memberId={user.memberId}
        memberName={user.memberName}
        memberEmail={user.memberEmail}
        balance={user.balance}
        onLogout={() => {
          localStorage.removeItem("user");
          window.location.reload();
        }}
      />
      <div className="simulation">
        <h1 className="simulation-title">투자 시뮬레이션 시작하기</h1>
        <p className="simulation-subtitle">
          날짜를 선택하고, 그날의 뉴스와 금 시세를 확인하세요.
        </p>
        <p className="simulation-subtitle">
          실제처럼 매수·매도를 체험하며 나만의 투자 전략을 만들어보세요.
        </p>
        <button className="simulation-btn" onClick={handleStart}>
          시뮬레이션 시작하기
        </button>
      </div>
    </>
  );
}

export default Simulation;
