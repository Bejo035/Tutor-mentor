package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public abstract class ARepositoryService<R extends MongoRepository<K, V>, K, V> {
    protected final R repository;

    public ARepositoryService(R repository) {
        this.repository = repository;
    }

    public K findById(V id) throws ResourceNotFoundException {
        Optional<K> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Could not find resource by %s id".formatted(id));
        }
        return entity.get();
    }

    public List<K> findAll() {
        return repository.findAll();
    }
}
