package com.example.petlife.mapper;

import com.example.petlife.entity.EmailTemplateEntity;
import com.example.petlife.util.RecordParams;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmailTemplateMapper {

    @Select("""
        SELECT id, template_code, subject_template, body_template, is_active, created_at, updated_at
        FROM email_templates
        WHERE is_active = TRUE
        ORDER BY template_code
        """)
    List<EmailTemplateEntity> findAllActive();

    @Select("""
        SELECT id, template_code, subject_template, body_template, is_active, created_at, updated_at
        FROM email_templates WHERE id = #{id}
        """)
    EmailTemplateEntity findById(@Param("id") Long id);

    @Select("""
        SELECT id, template_code, subject_template, body_template, is_active, created_at, updated_at
        FROM email_templates WHERE template_code = #{templateCode}
        """)
    EmailTemplateEntity findByCode(@Param("templateCode") String templateCode);

    // H2 は INSERT...RETURNING 未対応のため、Map 経由の useGeneratedKeys で生成IDを取得する
    // （エンティティは Java Record で不変のため、Record 自体には ID を書き戻せない）
    @Insert("""
        INSERT INTO email_templates(template_code, subject_template, body_template, is_active, created_at, updated_at)
        VALUES(#{templateCode}, #{subjectTemplate}, #{bodyTemplate}, #{isActive}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRaw(Map<String, Object> row);

    default Long insertReturningId(EmailTemplateEntity template) {
        Map<String, Object> params = RecordParams.toMap(template);
        insertRaw(params);
        return ((Number) params.get("id")).longValue();
    }

    @Update("""
        UPDATE email_templates
        SET subject_template = #{subjectTemplate}, body_template = #{bodyTemplate},
            is_active = #{isActive}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(EmailTemplateEntity template);
}
