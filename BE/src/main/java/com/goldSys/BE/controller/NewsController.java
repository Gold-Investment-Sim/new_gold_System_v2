package com.goldSys.BE.controller;

import com.goldSys.BE.entity.NewsArticle;
import com.goldSys.BE.repository.NewsArticleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*") // 프론트와 연결을 위해
public class NewsController {

    private final NewsArticleRepository newsArticleRepository;

    public NewsController(NewsArticleRepository newsArticleRepository) {
        this.newsArticleRepository = newsArticleRepository;
    }

    // 특정 날짜 뉴스 조회 (예: 2023-01-01)
    @GetMapping("/{date}")
    public List<NewsArticle> getNewsByDate(@PathVariable String date) {
        return newsArticleRepository.findByArticleDate(date);
    }
}
