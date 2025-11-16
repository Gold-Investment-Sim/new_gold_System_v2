// src/pages/OnBoarding/OnBoarding.jsx
import "./OnBoarding.css";
import { useEffect, useRef } from "react";

import dateImg from "../../assets/date.png";
import img1 from "../../assets/1.png";
import img2 from "../../assets/2.png";
import img3 from "../../assets/3.png";
import img4 from "../../assets/4.png";
import img5 from "../../assets/5.png";
import img6 from "../../assets/6.png";

function OnBoarding() {
  const sectionsRef = useRef([]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) entry.target.classList.add("show");
        });
      },
      { threshold: 0.3 }
    );

    sectionsRef.current.forEach((el) => el && observer.observe(el));
    return () => observer.disconnect();
  }, []);

  const addRef = (el) => {
    if (el && !sectionsRef.current.includes(el)) {
      sectionsRef.current.push(el);
    }
  };

  return (
    <div className="onboarding-container">

      {/* --------------------------------------------- */}
      {/* 🔥 [0] GoldSim 전체 소개 (온보딩 최상단) */}
      {/* --------------------------------------------- */}
      <section ref={addRef} className="fade-section ob-intro-text">
        <h1>뉴스와 AI로 배우는 금 시세 시뮬레이션</h1>
        <p>
          GoldSim은 뉴스, 경제 지표, 그리고 우리가 직접 개발한 LSTM 예측 모델을
          하나의 흐름으로 연결하여 금 시세가 왜 움직이는지를 이해하도록 돕는
          데이터 기반 학습 플랫폼입니다.
          날짜를 선택하면 그날의 뉴스가 시장에 어떤 영향을 주었는지부터,
          예측된 시세 변화와 다양한 지표 간의 관계까지 실전처럼 따라가며
          금 투자 감각을 자연스럽게 익힐 수 있습니다.
        </p>
      </section>

      {/* --------------------------------------------- */}
      {/* 🔥 [1] 날짜 선택 안내 + 이미지(date.png) */}
      {/* --------------------------------------------- */}
      <section ref={addRef} className="fade-section ob-center-text">
        <div className="ob-title-wrap">
         
        <p className="ob-desc">선택한 날짜를 기준으로 모든 학습이 시작됩니다.</p>
        </div>
      </section>

      <section ref={addRef} className="fade-section ob-img-box">
        <img src={dateImg} alt="date" className="no-shadow" />
      </section>

      {/* --------------------------------------------- */}
      {/* 🔥 [2] 뉴스 확인 안내 + 이미지 1,2,3 */}
      {/* --------------------------------------------- */}
      <section ref={addRef} className="fade-section ob-center-text">
        <p className="ob-desc">해당 날짜의 뉴스들을 읽고 주요 내용을 확인해보세요.</p>
      </section>

      <section ref={addRef} className="fade-section ob-img-box">
        <img src={img1} alt="" />
      </section>
      <section ref={addRef} className="fade-section ob-img-box">
        <img src={img2} alt="" />
      </section>
      <section ref={addRef} className="fade-section ob-img-box">
        <img src={img3} alt="" />
      </section>

      {/* --------------------------------------------- */}
      {/* 🔥 [3] LSTM 예측 안내 + 이미지 4 */}
      {/* --------------------------------------------- */}
      <section ref={addRef} className="fade-section ob-center-text">
        <p className="ob-desc">머신러닝(LSTM)이 예측한 금 시세 변화를 확인해보세요.</p>
      </section>

      <section ref={addRef} className="fade-section ob-img-box">
        <img src={img4} alt="" />
      </section>

      {/* --------------------------------------------- */}
      {/* 🔥 [4] 시세·지표 안내 + 이미지 5,6 */}
      {/* --------------------------------------------- */}
      <section ref={addRef} className="fade-section ob-center-text">
        <p className="ob-desc">다양한 시세 지표와 데이터를 함께 비교해볼 수 있어요.</p>
      </section>

      <section ref={addRef} className="fade-section ob-img-box">
        <img src={img5} alt="" />
      </section>
      <section ref={addRef} className="fade-section ob-img-box">
        <img src={img6} alt="" />
      </section>

      {/* --------------------------------------------- */}
      {/* 🔥 [5] CTA */}
      {/* --------------------------------------------- */}
      <section ref={addRef} className="fade-section ob-final">
        <h2>이제 금 매수·매도 시뮬레이션을 시작해볼까요?</h2>
        <button
          className="ob-start-btn"
          onClick={() => (window.location.href = "/simulation")}
        >
          시뮬레이션 시작하기
        </button>
      </section>

    </div>
  );
}

export default OnBoarding;
