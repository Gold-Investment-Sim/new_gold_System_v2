// src/pages/ForgotPassword.jsx
import { useState } from "react";
import { Link } from "react-router-dom";
import Navigation from "../../components/Navigation";     // <- 경로 확인 필요
import "../Login/Login.css";                           // <- 프로젝트에 맞게 유지

export default function ForgotPassword() {
  const [memberId, setMemberId] = useState("");
  const [memberEmail, setEmail] = useState("");
  const [done, setDone] = useState(false);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  async function submit(e) {
    e.preventDefault();
    setErr(""); setLoading(true);
    try {
      const res = await fetch("http://localhost:8080/api/auth/forgotPassword", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ memberId, memberEmail }),
      });
      if (res.ok) setDone(true);
      else setErr("요청을 처리할 수 없습니다.");
    } catch {
      setErr("네트워크 오류");
    } finally {
      setLoading(false);
    }
  }

  return (
      <div className="login-wrap">
        <div className="login-card">
          <h1 className="title">비밀번호 찾기</h1>

          {done ? (
            <p>임시 비밀번호를 이메일로 보냈습니다. 로그인 후 비밀번호를 변경하세요.</p>
          ) : (
            <form className="form" onSubmit={submit}>
              <label className="label" htmlFor="fid">아이디</label>
              <input
                id="fid" className="input" placeholder="아이디"
                value={memberId} onChange={(e)=>setMemberId(e.target.value)}
                autoComplete="username" required
              />

              <label className="label" htmlFor="fem">이메일</label>
              <input
                id="fem" type="email" className="input" placeholder="이메일"
                value={memberEmail} onChange={(e)=>setEmail(e.target.value)} required
              />

              {err && <div className="error">{err}</div>}

              <button type="submit" className="btn" disabled={loading}>
                {loading ? "전송 중..." : "임시 비밀번호 발송"}
              </button>
            </form>
          )}

          <div className="login-footer">
            <Link className="link" to="/login">로그인으로 돌아가기</Link>
          </div>
        </div>
      </div>
  );
}
