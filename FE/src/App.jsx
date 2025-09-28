import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

// 페이지 불러오기
import OnBoarding from "./pages/OnBoarding/OnBoarding";
import Simulation from "./pages/Simulation/Simulation";
import History from "./pages/History/History";
import Glossary from "./pages/Glossary/Glossary";
import Login from "./pages/Login/Login";
import Step1 from "./pages/Simulation/Step1";
import Step2 from "./pages/Simulation/Step2";
import Step3 from "./pages/Simulation/Step3";
import Result from "./pages/Simulation/Result";
import ForgotPassword from "./pages/Login/ForgotPassword";
import Signup from "./pages/Login/Signup";
import ChangePassword from "./pages/Login/ChangePassword";
import DeleteAccount from "./pages/Login/DeleteAccount";



function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 제일 첫 시작 (/) → OnBoarding */}
        <Route path="/" element={<OnBoarding />} />
        
        {/* 다른 페이지들 */}
        <Route path="/simulation" element={<Simulation />} />
        <Route path="/history" element={<History />} />
        <Route path="/glossary" element={<Glossary />} />
        <Route path="/login" element={<Login />} />
        <Route path="/forgotPassword" element={<ForgotPassword />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/changePassword" element={<ChangePassword />} />
        <Route path="/deleteAccount" element={<DeleteAccount />} />
        <Route path="/simulation/step1" element={<Step1 />} />
        <Route path="/simulation/step2" element={<Step2 />} />
        <Route path="/simulation/step3" element={<Step3 />} />
        <Route path="/simulation/result" element={<Result />} />


      </Routes>
    </BrowserRouter>
  );
}

export default App;
