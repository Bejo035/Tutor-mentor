package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base service providing common CRUD operations backed by a {@link MongoRepository}.
 *
 * @param <R> the repository type
 * @param <K> the entity type
 * @param <V> the entity ID type
 */
public abstract class ARepositoryService<R extends MongoRepository<K, V>, K, V> {
    protected final R repository;

    public ARepositoryService(R repository) {
        this.repository = repository;
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id the entity ID.
     * @return the entity.
     * @throws ResourceNotFoundException if no entity exists for the given ID.
     */
    public K findById(V id) throws ResourceNotFoundException {
        Optional<K> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Could not find resource by %s id".formatted(id));
        }
        return entity.get();
    }

    /**
     * Returns all entities.
     */
    public List<K> findAll() {
        return repository.findAll();
    }

    /**
     * Returns a {@link Page} of entities matching the given {@link Pageable}.
     *
     * @param pageable pagination information.
     * @return a page of entities.
     */
    public Page<K> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
