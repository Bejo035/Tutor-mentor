package ge.batumi.tutormentor.model.response;

import ge.batumi.tutormentor.model.db.UserProgramRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
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
    private boolean confirmed;
    private List<UserProgramRole> programRoles;
    private String profileImageId;
}
