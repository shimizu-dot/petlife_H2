package com.example.petlife.util;

import java.lang.reflect.RecordComponent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts an immutable Java record into a mutable {@link Map} keyed by
 * component name, so MyBatis can bind {@code #{property}} placeholders and
 * write a DB-generated key back into the map via {@code useGeneratedKeys}
 * (records have no setters, so the key cannot be written back onto the
 * record itself).
 */
public final class RecordParams {

    private RecordParams() {
    }

    public static Map<String, Object> toMap(Record record) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (RecordComponent component : record.getClass().getRecordComponents()) {
            try {
                map.put(component.getName(), component.getAccessor().invoke(record));
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to read record component: " + component.getName(), e);
            }
        }
        return map;
    }
}
