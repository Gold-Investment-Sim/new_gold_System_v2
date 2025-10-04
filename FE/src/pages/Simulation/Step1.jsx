// src/pages/Simulation/Step1.jsx
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import "./Step1.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Step1() {
  const [date, setDate] = useState(new Date());
  const navigate = useNavigate();

  // localStorage에서 로그인 정보 꺼내기
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isAuthed = !!user.memberId; // 로그인 여부

  // 범위: 2023년 1월 1일 ~ 2024년 12월 31일
  const minDate = new Date(2023, 0, 1);
  const maxDate = new Date(2024, 11, 31);

  const handleStart = () => {
    console.log("선택한 날짜:", date);

    if (isAuthed) {
      // ✅ 로그인 O → 선택한 날짜 저장 + Step2 이동
      console.log("로그인됨 → 이후 결과 저장 가능");
    } else {
      // ✅ 로그인 X → 선택한 날짜 전달만 (체험 모드)
      console.log("로그인 안 됨 → 결과 저장 불가 (체험 모드)");
    }

    // 로그인 여부 상관없이 Step2로 이동
    navigate("/simulation/step2", { state: { date } });
  };

  return (
    <>
        isAuthed={isAuthed}
        memberId={user.memberId}
        memberName={user.memberName}
        memberEmail={user.memberEmail}
        balance={user.balance}
        onLogout={() => {
          localStorage.removeItem("user");
          window.location.reload();
        }}
      <div className="step1">
        <h1 className="step1-title">날짜 선택하기</h1>
        <p className="step1-subtitle">
          투자할 날짜를 선택하세요. 선택한 날짜의 뉴스와 금 시세를 확인할 수 있습니다.
        </p>
        <div className="calendar-wrapper">
          <Calendar
            onChange={setDate}
            value={date}
            minDate={minDate}
            maxDate={maxDate}
          />
        </div>
        <p className="selected-date">
          선택한 날짜: {date.toLocaleDateString("ko-KR")}
        </p>
        <button className="step1-btn" onClick={handleStart}>
          선택완료
        </button>
      </div>
    </>
  );
}

export default Step1;
