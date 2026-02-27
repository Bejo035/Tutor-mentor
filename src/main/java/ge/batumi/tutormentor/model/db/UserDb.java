package ge.batumi.tutormentor.model.db;


import ge.batumi.tutormentor.exceptions.BadRequestException;
import ge.batumi.tutormentor.model.request.RegisterRequest;
import ge.batumi.tutormentor.model.request.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MongoDB document representing a registered user, including profile data, roles, and credentials.
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDb {
    @Id
    private String id;
    @Indexed(unique = true)
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
    @Indexed(unique = true)
    private String username;
    private boolean confirmed = false;
    private String password;
    private List<UserRole> roles = new ArrayList<>();
    private List<UserProgramRole> programRoles = new ArrayList<>();
    private Integer rating;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public UserDb(UserRequest request) {
        BeanUtils.copyProperties(request, this);
    }

    public UserDb(RegisterRequest request) {
        if (request.getUserRole().equals(UserRole.ADMIN)) {
            throw new BadRequestException("'userRole' can only be %s".formatted(Arrays.stream(UserRole.values()).filter(userRole -> userRole != UserRole.ADMIN).map(UserRole::toString).toList()));
        }
        BeanUtils.copyProperties(request, this);
        roles.add(request.getUserRole());
        programRoles.add(request.getProgramRole());
    }

}
