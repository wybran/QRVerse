package dev.wybran.qrverse.controller;

import dev.wybran.qrverse.dto.request.QrCodeRequest;
import dev.wybran.qrverse.model.QrCode;
import dev.wybran.qrverse.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> redirectToLink(@PathVariable String uuid) {
        QrCode qrCode = qrCodeService.getQrCodeByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("QR code not found"));

        qrCodeService.incrementClickCount(uuid);

        return ResponseEntity.status(302)
                .header("Location", qrCode.getLink())
                .build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QrCode>> getUserQrCodes(@PathVariable Long userId) {
        return ResponseEntity.ok(qrCodeService.getUserQrCodes(userId));
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