package com.goldSys.BE.glossary.controller;

import com.goldSys.BE.glossary.entity.Glossary;
import com.goldSys.BE.glossary.repository.GlossaryRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/glossary")
@CrossOrigin(origins = "http://localhost:5173")  // 프론트 React 포트
public class GlossaryController {

    private final GlossaryRepository glossaryRepository;

    public GlossaryController(GlossaryRepository glossaryRepository) {
        this.glossaryRepository = glossaryRepository;
    }

    // ✅ 전체 목록
    @GetMapping
    public List<Glossary> getAll() {
        return glossaryRepository.findAll();
    }

    // ✅ 검색
    @GetMapping("/search")
    public List<Glossary> search(@RequestParam String term) {
        return glossaryRepository.findByTermContainingIgnoreCase(term);
    }
}
