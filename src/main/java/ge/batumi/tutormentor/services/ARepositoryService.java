package ge.batumi.tutormentor.services;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public abstract class ARepositoryService<R extends MongoRepository<K, V>, K, V> {
    protected final R repository;

    public ARepositoryService(R repository) {
        this.repository = repository;
    }

    public Optional<K> findById(V id) {
        return repository.findById(id);
    }

    public List<K> findAll() {
        return repository.findAll();
    }
}
