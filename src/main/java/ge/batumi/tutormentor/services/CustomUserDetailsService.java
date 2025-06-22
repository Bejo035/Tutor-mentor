package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.repository.UserRepository;
import ge.batumi.tutormentor.security.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDb userDb = userRepository.findByUserName(username);
        if (userDb == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return new UserPrincipal(userDb);
    }
}
