package com.example.petlife.mapper;

import com.example.petlife.dto.subscription.SubscriptionRow;
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

@SpringBootTest
@Transactional
class SubscriptionMapperTest {

    @Autowired SubscriptionMapper subscriptionMapper;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void findUpcomingRenewalsByUserIdWorksOnH2WithoutLateral() {
        String email = "notification-test-" + UUID.randomUUID() + "@petlife.local";

        jdbcTemplate.update("""
            INSERT INTO users (role_id, name, email, password_hash, status)
            VALUES (3, ?, ?, 'hash', 'ACTIVE')
            """, "通知テストユーザー", email);

        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE email = ?",
                Long.class,
                email
        );

        jdbcTemplate.update("""
            INSERT INTO pets (owner_user_id, name, species)
            VALUES (?, 'テスト犬', 'DOG')
            """, userId);

        Long planId = jdbcTemplate.queryForObject(
                "SELECT id FROM plans WHERE name = 'STANDARD'",
                Long.class
        );

        jdbcTemplate.update("""
            INSERT INTO subscriptions (user_id, plan_id, start_date, end_date, status, auto_renew)
            VALUES (?, ?, CURRENT_DATE, DATEADD('DAY', 7, CURRENT_DATE), 'ACTIVE', TRUE)
            """, userId, planId);

        List<SubscriptionRow> rows = subscriptionMapper.findUpcomingRenewalsByUserId(userId);

        assertFalse(rows.isEmpty());
        SubscriptionRow row = rows.get(0);
        assertEquals("通知テストユーザー", row.ownerName());
        assertNotNull(row.petName());
    }
}
