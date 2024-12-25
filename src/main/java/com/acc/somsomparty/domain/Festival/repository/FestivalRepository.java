package com.acc.somsomparty.domain.Festival.repository;

import com.acc.somsomparty.domain.Festival.entity.Festival;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
    Page<Festival> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Festival> findByCreatedAtLessThanOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable pageable);
    List<Festival> findByStartDate(LocalDate localDate);

    @Query("SELECT f FROM Festival f WHERE (f.nameLower LIKE CONCAT('%', :keyword, '%') " +
            "OR f.descriptionLower LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:lastId = 0 OR f.id < :lastId) " +
            "ORDER BY f.id DESC")
    List<Festival> searchByKeyword(Long lastId, int limit, String keyword);
}
