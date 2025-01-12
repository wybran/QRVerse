package dev.wybran.qrverse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QRCodeResponse {
    private final String uuid;
    private final String link;
    private final long clickCount;
    private final Boolean isProtected;
    private final String createdAt;
    private final String updatedAt;
}
