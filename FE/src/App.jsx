import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useEffect, useState } from "react";
import "./App.css";
import Navigation from "./components/Navigation";

// 페이지들 import
import OnBoarding from "./pages/OnBoarding/OnBoarding";
import Simulation from "./pages/Simulation/Simulation";
import History from "./pages/History/History";
import Glossary from "./pages/Glossary/Glossary";
import Login from "./pages/Login/Login";
import Step1 from "./pages/Simulation/Step1";
import Step2 from "./pages/Simulation/Step2";
import Step3 from "./pages/Simulation/Step3";
import Result from "./pages/Simulation/Result";
import ForgotPassword from "./pages/Login/ForgotPassword";
import Signup from "./pages/Login/Signup";
import ChangePassword from "./pages/Login/ChangePassword";
import DeleteAccount from "./pages/Login/DeleteAccount";

function AppInner() {
  const [auth, setAuth] = useState(null);

  // 앱 시작 시 /api/auth/me 호출 → 로그인 상태 복구
  useEffect(() => {
    (async () => {
      try {
        const res = await fetch("http://localhost:8080/api/auth/me", {
          credentials: "include", // 세션 쿠키 포함
        });
        if (res.ok) {
          const user = await res.json();
          setAuth(user); // {memberId, memberName, memberEmail, balance, ...}
        } else {
          setAuth(null);
        }
      } catch (err) {
        console.error("auth/me 확인 실패", err);
        setAuth(null);
      }
    })();
  }, []);

  const onLogout = async () => {
    try {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } finally {
      setAuth(null);
      window.location.replace("/login");  // 새로고침 포함된 강제 이동

    }
  };

  return (
    <>
      <Navigation
        isAuthed={!!auth}
        memberId={auth?.memberId}
        memberName={auth?.memberName}
        memberEmail={auth?.memberEmail}
        balance={auth?.balance}
        onLogout={onLogout}
      />
      <Routes>
        <Route path="/" element={<OnBoarding />} />
        <Route path="/simulation" element={<Simulation />} />
        <Route path="/history" element={<History />} />
        <Route path="/glossary" element={<Glossary />} />
        <Route path="/login" element={<Login onAuthed={setAuth} />} />
        <Route path="/forgotPassword" element={<ForgotPassword />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/changePassword" element={<ChangePassword />} />
        <Route path="/deleteAccount" element={<DeleteAccount />} />
        <Route path="/simulation/step1" element={<Step1 />} />
        <Route path="/simulation/step2" element={<Step2 />} />
        <Route path="/simulation/step3" element={<Step3 />} />
        <Route path="/simulation/result" element={<Result />} />
      </Routes>
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppInner />
    </BrowserRouter>
  );
}
