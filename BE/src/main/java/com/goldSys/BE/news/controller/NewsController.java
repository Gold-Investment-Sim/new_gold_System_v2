package com.goldSys.BE.news.controller;

import com.goldSys.BE.news.entity.NewsArticle;
import com.goldSys.BE.news.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsArticleRepository newsArticleRepository;

    // 특정 날짜 뉴스 조회 (예: 2023-01-01)
    @GetMapping("/{date}")
    public List<NewsArticle> getNewsByDate(@PathVariable String date) {
        return newsArticleRepository.findByArticleDate(date);
    }
}