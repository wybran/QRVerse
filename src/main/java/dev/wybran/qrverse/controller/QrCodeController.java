package dev.wybran.qrverse.controller;

import dev.wybran.qrverse.dto.request.QrCodeRequest;
import dev.wybran.qrverse.model.QrCode;
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
    public ResponseEntity<QrCode> createQrCode(Principal principal, @RequestBody QrCodeRequest qrCode) {
        return ResponseEntity.ok(qrCodeService.createQrCode(principal.getName(), qrCode));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<String> getQrCodeLink(
            @PathVariable String uuid,
            @RequestHeader(value = "Authorization", required = false) String password
    ) {
        QrCode qrCode = qrCodeService.getQrCodeByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("QR code not found"));

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
    }

    private boolean validatePassword(String authorizationHeader, String storedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(authorizationHeader, storedPassword);
    }

    @GetMapping
    public ResponseEntity<List<QrCode>> getUserQrCodes(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(qrCodeService.getUserQrCodes(principal.getName()));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<QrCode> updateQrCode(@PathVariable String uuid, @RequestBody QrCode updatedQrCode) {
        return qrCodeService.getQrCodeByUuid(uuid)
                .map(existingQrCode -> {
                    updatedQrCode.setUuid(uuid);
                    return ResponseEntity.ok(qrCodeService.updateQrCode(updatedQrCode));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteQrCode(@PathVariable String uuid) {
        qrCodeService.deleteQrCode(uuid);
        return ResponseEntity.noContent().build();
    }
}