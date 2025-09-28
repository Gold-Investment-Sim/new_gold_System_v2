package com.goldSys.BE.news.repository;

import com.goldSys.BE.news.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    List<NewsArticle> findByArticleDate(String articleDate);
}
