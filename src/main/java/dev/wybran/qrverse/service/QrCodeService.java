package dev.wybran.qrverse.service;

import dev.wybran.qrverse.dto.request.QrCodeRequest;
import dev.wybran.qrverse.dto.response.QRCodeResponse;
import dev.wybran.qrverse.dto.response.UserResponse;
import dev.wybran.qrverse.model.QrCode;
import dev.wybran.qrverse.repository.QrCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public QRCodeResponse createQrCode(String userEmail, QrCodeRequest qrCodeRequest) {
        UserResponse user = userService.getUserResponse(userEmail);

        QrCode qrCode = new QrCode();
        qrCode.setUserId(user.getId());
        qrCode.setLink(qrCodeRequest.getLink());

        if (qrCodeRequest.getPassword() != null) {
            String encryptedPassword = passwordEncoder.encode(qrCodeRequest.getPassword());
            qrCode.setPassword(encryptedPassword);
        }

        QrCode savedQrCode = qrCodeRepository.save(qrCode);

        return mapToResponse(savedQrCode);
    }

    public Optional<QrCode> getQrCodeByUuid(String uuid) {
        return qrCodeRepository.findByUuid(uuid);
    }

    public List<QRCodeResponse> getUserQrCodes(String userEmail) {
        UserResponse user = userService.getUserResponse(userEmail);
        return qrCodeRepository.findAllByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public QRCodeResponse updateQrCode(String uuid, QrCodeRequest updatedQrCode) {
        QrCode existingQrCode = qrCodeRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("QR Code not found"));

        updateQrCodeFields(existingQrCode, updatedQrCode);

        QrCode updatedQrCodeEntity = qrCodeRepository.save(existingQrCode);

        return mapToResponse(updatedQrCodeEntity);
    }

    public void deleteQrCode(String uuid) {
        qrCodeRepository.deleteByUuid(uuid);
    }

    public void incrementClickCount(String uuid) {
        QrCode qrCode = qrCodeRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("QR Code not found"));

        qrCode.setClickCount(qrCode.getClickCount() + 1);
        qrCodeRepository.save(qrCode);
    }


    private void updateQrCodeFields(QrCode existingQrCode, QrCodeRequest updatedQrCode) {
        if (updatedQrCode.getLink() != null) {
            existingQrCode.setLink(updatedQrCode.getLink());
        }
        if (updatedQrCode.getPassword() != null) {
            String encryptedPassword = passwordEncoder.encode(updatedQrCode.getPassword());
            existingQrCode.setPassword(encryptedPassword);
        }
    }

    private QRCodeResponse mapToResponse(QrCode qrCode) {
        return new QRCodeResponse(
                qrCode.getUuid(),
                qrCode.getLink(),
                qrCode.getClickCount(),
                qrCode.getPassword() != null,
                qrCode.getCreatedAt().toString(),
                qrCode.getUpdatedAt().toString()
        );
    }
}