package ge.batumi.tutormentor.model.request;

import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.db.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private UserRole userRole = UserRole.STUDENT;
    private UserProgramRole programRole = UserProgramRole.SEEKER;
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
}
