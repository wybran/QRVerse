package dev.wybran.qrverse.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class QrCodeRequest {
    private final String link;
}