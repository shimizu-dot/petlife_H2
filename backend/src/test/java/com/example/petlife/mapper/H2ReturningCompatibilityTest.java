package com.example.petlife.mapper;

import com.example.petlife.dto.subscription.SubscriptionRow;
import com.example.petlife.entity.NotificationEntity;
import com.example.petlife.entity.PetEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PostgreSQL の INSERT...RETURNING / LEFT JOIN LATERAL は H2 で構文エラーになるため、
 * insertReturningId() を Map + useGeneratedKeys 方式に、LATERAL を相関サブクエリに
 * 置き換えた。ここでは代表的な Mapper で実際に H2 上で動くことを確認する。
 */
@SpringBootTest
@Transactional
class H2ReturningCompatibilityTest {

    @Autowired PetMapper petMapper;
    @Autowired NotificationMapper notificationMapper;
    @Autowired SubscriptionMapper subscriptionMapper;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void petMapperInsertReturningIdWorksOnH2() {
        PetEntity pet = new PetEntity(
                null, 1L, "テスト猫", "CAT", null, "MALE",
                null, null, null, null, null, null, null
        );

        Long newId = petMapper.insertReturningId(pet);

        assertNotNull(newId);
        PetEntity saved = petMapper.findById(newId);
        assertEquals("テスト猫", saved.name());
    }

    @Test
    void notificationMapperInsertReturningIdWorksOnH2() {
        NotificationEntity notification = new NotificationEntity(
                null, "INFO", "テスト通知", "本文", null, null,
                "SENT", 1L, null, null, null
        );

        Long newId = notificationMapper.insertReturningId(notification);

        assertNotNull(newId);
        NotificationEntity saved = notificationMapper.findById(newId);
        assertEquals("テスト通知", saved.title());
    }

    @Test
    void subscriptionMapperFindRowsByUserIdWorksOnH2WithoutLateral() {
        String email = "sub-mapper-test-" + UUID.randomUUID() + "@petlife.local";

        jdbcTemplate.update("""
            INSERT INTO users (role_id, name, email, password_hash, status)
            VALUES (3, ?, ?, 'hash', 'ACTIVE')
            """, "サブスクテストユーザー", email);

        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE email = ?", Long.class, email);

        jdbcTemplate.update("""
            INSERT INTO pets (owner_user_id, name, species)
            VALUES (?, 'サブスク犬', 'DOG')
            """, userId);

        Long planId = jdbcTemplate.queryForObject(
                "SELECT id FROM plans WHERE name = 'STANDARD'", Long.class);

        jdbcTemplate.update("""
            INSERT INTO subscriptions (user_id, plan_id, start_date, status, auto_renew)
            VALUES (?, ?, CURRENT_DATE, 'ACTIVE', TRUE)
            """, userId, planId);

        List<SubscriptionRow> rows = subscriptionMapper.findRowsByUserId(userId, 10, 0);

        assertFalse(rows.isEmpty());
        assertEquals("サブスク犬", rows.get(0).petName());
    }
}
