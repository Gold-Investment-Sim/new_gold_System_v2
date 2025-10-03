// src/pages/Login.jsx
import { useState, useEffect } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import axios from "axios";
import "../Login/Login.css";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  withCredentials: true,
});

export default function Login({ onAuthed }) {
  const nav = useNavigate();
  const loc = useLocation();
  const [memberId, setMemberId] = useState("");
  const [memberPwd, setMemberPwd] = useState("");
  const [remember, setRemember] = useState(false);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  // 저장된 아이디 불러오기
  useEffect(() => {
    const saved = localStorage.getItem("saved_memberId");
    if (saved) {
      setMemberId(saved);
      setRemember(true);
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (loading) return;
    setErr("");
    setLoading(true);
    try {
      const { data } = await api.post("/api/auth/login", { memberId, memberPwd });
      // 전역 상태 갱신
      onAuthed?.(data);
      // 새로고침 대비 저장
      localStorage.setItem("user", JSON.stringify(data));

      // 아이디 저장
      if (remember) localStorage.setItem("saved_memberId", memberId);
      else localStorage.removeItem("saved_memberId");

      // 보호 라우트가 넘겨준 이동 대상 or 홈
      const from = loc.state?.from || "/";
      nav(from, { replace: true });
    } catch (e2) {
      const msg = e2?.response?.data?.message || "아이디 또는 비밀번호가 올바르지 않습니다.";
      setErr(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-wrap">
      <div className="login-card">
        <h1 className="title">로그인</h1>
        <form className="form" onSubmit={handleSubmit}>
          <label className="label" htmlFor="memberId">아이디</label>
          <input
            id="memberId"
            type="text"
            className="input"
            placeholder="아이디"
            value={memberId}
            onChange={(e) => setMemberId(e.target.value)}
            autoComplete="username"
            required
          />

          <label className="label" htmlFor="memberPwd">비밀번호</label>
          <input
            id="memberPwd"
            type="password"
            className="input"
            placeholder="비밀번호"
            value={memberPwd}
            onChange={(e) => setMemberPwd(e.target.value)}
            autoComplete="current-password"
            required
          />

          <label className="remember">
            <input
              type="checkbox"
              checked={remember}
              onChange={(e) => setRemember(e.target.checked)}
            />
            아이디 저장
          </label>

          {err && <div className="error">{err}</div>}

          <button type="submit" className="btn" disabled={loading}>
            {loading ? "로그인 중..." : "로그인"}
          </button>
        </form>

        <div className="login-footer">
          <Link className="link" to="/signup">회원가입</Link>
          <span className="dot">·</span>
          <Link className="link" to="/forgotPassword">비밀번호 찾기</Link>
        </div>
      </div>
    </div>
  );
}
