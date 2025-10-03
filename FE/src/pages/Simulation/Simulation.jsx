// src/pages/Simulation/Simulation.jsx
import { useNavigate } from "react-router-dom";
import "./Simulation.css";

function Simulation() {
  const navigate = useNavigate();  

  const handleStart = () => {
    console.log("시뮬레이션 시작하기 버튼 클릭됨");
    navigate("/simulation/step1");  
  };

  return (
    <>
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
