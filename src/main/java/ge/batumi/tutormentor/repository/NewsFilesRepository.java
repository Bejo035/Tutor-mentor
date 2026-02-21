package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.NewsFileDb;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsFilesRepository extends MongoRepository<NewsFileDb, String> {

    List<NewsFileDb> findAllByNewsId(String newsId);

    List<NewsFileDb> findByNewsIdAndKey(String newsId, String key);
}
