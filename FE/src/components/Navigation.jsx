// src/components/Navigation.jsx
import { Link } from "react-router-dom";
import "./Navigation.css";

function Navigation() {
  return (
    <nav className="nav">
      <div className="nav-container">
        {/* 로고 */}
        <div className="nav-logo">GoldSim</div>

        {/* 메뉴 */}
        <div className="nav-menu">
          <Link to="/simulation">투자 시뮬레이션</Link>
          <Link to="/history">투자 이력</Link>
          <Link to="/glossary">용어사전</Link>
        </div>

        {/* 로그인 */}
        <div className="nav-login">
          <Link to="/login">로그인</Link>
        </div>
      </div>
    </nav>
  );
}

export default Navigation;
