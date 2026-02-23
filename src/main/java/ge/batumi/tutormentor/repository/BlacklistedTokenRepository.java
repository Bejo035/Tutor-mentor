package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.BlacklistedTokenDb;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistedTokenRepository extends MongoRepository<BlacklistedTokenDb, String> {
    boolean existsByTokenId(String tokenId);
}
