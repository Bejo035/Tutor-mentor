package ge.batumi.tutormentor.security;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Adapter that wraps a {@link UserDb} entity to satisfy the Spring Security
 * {@link UserDetails} contract, keeping the domain model free of framework concerns.
 */
@Data
public class UserDetailsAdapter implements UserDetails {
    private final UserDb userDb;

    public UserDetailsAdapter(UserDb userDb) {
        this.userDb = userDb;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDb.getRoles().stream().map(UserRole::name).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return userDb.getPassword();
    }

    @Override
    public String getUsername() {
        return userDb.getUsername();
    }
}
