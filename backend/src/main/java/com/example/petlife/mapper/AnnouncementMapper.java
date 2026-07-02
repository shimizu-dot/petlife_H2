package com.example.petlife.mapper;

import com.example.petlife.entity.AnnouncementEntity;
import com.example.petlife.util.RecordParams;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnnouncementMapper {

    @Select("""
        SELECT id, title, body, is_active AS "isActive", created_by_user_id AS "createdByUserId",
               created_at AS "createdAt", updated_at AS "updatedAt"
        FROM announcements
        WHERE is_active = TRUE
        ORDER BY created_at DESC
        """)
    List<AnnouncementEntity> findActive();

    @Select("""
        SELECT id, title, body, is_active AS "isActive", created_by_user_id AS "createdByUserId",
               created_at AS "createdAt", updated_at AS "updatedAt"
        FROM announcements
        ORDER BY created_at DESC
        """)
    List<AnnouncementEntity> findAll();

    // H2 は INSERT...RETURNING 未対応のため、Map 経由の useGeneratedKeys で生成IDを取得する
    // （エンティティは Java Record で不変のため、Record 自体には ID を書き戻せない）
    @Insert("""
        INSERT INTO announcements(title, body, is_active, created_by_user_id, created_at, updated_at)
        VALUES(#{title}, #{body}, TRUE, #{createdByUserId}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRaw(Map<String, Object> row);

    default Long insertReturningId(AnnouncementEntity row) {
        Map<String, Object> params = RecordParams.toMap(row);
        insertRaw(params);
        return ((Number) params.get("id")).longValue();
    }

    @Update("""
        UPDATE announcements SET is_active = #{isActive}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int updateIsActive(@Param("id") Long id, @Param("isActive") boolean isActive);

    @Update("DELETE FROM announcements WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
