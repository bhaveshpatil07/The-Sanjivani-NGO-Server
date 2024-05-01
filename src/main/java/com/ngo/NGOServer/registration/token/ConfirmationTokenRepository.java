package com.ngo.NGOServer.registration.token;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, String> {

    Optional<ConfirmationToken> findByToken(String token);

    default int updateConfirmedAt(String token, LocalDateTime confirmedAt) {
        return findByToken(token)
                .map(confirmationToken -> {
                    confirmationToken.setConfirmedAt(confirmedAt);
                    save(confirmationToken);
                    return 1;
                })
                .orElse(0);
    }
}
