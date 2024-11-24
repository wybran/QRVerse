package dev.wybran.qrverse.service;

import dev.wybran.qrverse.model.QrCode;
import dev.wybran.qrverse.repository.QrCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QrCodeService {
    private final QrCodeRepository qrCodeRepository;

    public QrCode createQrCode(QrCode qrCode) {
        return qrCodeRepository.save(qrCode);
    }

    public Optional<QrCode> getQrCodeByUuid(String uuid) {
        return qrCodeRepository.findByUuid(uuid);
    }

    public List<QrCode> getUserQrCodes(Long userId) {
        return qrCodeRepository.findAllByUserId(userId);
    }

    public QrCode updateQrCode(QrCode qrCode) {
        return qrCodeRepository.save(qrCode);
    }

    public void deleteQrCode(String uuid) {
        qrCodeRepository.deleteByUuid(uuid);
    }

    public void incrementClickCount(String uuid) {
        qrCodeRepository.findByUuid(uuid).ifPresent(qrCode -> {
            qrCode.setClickCount(qrCode.getClickCount() + 1);
            qrCodeRepository.save(qrCode);
        });
    }
}