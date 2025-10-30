package com.goldSys.BE.glossary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "glossary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Glossary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String term;
    private String category;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(columnDefinition = "TEXT")
    private String summary;
}
