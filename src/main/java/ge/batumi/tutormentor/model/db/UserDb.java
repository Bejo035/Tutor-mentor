package ge.batumi.tutormentor.model.db;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
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
    @JsonIgnore
    private Map<UserProgramRole, List<String>> programRoleToProgramSchemeMap = new HashMap<>();

    @JsonAnyGetter
    public Map<UserProgramRole, List<String>> getFlattenedMap() {
        return programRoleToProgramSchemeMap;
    }
}
