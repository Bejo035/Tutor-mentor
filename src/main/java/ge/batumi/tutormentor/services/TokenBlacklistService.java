package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.BlacklistedTokenDb;
import ge.batumi.tutormentor.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklist(String tokenId, Instant expiresAt) {
        if (!isBlacklisted(tokenId)) {
            blacklistedTokenRepository.save(new BlacklistedTokenDb(tokenId, expiresAt));
        }
    }

    public boolean isBlacklisted(String tokenId) {
        return blacklistedTokenRepository.existsByTokenId(tokenId);
    }
}
