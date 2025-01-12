package dev.wybran.qrverse.controller;

import dev.wybran.qrverse.dto.request.QrCodeRequest;
import dev.wybran.qrverse.dto.response.QRCodeResponse;
import dev.wybran.qrverse.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/qr-codes")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @PostMapping
    public ResponseEntity<QRCodeResponse> createQrCode(
            Principal principal,
            @RequestBody QrCodeRequest qrCodeRequest) {
        QRCodeResponse response = qrCodeService.createQrCode(principal.getName(), qrCodeRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<String> getQrCodeLink(
            @PathVariable String uuid,
            @RequestHeader(value = "Authorization", required = false) String password) {
        return qrCodeService.getQrCodeByUuid(uuid)
                .map(qrCode -> {
                    if (qrCode.getPassword() != null) {
                        if (password == null) {
                            return ResponseEntity.status(401).body("Password required");
                        } else if (!validatePassword(password, qrCode.getPassword())) {
                            return ResponseEntity.status(401).body("Invalid password");
                        }
                    }

                    qrCodeService.incrementClickCount(uuid);

                    String link = qrCode.getLink();
                    if (!link.startsWith("http")) {
                        link = "http://" + link;
                    }

                    return ResponseEntity.ok(link);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<QRCodeResponse>> getUserQrCodes(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        List<QRCodeResponse> userQrCodes = qrCodeService.getUserQrCodes(principal.getName());
        return ResponseEntity.ok(userQrCodes);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<QRCodeResponse> updateQrCode(
            @PathVariable String uuid,
            @RequestBody QrCodeRequest updatedQrCode) {
        try {
            QRCodeResponse updated = qrCodeService.updateQrCode(uuid, updatedQrCode);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteQrCode(@PathVariable String uuid) {
        try {
            qrCodeService.deleteQrCode(uuid);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean validatePassword(String providedPassword, String storedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(providedPassword, storedPassword);
    }
}