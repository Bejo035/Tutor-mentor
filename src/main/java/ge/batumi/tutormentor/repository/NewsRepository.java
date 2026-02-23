package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.NewsDb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for {@link NewsDb} entities.
 */
@Repository
public interface NewsRepository extends MongoRepository<NewsDb, String> {
}
