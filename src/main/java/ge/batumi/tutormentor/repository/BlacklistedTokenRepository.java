package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.BlacklistedTokenDb;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for {@link BlacklistedTokenDb} entities with token-ID existence checks.
 */
public interface BlacklistedTokenRepository extends MongoRepository<BlacklistedTokenDb, String> {
    boolean existsByTokenId(String tokenId);
}
