package ge.batumi.tutormentor.model.db;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.batumi.tutormentor.model.request.RegisterRequest;
import ge.batumi.tutormentor.model.request.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDb implements UserDetails {
    @Id
    private String id;
    private String email;
    private String year;
    private String strengths;
    private String motivation;
    private String keywords;
    private String userFeedback;
    private String name;
    private String surname;
    private String workingPlace;
    private String workingPosition;
    private String experience;
    private String mentoringCourseName;
    private String courseDescription;
    private String expectations;
    private String hobbies;
    private String username;
    private String password;
    private List<UserRole> roles = new ArrayList<>();
    private List<UserProgramRole> programRoles = new ArrayList<>();
    private boolean confirmed = false;
    @JsonIgnore
    private Map<UserProgramRole, List<String>> programRoleToProgramSchemeMap = new HashMap<>();

    public UserDb(UserRequest request) {
        BeanUtils.copyProperties(request, this);
        this.password = new BCryptPasswordEncoder().encode(this.password);
    }

    public UserDb(RegisterRequest request) throws BadRequestException {
        BeanUtils.copyProperties(request, this);
        if (request.getUserRole().equals(UserRole.ADMIN)) {
            throw new BadRequestException("'userRole' can only be %s".formatted(Arrays.stream(UserRole.values()).filter(userRole -> userRole != UserRole.ADMIN).map(UserRole::toString).toList()));
        }
        roles.add(request.getUserRole());
        this.password = new BCryptPasswordEncoder().encode(this.password);
    }

    @JsonAnyGetter
    public Map<UserProgramRole, List<String>> getFlattenedMap() {
        return programRoleToProgramSchemeMap;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(UserRole::name).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
