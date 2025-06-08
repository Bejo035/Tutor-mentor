package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ARepositoryService<UserRepository, UserDb, String> {
    public UserService(UserRepository repository) {
        super(repository);
    }

    public UserDb save(UserDb userDb) {
        return repository.save(userDb);
    }

    public List<UserDb> findAllById(List<String> userIds) {
        return repository.findAllById(userIds);
    }

    public long countByIdIn(List<String> userIds) {
        return repository.countByIdIn(userIds);
    }
}
