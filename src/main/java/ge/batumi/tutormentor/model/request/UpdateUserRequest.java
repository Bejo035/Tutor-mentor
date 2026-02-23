package ge.batumi.tutormentor.model.request;

import ge.batumi.tutormentor.model.db.UserProgramRole;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private List<UserProgramRole> programRoles;
    @Size(max = 50, message = "Year must not exceed 50 characters")
    private String year;
    @Size(max = 1000, message = "Strengths must not exceed 1000 characters")
    private String strengths;
    @Size(max = 1000, message = "Motivation must not exceed 1000 characters")
    private String motivation;
    @Size(max = 500, message = "Keywords must not exceed 500 characters")
    private String keywords;
    @Size(max = 2000, message = "User feedback must not exceed 2000 characters")
    private String userFeedback;
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    @Size(max = 100, message = "Surname must not exceed 100 characters")
    private String surname;
    @Size(max = 200, message = "Working place must not exceed 200 characters")
    private String workingPlace;
    @Size(max = 200, message = "Working position must not exceed 200 characters")
    private String workingPosition;
    @Size(max = 2000, message = "Experience must not exceed 2000 characters")
    private String experience;
    @Size(max = 200, message = "Mentoring course name must not exceed 200 characters")
    private String mentoringCourseName;
    @Size(max = 2000, message = "Course description must not exceed 2000 characters")
    private String courseDescription;
    @Size(max = 1000, message = "Expectations must not exceed 1000 characters")
    private String expectations;
    @Size(max = 500, message = "Hobbies must not exceed 500 characters")
    private String hobbies;
}
