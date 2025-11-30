# 💰 저금통: 사회초년생을 위한 금 모의 투자 시뮬레이션

> 2025년 신한대학교 소프트웨어융합학과 졸업 작품 프로젝트

<br/>

## 🎯 프로젝트 목표 (Our Goal)

금 투자에 관심은 있지만 자본이나 정보 부족으로 어려움을 겪는 초보 투자자를 위해, 실제 자본 투입 없이 안전하게 금 투자의 흐름을 학습하고 체험할 수 있는 웹 기반 모의 투자 플랫폼을 제공합니다.

- **안전한 투자 경험:** 과거 금 시세 데이터를 기반으로 리스크 없이 매수/매도 시뮬레이션을 제공합니다.
- **직관적인 정보 제공:** 금리, 환율 등 외부 요인과 시각화된 데이터를 통해 초보자도 쉽게 투자 판단을 내릴 수 있도록 돕습니다.
- **몰입감 있는 학습 환경:** AI와의 예측 대결, 뉴스 데이터 분석 등 다양한 기능을 통해 사용자의 투자 학습을 지원합니다.

<br/>

## ✨ 핵심 기능 (Features)

### 1. 시뮬레이션 시스템
- **금 시세 예측 대결:** AI와 사용자가 금 시세를 예측하고 결과를 비교하는 기능을 제공합니다.
- **거래 시뮬레이션:** 과거 데이터를 기반으로 가상 자산을 이용해 매수/매도 거래를 체험할 수 있습니다.
- **결과 시각화:** 사용자의 예측, AI 예측, 실제 금 시세를 그래프로 비교하여 직관적인 피드백을 제공합니다.

### 2. 사용자 시스템
- **회원 관리:** 회원가입, 로그인, 정보 수정 및 탈퇴 기능을 제공합니다.
- **이력 관리:** 과거 모든 시뮬레이션 기록과 성과를 조회하고 분석할 수 있습니다.
- **용어 사전:** 금 투자 관련 용어를 쉽게 찾아보고 학습할 수 있는 기능을 제공합니다.

### 3. 데이터 예측 시스템 (AI)
- **데이터 처리:** 뉴스, 기준금리, 유가 등 다양한 외부 데이터를 수집하고 정제합니다.
- **예측 모델:** LSTM 모델을 기반으로 금 시세 예측 결과를 생성합니다.

<br/>

## 🛠 기술 스택 (Tech Stack)

- **Backend:** Java 21, Spring Boot 3.5.6
- **Frontend:** React.js (Vite), Chart.js, Tailwind CSS
- **Database:** MySQL
- **AI / ML:** Python, TensorFlow (LSTM), FastAPI
- **External APIs:** FRED API, METALS-API, Naver Search API
- **Collaboration:** Git, GitHub, Discord

<br/>

## 👥 우리 팀 (Our Team)

| 역할 | 이름 | GitHub |
| :--- | :--- | :--- |
| **책임개발자** | 최승희 | `@sseung0510` |
| **공동개발자** | 김서희 | `@kimseoheeyeyo` |
| **공동개발자** | 문해찬 | `@haechanmoon` |
| **공동개발자** | 안제홍 | `@burindol3` |

<br/>

---

## 🚀 시작 가이드 (Getting Started)

### 1. Backend (Spring Boot)
```bash
cd BE
# Windows
./gradlew bootRun
# Mac/Linux
./gradlew bootRun
```
* 서버는 기본적으로 `8080` 포트에서 실행됩니다.
* `application.properties` 또는 `application.yml`에서 DB 설정을 확인해주세요.

### 2. Frontend (React)
```bash
cd FE
npm install
npm run dev
```
* 브라우저에서 `http://localhost:5173` (Vite 기본 포트)으로 접속합니다.

### 3. AI Model Server (FastAPI)
```bash
cd lstm_model
# 가상환경 생성 및 활성화 (권장)
python -m venv .venv
# Windows: .venv\Scripts\activate
# Mac/Linux: source .venv/bin/activate

# 의존성 설치
pip install -r requirements.txt

# 서버 실행
python -m uvicorn app:app --reload --port 8000
```
* AI 예측 서버는 `8000` 포트에서 실행됩니다.

---
Copyright © 2025 Shinhan University Capstone Design Team. All Rights Reserved.
