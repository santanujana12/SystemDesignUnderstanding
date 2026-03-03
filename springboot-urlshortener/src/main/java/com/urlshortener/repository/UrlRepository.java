package com.urlshortener.repository;

import com.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByCode(String code);

    Optional<Url> findByCodeAndExpiresAtAfter(String code, LocalDateTime now);

    Optional<Url> findByOriginalUrlAndExpiresAtAfter(String originalUrl, LocalDateTime now);

    boolean existsByCode(String code);

    @Modifying
    @Query("DELETE FROM Url u WHERE u.expiresAt <= :now")
    int deleteExpiredUrls(LocalDateTime now);
}
