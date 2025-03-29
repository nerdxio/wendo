package app.wendo.users.repositories;

import app.wendo.users.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByIdentifierAndCodeAndUsedFalse(String identifier, String code);
    Optional<Otp> findTopByIdentifierOrderByCreatedAtDesc(String identifier);
}
