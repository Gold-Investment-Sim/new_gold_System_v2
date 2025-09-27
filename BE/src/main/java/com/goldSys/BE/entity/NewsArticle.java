package com.goldSys.BE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "NEWS_ARTICLE")
public class NewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    private String articleDate;
    private String articleTitle;
    private String articleContent;
    private String url;
    private Float sentimentScore;
    private String summaryFull;
}
