package com.example.petlife.mapper;

import com.example.petlife.entity.MedicalAttachmentEntity;
import com.example.petlife.util.RecordParams;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MedicalAttachmentMapper {

    @Select("""
        SELECT id, medical_history_id, file_name, file_path, file_mime_type,
               file_size_bytes, description, uploaded_at, deleted_at, created_at, updated_at
        FROM medical_attachments
        WHERE medical_history_id = #{historyId} AND deleted_at IS NULL
        ORDER BY uploaded_at DESC
        """)
    List<MedicalAttachmentEntity> findByHistoryId(@Param("historyId") Long historyId);

    @Select("""
        SELECT id, medical_history_id, file_name, file_path, file_mime_type,
               file_size_bytes, description, uploaded_at, deleted_at, created_at, updated_at
        FROM medical_attachments WHERE id = #{id} AND deleted_at IS NULL
        """)
    MedicalAttachmentEntity findById(@Param("id") Long id);

    // H2 は INSERT...RETURNING 未対応のため、Map 経由の useGeneratedKeys で生成IDを取得する
    // （エンティティは Java Record で不変のため、Record 自体には ID を書き戻せない）
    @Insert("""
        INSERT INTO medical_attachments(medical_history_id, file_name, file_path,
            file_mime_type, file_size_bytes, description, uploaded_at, created_at, updated_at)
        VALUES(#{medicalHistoryId}, #{fileName}, #{filePath},
            #{fileMimeType}, #{fileSizeBytes}, #{description}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRaw(Map<String, Object> row);

    default Long insertReturningId(MedicalAttachmentEntity attachment) {
        Map<String, Object> params = RecordParams.toMap(attachment);
        insertRaw(params);
        return ((Number) params.get("id")).longValue();
    }

    @Update("""
        UPDATE medical_attachments
        SET deleted_at = #{deletedAt}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id} AND deleted_at IS NULL
        """)
    int softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
}
