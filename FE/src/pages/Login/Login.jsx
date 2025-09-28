// src/pages/Login.jsx
import { useState, useEffect } from "react";
import axios from "axios";
import Navigation from "../../components/Navigation";
import "../Login/Login.css";

export default function Login() {
  const [memberId, setMemberId] = useState("");
  const [memberPwd, setMemberPwd] = useState("");
  const [remember, setRemember] = useState(false);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const saved = localStorage.getItem("saved_memberId");
    if (saved) { setMemberId(saved); setRemember(true); }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErr(""); setLoading(true);
    try {
      await axios.post(
        "http://localhost:8080/api/auth/login",
        { memberId, memberPwd },
        { withCredentials: true }
      );
      if (remember) localStorage.setItem("saved_memberId", memberId);
      else localStorage.removeItem("saved_memberId");
      window.location.href = "/";
    } catch {
      setErr("아이디 또는 비밀번호가 올바르지 않습니다.");
    } finally { setLoading(false); }
  };

  return (
    <>
      <Navigation />
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
              onChange={(e)=>setMemberId(e.target.value)}
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
              onChange={(e)=>setMemberPwd(e.target.value)}
              autoComplete="current-password"
              required
            />

            <label className="remember">
              <input
                type="checkbox"
                checked={remember}
                onChange={(e)=>setRemember(e.target.checked)}
              />
              아이디 저장
            </label>

            {err && <div className="error">{err}</div>}

            <button type="submit" className="btn" disabled={loading}>
              {loading ? "로그인 중..." : "로그인"}
            </button>
          </form>

          <div className="login-footer">
            <a className="link" href="/signup">회원가입</a>
            <span className="dot">·</span>
            <a className="link" href="/forgotPassword">비밀번호 찾기</a>
          </div>
        </div>
      </div>
    </>
  );
}
