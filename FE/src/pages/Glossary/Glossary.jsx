import React, { useState } from "react";
import "./Glossary.css";

const dummyTerms = [
  { term: "금 ETF", category: "투자상품", definition: "금 가격을 추종하는 상장지수펀드" },
  { term: "온스(Oz)", category: "단위", definition: "국제 금 거래 단위 (1oz = 31.1035g)" },
  { term: "스팟가격", category: "시장용어", definition: "현재 시점의 금 현물 거래가격" },
];

export default function Glossary() {
  const [search, setSearch] = useState("");

  const filtered = dummyTerms.filter((t) =>
    t.term.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="glossary">
      <h1 className="glossary-title">📘 용어사전</h1>
      <input
        type="text"
        className="glossary-search"
        placeholder="용어를 검색하세요..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <div className="glossary-list">
        {filtered.map((item, i) => (
          <div key={i} className="glossary-card">
            <h3>{item.term}</h3>
            <p className="glossary-category">{item.category}</p>
            <p>{item.definition}</p>
          </div>
        ))}
        {filtered.length === 0 && <p className="no-result">검색 결과가 없습니다.</p>}
      </div>
    </div>
  );
}
