package ge.batumi.tutormentor.model.db;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.batumi.tutormentor.model.request.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
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
    private String username;
    private String password;
    private List<UserRole> roles;
    private List<UserProgramRole> programRoles;
    private boolean confirmed = false;
    @JsonIgnore
    private Map<UserProgramRole, List<String>> programRoleToProgramSchemeMap = new HashMap<>();

    public UserDb(UserRequest request) {
        BeanUtils.copyProperties(request, this);
    }

    @JsonAnyGetter
    public Map<UserProgramRole, List<String>> getFlattenedMap() {
        return programRoleToProgramSchemeMap;
    }
}
