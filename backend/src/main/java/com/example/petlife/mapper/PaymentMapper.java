package com.example.petlife.mapper;

import com.example.petlife.entity.PaymentEntity;
import com.example.petlife.util.RecordParams;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentMapper {

    @Select("""
        SELECT id, invoice_id, paid_amount, paid_at, payment_method,
               transaction_ref, status, deleted_at, created_at, updated_at
        FROM payments
        WHERE invoice_id = #{invoiceId}
          AND deleted_at IS NULL
        ORDER BY created_at DESC
        """)
    List<PaymentEntity> findByInvoiceId(@Param("invoiceId") Long invoiceId);

    @Select("""
        SELECT id, invoice_id, paid_amount, paid_at, payment_method,
               transaction_ref, status, deleted_at, created_at, updated_at
        FROM payments
        WHERE id = #{id} AND deleted_at IS NULL
        """)
    PaymentEntity findById(@Param("id") Long id);

    // H2 は INSERT...RETURNING 未対応のため、Map 経由の useGeneratedKeys で生成IDを取得する
    // （エンティティは Java Record で不変のため、Record 自体には ID を書き戻せない）
    @Insert("""
        INSERT INTO payments(invoice_id, paid_amount, paid_at, payment_method,
            transaction_ref, status, created_at, updated_at)
        VALUES(#{invoiceId}, #{paidAmount}, #{paidAt}, #{paymentMethod},
            #{transactionRef}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRaw(Map<String, Object> row);

    default Long insertReturningId(PaymentEntity payment) {
        Map<String, Object> params = RecordParams.toMap(payment);
        insertRaw(params);
        return ((Number) params.get("id")).longValue();
    }

    @Update("""
        UPDATE payments
        SET status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id} AND deleted_at IS NULL
        """)
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("""
        UPDATE payments
        SET deleted_at = #{deletedAt}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id} AND deleted_at IS NULL
        """)
    int softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
}
