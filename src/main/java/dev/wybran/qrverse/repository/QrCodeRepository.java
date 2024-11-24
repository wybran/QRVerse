package dev.wybran.qrverse.repository;

import dev.wybran.qrverse.model.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    List<QrCode> findAllByUserId(Long userId);
    Optional<QrCode> findByUuid(String uuid);

    void deleteByUuid(String uuid);

}