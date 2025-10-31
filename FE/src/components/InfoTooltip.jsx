// src/components/InfoTooltip.jsx
import { useState, useMemo, useRef, useLayoutEffect } from "react";
import { createPortal } from "react-dom";
import { FaInfoCircle } from "react-icons/fa";
import "./InfoTooltip.css";

export default function InfoTooltip({ title, isPred = false }) {
    const [open, setOpen] = useState(false);
    const [pos, setPos] = useState({ top: 0, left: 0 });
    const anchorRef = useRef(null);

    const text = useMemo(() => {
        if (title?.includes("환율"))
            return "미국 달러 1달러를 매입하기 위해 필요한 원화 금액입니다.\n환율 상승은 원화 가치 하락을 의미합니다.";
        if (title?.includes("VIX"))
            return "미국 S&P500 지수 옵션의 변동성을 수치화한 지표입니다.\n시장 불확실성과 위험 수준을 나타냅니다.";
        if (title?.includes("ETF"))
            return "SPDR Gold ETF의 일일 거래량을 의미합니다.\n금 관련 투자 수요의 변화를 파악하는 참고 지표입니다.";
        if (title?.includes("예측") || isPred)
            return "GoldSim이 과거 금 시세·환율·VIX·ETF 데이터를 기반으로 생성한 예측 결과입니다.\n통계적 모델 분석을 통해 향후 가격 흐름을 추정합니다.";
        if (title?.includes("금"))
            return "국제 금 시세(USD/oz)를 원화 기준 g(그램)당 가격으로 환산한 값입니다.\n실제 금 거래 시 참고되는 대표적인 단가 지표입니다.";
        if (title?.includes("PnL"))
            return "PnL(Profit and Loss)은 각 거래의 손익을 의미합니다.\n양수(▲)는 이익, 음수(▼)는 손실을 뜻합니다."; 
        return "";
    }, [title, isPred]);


    const recalc = () => {
        const el = anchorRef.current;
        if (!el) return;
        const r = el.getBoundingClientRect();
        setPos({ top: r.bottom + 6, left: r.left });
    };

    useLayoutEffect(() => {
        if (!open) return;
        recalc();
        const fn = () => recalc();
        window.addEventListener("resize", fn);
        window.addEventListener("scroll", fn, true);
        return () => {
            window.removeEventListener("resize", fn);
            window.removeEventListener("scroll", fn, true);
        };
    }, [open]);

    if (!text) return null;

    return (
        <>
            <span
                ref={anchorRef}
                className="it-wrap"
                onMouseEnter={() => setOpen(true)}
                onMouseLeave={() => setOpen(false)}
            >
                <FaInfoCircle className="it-icon" aria-label="정보" />
            </span>

            {open &&
                createPortal(
                    <div className="it-box" style={{ top: pos.top, left: pos.left }}>
                        {text}
                    </div>,
                    document.body
                )}
        </>
    );
}
