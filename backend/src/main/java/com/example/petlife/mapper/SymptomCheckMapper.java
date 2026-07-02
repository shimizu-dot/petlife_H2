package com.example.petlife.mapper;

import com.example.petlife.entity.SymptomCheckEntity;
import com.example.petlife.util.RecordParams;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SymptomCheckMapper {

    // H2 は INSERT...RETURNING 未対応のため、Map 経由の useGeneratedKeys で生成IDを取得する
    // （エンティティは Java Record で不変のため、Record 自体には ID を書き戻せない）
    @Insert("""
        INSERT INTO symptom_checks(
            pet_id, requested_by_user_id, symptom_type, onset_text, memo,
            severity, recommendation, guidance, ai_model, created_at
        )
        VALUES(
            #{petId}, #{requestedByUserId}, #{symptomType}, #{onsetText}, #{memo},
            #{severity}, #{recommendation}, #{guidance}, #{aiModel}, CURRENT_TIMESTAMP
        )
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRaw(Map<String, Object> row);

    default Long insertReturningId(SymptomCheckEntity row) {
        Map<String, Object> params = RecordParams.toMap(row);
        insertRaw(params);
        return ((Number) params.get("id")).longValue();
    }

    @Select("""
        SELECT id, pet_id, requested_by_user_id, symptom_type, onset_text, memo,
               severity, recommendation, guidance, ai_model, created_at
        FROM symptom_checks
        WHERE pet_id = #{petId}
          AND deleted_at IS NULL
        ORDER BY id DESC
        LIMIT #{limit}
        """)
    List<SymptomCheckEntity> findRecentByPetId(@Param("petId") Long petId, @Param("limit") int limit);
}
