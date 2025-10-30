import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Glossary.css";

export default function Glossary() {
  const [search, setSearch] = useState("");
  const [terms, setTerms] = useState([]);
  const [openIndex, setOpenIndex] = useState(null); // ✅ 어떤 카드가 열렸는지 저장

  useEffect(() => {
    axios.get("http://localhost:8080/api/glossary")
      .then(res => setTerms(res.data))
      .catch(err => console.error("용어 불러오기 실패:", err));
  }, []);

  const filtered = terms.filter((t) =>
    t.term.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="glossary">
      <h1 className="glossary-title">📘 용어사전</h1>
      <input
        type="text"
        className="glossary-search"
        placeholder="용어를 검색하세요."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <div className="glossary-list">
        {filtered.map((item, i) => (
          <div
            key={i}
            className={`glossary-card ${openIndex === i ? "open" : ""}`}
            onClick={() => setOpenIndex(openIndex === i ? null : i)}
          >
            <h3>{item.term}</h3>
            <p className="glossary-category">{item.category}</p>
            <p>{item.definition}</p>

            {/* ✅ summary 영역 (클릭 시 확장) */}
            {openIndex === i && (
              <div className="glossary-summary">
                <p>{item.summary}</p>
              </div>
            )}
          </div>
        ))}
        {filtered.length === 0 && (
          <p className="no-result">검색 결과가 없습니다.</p>
        )}
      </div>
    </div>
  );
}
