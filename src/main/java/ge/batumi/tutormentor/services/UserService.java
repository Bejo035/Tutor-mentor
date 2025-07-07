package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.repository.UserRepository;
import org.springframework.beans.BeanUtils;
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

    public UserDb addUser(UserRequest request) {
        return repository.save(new UserDb(request));
    }

    public UserDb updateUser(String id, UserRequest request) throws ResourceNotFoundException {
        UserDb userDb = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        BeanUtils.copyProperties(request, userDb); // TODO here additional checks is needed!!!
        return repository.save(userDb);
    }


    public List<UserDb> findAllById(List<String> userIds) {
        return repository.findAllById(userIds);
    }

    public long countByIdIn(List<String> userIds) {
        return repository.countByIdIn(userIds);
    }

    public void deleteUser(String id) {
        repository.deleteById(id);
    }
}
