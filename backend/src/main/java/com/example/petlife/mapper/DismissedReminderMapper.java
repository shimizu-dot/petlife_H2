package com.example.petlife.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

@Mapper
public interface DismissedReminderMapper {

    @Insert("""
        MERGE INTO dismissed_reminders(user_id, reminder_key, dismissed_at)
        KEY (user_id, reminder_key)
        VALUES(#{userId}, #{reminderKey}, CURRENT_TIMESTAMP)
        """)
    int insert(@Param("userId") Long userId, @Param("reminderKey") String reminderKey);

    @Select("SELECT reminder_key FROM dismissed_reminders WHERE user_id = #{userId}")
    Set<String> findKeysByUserId(@Param("userId") Long userId);
}
