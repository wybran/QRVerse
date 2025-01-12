package dev.wybran.qrverse.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "qr_codes")
public class QrCode {

    @Id
    private String uuid;

    private Long userId;

    private String link;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private long clickCount;

    @Nullable
    private String password;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        this.uuid = UUID.randomUUID().toString();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}