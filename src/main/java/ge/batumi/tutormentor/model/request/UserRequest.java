package ge.batumi.tutormentor.model.request;

import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.db.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private List<UserRole> roles;
    private List<UserProgramRole> programRoles;
    private boolean confirmed = false;
}
