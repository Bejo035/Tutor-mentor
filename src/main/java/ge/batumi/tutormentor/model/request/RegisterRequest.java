package ge.batumi.tutormentor.model.request;

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
    private UserRole userRole;
}
