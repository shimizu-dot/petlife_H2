package com.example.petlife.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DatabaseBackupService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseBackupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public byte[] backup() throws Exception {
        Path tempFile = Files.createTempFile("petlife-h2-backup-", ".sql");
        try {
            jdbcTemplate.execute("SCRIPT TO '" + escapeH2Path(tempFile) + "'");
            return Files.readAllBytes(tempFile);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public void restore(byte[] sqlContent) throws Exception {
        Path tempFile = Files.createTempFile("petlife-h2-restore-", ".sql");
        try {
            Files.writeString(tempFile, new String(sqlContent, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            jdbcTemplate.execute("DROP ALL OBJECTS");
            jdbcTemplate.execute("RUNSCRIPT FROM '" + escapeH2Path(tempFile) + "'");
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private String escapeH2Path(Path path) {
        return path.toAbsolutePath().toString().replace("'", "''");
    }
}
