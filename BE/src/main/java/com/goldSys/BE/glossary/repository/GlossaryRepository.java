package com.goldSys.BE.glossary.repository;

import com.goldSys.BE.glossary.entity.Glossary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GlossaryRepository extends JpaRepository<Glossary, Long> {
    List<Glossary> findByTermContainingIgnoreCase(String term);
}
