package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.AuthRequest;
import ge.batumi.tutormentor.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ARepositoryService<UserRepository, UserDb, String> {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository repository, AuthenticationManager authenticationManager, JwtService jwtService) {
        super(repository);
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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

    public UserDb findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    public String verify(AuthRequest authRequest) {
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        if (authenticate.isAuthenticated()) {
            return jwtService.generateToken(authRequest);
        }

        return "Fail";
    }
}
