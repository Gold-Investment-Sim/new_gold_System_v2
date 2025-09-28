// src/components/Navigation.jsx
import { Link, useNavigate } from "react-router-dom";
import { useState, useRef, useEffect, useMemo } from "react";
import { FaUserCircle } from "react-icons/fa";
import "./Navigation.css";

function Navigation({ isAuthed, memberId, memberName, memberEmail, balance, onLogout }) {
  const [open, setOpen] = useState(false);
  const menuRef = useRef(null);
  const nav = useNavigate();

  const displayName = useMemo(
    () => (memberName && memberName.trim()) || (memberId && memberId.trim()) || "사용자",
    [memberName, memberId]
  );
  const initials = useMemo(() => {
    const s = (memberName || memberId || "").trim();
    return s ? s.slice(0, 1).toUpperCase() : "";
  }, [memberName, memberId]);

  useEffect(() => {
    const handler = (e) => { if (menuRef.current && !menuRef.current.contains(e.target)) setOpen(false); };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <nav className="nav">
      <div className="nav-container">
        <div className="nav-logo">GoldSim</div>

        <div className="nav-menu">
          <Link to="/simulation">투자 시뮬레이션</Link>
          <Link to="/history">투자 이력</Link>
          <Link to="/glossary">용어사전</Link>
        </div>

        {!isAuthed ? (
          <Link to="/login" className="gs-cta">로그인</Link>
        ) : (
          <div className="profile" ref={menuRef}>
            <button
              type="button"
              className="gs-avatar"
              onClick={() => setOpen(v => !v)}
              aria-haspopup="menu"
              aria-expanded={open}
              title={displayName}
            >
              {initials ? <span className="gs-avatar-text">{initials}</span> : <FaUserCircle aria-hidden />}
            </button>

            {open && (
              <div className="gs-menu" role="menu">
                <div className="gs-menu-header">
                  <div className="gs-menu-avatar">{initials || <FaUserCircle aria-hidden />}</div>
                  <div className="gs-menu-meta">
                    <div className="gs-menu-name">{displayName}</div>
                    {/* 아이디/이메일 노출 규칙 */}
                    {memberId ? <div className="gs-menu-id">@{memberId}</div> : null}
                    {memberEmail ? <div className="gs-menu-id">{memberEmail}</div> : null}
                    {typeof balance === "number" && (
                      <div className="gs-menu-balance">보유 자산 {Number(balance).toLocaleString()}원</div>
                    )}
                  </div>
                </div>

                <div className="gs-menu-divider" />

                <button
                  className="gs-menu-item"
                  onClick={() => { setOpen(false); nav("/changePassword"); }} // 경로 정정
                >
                  비밀번호 변경
                </button>
                <button
                  className="gs-menu-item"
                  onClick={() => { setOpen(false); nav("/deleteAccount"); }}
                >
                  회원탈퇴
                </button>
                <button
                  className="gs-menu-item danger"
                  onClick={() => { setOpen(false); onLogout?.(); }}
                >
                  로그아웃
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </nav>
  );
}

export default Navigation;
