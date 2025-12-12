package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.repository.UserRepository;
import ge.batumi.tutormentor.utils.UserMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserService extends ARepositoryService<UserRepository, UserDb, String> implements UserDetailsService {
    private final UserMapper userMapper;

    public UserService(UserRepository repository, UserMapper userMapper) {
        super(repository);
        this.userMapper = userMapper;
    }

    public UserDb save(UserDb userDb) {
        return repository.save(userDb);
    }

    public UserDb addUser(UserRequest request) {
        return repository.save(new UserDb(request));
    }

    public UserDb updateUser(String id, UserRequest request) throws ResourceNotFoundException {
        UserDb userDb = findById(id);
        userMapper.copyProperties(request, userDb);// TODO here additional checks is needed!!!
        return repository.save(userDb);
    }

    public UserDb updateUser(Principal userPrincipal, UserRequest request) throws ResourceNotFoundException {
        UserDb userDb = loadUserByUsername(userPrincipal.getName());
        if (userDb == null) {
            throw new ResourceNotFoundException("Could not find user for '%s' username".formatted(userPrincipal.getName()));
        }
        userMapper.copyProperties(request, userDb);// TODO here additional checks is needed!!!
        return repository.save(userDb);
    }

    public List<UserDb> findAllById(List<String> userIds) {
        return repository.findAllById(userIds);
    }

    public void deleteUser(String id) {
        repository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public UserDb loadUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public boolean confirmUser(String id) throws ResourceNotFoundException {
        UserDb userDb = findById(id);
        if (userDb.isConfirmed()) {
            return false;
        }
        userDb.setConfirmed(true);
        repository.save(userDb);
        return true;
    }
}
