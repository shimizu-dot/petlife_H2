package com.example.petlife.mapper;

import com.example.petlife.entity.RoleEntity;
import com.example.petlife.util.RecordParams;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper {

    @Select("SELECT id, role_code, role_name, created_at, updated_at FROM roles ORDER BY id")
    List<RoleEntity> findAll();

    @Select("SELECT id, role_code, role_name, created_at, updated_at FROM roles WHERE id = #{id}")
    RoleEntity findById(@Param("id") Long id);

    @Select("SELECT id, role_code, role_name, created_at, updated_at FROM roles WHERE role_code = #{roleCode}")
    RoleEntity findByCode(@Param("roleCode") String roleCode);

    // H2 は INSERT...RETURNING 未対応のため、Map 経由の useGeneratedKeys で生成IDを取得する
    // （エンティティは Java Record で不変のため、Record 自体には ID を書き戻せない）
    @Insert("""
        INSERT INTO roles(role_code, role_name, created_at, updated_at)
        VALUES(#{roleCode}, #{roleName}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRaw(Map<String, Object> row);

    default Long insertReturningId(RoleEntity role) {
        Map<String, Object> params = RecordParams.toMap(role);
        insertRaw(params);
        return ((Number) params.get("id")).longValue();
    }

    @Update("""
        UPDATE roles
        SET role_code = #{roleCode}, role_name = #{roleName}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(RoleEntity role);
}
