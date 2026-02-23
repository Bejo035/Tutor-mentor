package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.BlacklistedTokenDb;
import ge.batumi.tutormentor.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service for managing JWT token revocation via a blacklist stored in MongoDB.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Adds a token to the blacklist so it is rejected on subsequent use.
     *
     * @param tokenId   the JWT ID (jti) claim.
     * @param expiresAt when the token naturally expires (used for TTL-based cleanup).
     */
    public void blacklist(String tokenId, Instant expiresAt) {
        if (!isBlacklisted(tokenId)) {
            blacklistedTokenRepository.save(new BlacklistedTokenDb(tokenId, expiresAt));
        }
    }

    /**
     * Checks whether a token has been blacklisted.
     *
     * @param tokenId the JWT ID (jti) claim.
     * @return {@code true} if the token is revoked.
     */
    public boolean isBlacklisted(String tokenId) {
        return blacklistedTokenRepository.existsByTokenId(tokenId);
    }
}
