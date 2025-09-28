// src/pages/DeleteAccount.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Navigation from "../../components/Navigation";
import "../Login/Login.css";

export default function DeleteAccount() {
  const nav = useNavigate();
  const [password, setPassword] = useState("");
  const [agree, setAgree] = useState(false);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [done, setDone] = useState(false);

  const canSubmit = Boolean(password) && agree && !loading;

  async function submit(e) {
    e.preventDefault();
    setErr("");
    try {
      setLoading(true);
      const res = await fetch("http://localhost:8080/api/auth/deleteAccount", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ password }),
      });
      if (!res.ok) throw new Error("탈퇴 실패");
      setDone(true);
    } catch (e) {
      setErr(e.message || "탈퇴 실패");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <Navigation />
      <div className="login-wrap">
        <div className="login-card">
          <h1 className="title">회원 탈퇴</h1>

          {done ? (
            <div className="form" style={{ textAlign: "center" }}>
              계정이 비활성화되었습니다. 30일 후 영구 삭제됩니다.
              <button className="btn" style={{ marginTop: 16 }} onClick={() => nav("/login")}>
                로그인 페이지로 이동
              </button>
            </div>
          ) : (
            <form className="form" onSubmit={submit}>
              {/* 안내 박스 */}
              <div className="hint" style={{ border: "1px solid #e5e7eb", padding: 12, borderRadius: 8 }}>
                <strong>안내</strong>
                <ul style={{ marginTop: 8, paddingLeft: 18 }}>
                  <li>탈퇴 후 30일 동안은 로그인할 수 없으며, 이후 계정은 영구 삭제됩니다.</li>
                  <li>시뮬레이션 이력 등 이용 기록은 삭제됩니다.</li>
                  <li>삭제 이후 복구는 불가능합니다.</li>
                </ul>
              </div>

              {/* 동의 체크 */}
              <label className="remember" style={{ marginTop: 12 }}>
                <input
                  type="checkbox"
                  checked={agree}
                  onChange={(e) => setAgree(e.target.checked)}
                />
                위 내용을 확인했으며 계정 삭제에 동의합니다.
              </label>

              {/* 비밀번호 */}
              <label className="label" htmlFor="pwd" style={{ marginTop: 8 }}>현재 비밀번호</label>
              <input
                id="pwd"
                type="password"
                className="input"
                placeholder="비밀번호를 입력하세요"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />

              {err && <div className="error">{err}</div>}

              <button className="btn" disabled={!canSubmit}>
                {loading ? "처리 중..." : "회원 탈퇴"}
              </button>
            </form>
          )}
        </div>
      </div>
    </>
  );
}
