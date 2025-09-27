// src/pages/OnBoarding/OnBoarding.jsx
// src/pages/OnBoarding/OnBoarding.jsx
import Navigation from "../../components/Navigation.jsx";

import "./OnBoarding.css";

function OnBoarding() {
  return (
    <>
      <Navigation />
      <div className="onboarding">
        <h1 className="onboarding-title">
          투자 초보도 이해할 수 있는 금 시세 학습 플랫폼
        </h1>
        <p className="onboarding-subtitle">
          뉴스와 데이터를 읽고 금 시세 변화를 직접 경험하세요!
        </p>
      </div>
    </>
  );
}

export default OnBoarding;
