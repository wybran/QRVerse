package dev.wybran.qrverse.model;

import jakarta.persistence.*;
import lombok.Data;

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

    private boolean dynamic;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private long clickCount;

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