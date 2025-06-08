package ge.batumi.tutormentor.model.db;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDb {
    @Id
    private String id;
    private String userName;
    private String password;
    private List<UserRole> roles;
    private List<UserProgramRole> programRoles;
    private boolean confirmed = false;
    private Map<UserProgramRole, List<String>> programRoleToProgramSchemeMap;
    @JsonAnyGetter
    public Map<UserProgramRole, List<String>> getFlattenedMap() {
        return programRoleToProgramSchemeMap;
    }
}
