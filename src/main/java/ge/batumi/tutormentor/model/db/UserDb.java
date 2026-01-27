package ge.batumi.tutormentor.model.db;


import ge.batumi.tutormentor.model.request.RegisterRequest;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.model.response.UserData;
import ge.batumi.tutormentor.security.config.SecurityConfig;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    private boolean confirmed = false;
    private String password;
    private List<UserRole> roles = new ArrayList<>();
    private List<UserProgramRole> programRoles = new ArrayList<>();
    private Integer rating;

    public UserData toUserData() {
        return new UserData(id, name, surname, workingPlace, workingPosition);
    }

    public UserDb(UserRequest request) {
        BeanUtils.copyProperties(request, this);
        setPassword(request.getPassword());
    }

    public UserDb(RegisterRequest request) throws BadRequestException {
        if (request.getUserRole().equals(UserRole.ADMIN)) {
            throw new BadRequestException("'userRole' can only be %s".formatted(Arrays.stream(UserRole.values()).filter(userRole -> userRole != UserRole.ADMIN).map(UserRole::toString).toList()));
        }
        BeanUtils.copyProperties(request, this);
        roles.add(request.getUserRole());
        programRoles.add(request.getProgramRole());
        setPassword(request.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(UserRole::name).map(SimpleGrantedAuthority::new).toList();
    }

    public void setPassword(String password) {
        this.password = SecurityConfig.passwordEncoder().encode(password);
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
