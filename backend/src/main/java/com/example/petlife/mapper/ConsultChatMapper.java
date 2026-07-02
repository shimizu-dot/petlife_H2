package com.example.petlife.mapper;

import com.example.petlife.entity.ConsultChatMessageEntity;
import com.example.petlife.util.RecordParams;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsultChatMapper {

    // H2 は INSERT...RETURNING 未対応のため、Map 経由の useGeneratedKeys で生成IDを取得する
    // （エンティティは Java Record で不変のため、Record 自体には ID を書き戻せない）
    @Insert("""
        INSERT INTO consult_chat_messages(user_id, sender_type, message, created_at)
        VALUES(#{userId}, #{senderType}, #{message}, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRaw(Map<String, Object> row);

    default Long insertReturningId(ConsultChatMessageEntity row) {
        Map<String, Object> params = RecordParams.toMap(row);
        insertRaw(params);
        return ((Number) params.get("id")).longValue();
    }

    @Select("""
        SELECT id, user_id, sender_type, message, created_at
        FROM consult_chat_messages
        WHERE user_id = #{userId}
        ORDER BY id DESC
        LIMIT #{limit}
        """)
    List<ConsultChatMessageEntity> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
