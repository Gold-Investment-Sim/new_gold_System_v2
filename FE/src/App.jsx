import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import {useEffect, useState} from "react";
import "./App.css";
import Navigation from "./components/Navigation";

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

const API = import.meta.env.VITE_API_URL || "http://localhost:8080";

function AppInner() {
    const [auth, setAuth] = useState(() => {
        try {
            const u = localStorage.getItem("user");
            return u ? JSON.parse(u) : null;
        } catch { return null; }
    });
    const [loading, setLoading] = useState(true); // 로딩 상태 추가

    // 앱 시작 시 서버에 세션 유효성 검사 요청
    useEffect(() => {
        const verifyUser = async () => {
            try {
                const res = await fetch(`${API}/api/auth/me`, { credentials: "include" });
                if (!res.ok) { // 세션이 만료되었거나 유효하지 않으면 에러 발생
                    throw new Error("Session expired");
                }
                const user = await res.json();
                onAuthed(user); // 세션이 유효하면 사용자 정보 갱신
            } catch (e) {
                // 세션이 유효하지 않으면 localStorage를 비우고 로그아웃 상태로 만듦
                localStorage.removeItem("user");
                setAuth(null);
            } finally {
                setLoading(false); // 세션 확인이 끝나면 로딩 종료
            }
        };
        verifyUser();
    }, []); // []를 전달하여 앱이 처음 시작될 때 한 번만 실행

  const onAuthed = (user) => {
    setAuth(user);
    localStorage.setItem("user", JSON.stringify(user));
  };

  const onLogout = async () => {
    try {
      await fetch(`${API}/api/auth/logout`, { method: "POST", credentials: "include" });
    } finally {
      localStorage.removeItem("user");
      setAuth(null);
      window.location.replace("/login");
    }
  };
    // 로딩 중에는 아무것도 표시하지 않거나 로딩 스피너를 보여줄 수 있습니다.
    if (loading) {
        return <div>Loading...</div>;
    }
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
                <Route path="/" element={<OnBoarding auth={auth} />} />
                <Route path="/simulation" element={<Simulation auth={auth} />} />
                <Route
                    path="/history"
                    element={!!auth ? <History auth={auth} /> : <Navigate to="/login" replace />}
                />
                <Route path="/glossary" element={<Glossary auth={auth} />} />

                <Route path="/login" element={<Login onAuthed={onAuthed} />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/forgotPassword" element={<ForgotPassword />} />
                <Route path="/changePassword" element={<ChangePassword onLogout={onLogout} />} />
                <Route path="/deleteAccount" element={<DeleteAccount onLogout={onLogout} />} />

                <Route path="/simulation/step1" element={<Step1 auth={auth} />} />
                <Route path="/simulation/step2" element={<Step2 auth={auth} />} />
                <Route path="/simulation/step3" element={<Step3 auth={auth} />} />
                <Route path="/simulation/result" element={<Result auth={auth} />} />
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
