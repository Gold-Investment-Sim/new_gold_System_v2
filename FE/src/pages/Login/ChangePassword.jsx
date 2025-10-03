// src/pages/ChangePassword.jsx
import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../Login/Login.css";

const PWD_REGEX =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[ !"#$%&'()*+,\-./:;<=>?@[\\\]^_`{|}~]).{8,}$/;

export default function ChangePassword({ onLogout }) {
  const nav = useNavigate();
  const [currentPwd, setCurrentPwd] = useState("");
  const [newPwd, setNewPwd] = useState("");
  const [confirmPwd, setConfirmPwd] = useState("");
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [done, setDone] = useState(false);

  const pwdValid = useMemo(() => PWD_REGEX.test(newPwd), [newPwd]);
  const pwdMatch = useMemo(() => newPwd === confirmPwd, [newPwd, confirmPwd]);
  const canSubmit = currentPwd && pwdValid && pwdMatch && !loading;

  async function submit(e) {
    e.preventDefault();
    setErr("");
    if (!pwdValid) { setErr("새 비밀번호 규칙을 확인하세요."); return; }
    if (!pwdMatch) { setErr("새 비밀번호가 일치하지 않습니다."); return; }

    try {
      setLoading(true);
      const res = await fetch("http://localhost:8080/api/auth/updatePassword", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ currentPwd, newPwd, confirmPwd }),
      });
      if (!res.ok) {
        let msg = "변경 실패";
        try { const data = await res.json(); if (data?.message) msg = data.message; } catch {}
        throw new Error(msg);
      }

      // 성공 시: onLogout이 있으면 즉시 로그아웃 처리, 없으면 완료 화면
      if (typeof onLogout === "function") {
        await onLogout();
        return;
      }
      setDone(true);
      setCurrentPwd(""); setNewPwd(""); setConfirmPwd("");
    } catch (e) {
      setErr(e.message || "변경 실패");
    } finally {
      setLoading(false);
    }
  }

  async function logoutAndGoLogin() {
    try {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } finally {
      nav("/login", { replace: true });
    }
  }

  return (
    <div className="login-wrap">
      <div className="login-card">
        <h1 className="title">비밀번호 변경</h1>

        {done ? (
          <div className="form" style={{ textAlign: "center" }}>
            비밀번호를 변경했습니다. 보안을 위해 다시 로그인하세요.
            <button className="btn" style={{ marginTop: 16 }} onClick={logoutAndGoLogin}>
              로그아웃 후 로그인
            </button>
          </div>
        ) : (
          <form className="form" onSubmit={submit}>
            <label className="label" htmlFor="cur">현재 비밀번호</label>
            <input
              id="cur" type="password" className="input" value={currentPwd}
              onChange={(e)=>setCurrentPwd(e.target.value)} required
            />

            <label className="label" htmlFor="new">새 비밀번호</label>
            <input
              id="new" type="password" className="input" value={newPwd}
              onChange={(e)=>setNewPwd(e.target.value)} required aria-describedby="pwd-help"
            />
            <div id="pwd-help" className={`hint ${pwdValid ? "success" : ""}`}>
              최소 8자, 대/소문자·숫자·특수문자 각 1개 이상
            </div>

            <label className="label" htmlFor="cnf">새 비밀번호 확인</label>
            <input
              id="cnf" type="password" className="input" value={confirmPwd}
              onChange={(e)=>setConfirmPwd(e.target.value)} required
            />
            <div className={`hint ${confirmPwd ? (pwdMatch ? "success" : "error") : ""}`}>
              {confirmPwd ? (pwdMatch ? "일치합니다." : "일치하지 않습니다.") : "\u00A0"}
            </div>

            {err && <div className="error">{err}</div>}

            <button className="btn" disabled={!canSubmit}>
              {loading ? "변경 중..." : "비밀번호 변경"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
