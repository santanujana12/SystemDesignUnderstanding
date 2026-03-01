package com.urlshortener.repository;

import com.urlshortener.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    long countByCode(String code);

    @Query("SELECT DATE(a.timestamp) as date, COUNT(a) as clicks " +
           "FROM Analytics a " +
           "WHERE a.code = :code " +
           "AND a.timestamp >= :startDate " +
           "GROUP BY DATE(a.timestamp) " +
           "ORDER BY DATE(a.timestamp) DESC")
    List<Object[]> findClicksByDay(@Param("code") String code, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.referrer as referrer, COUNT(a) as clicks " +
           "FROM Analytics a " +
           "WHERE a.code = :code " +
           "GROUP BY a.referrer " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> findTopReferrers(@Param("code") String code);
}
