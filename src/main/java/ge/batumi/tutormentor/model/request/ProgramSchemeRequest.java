package ge.batumi.tutormentor.model.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSchemeRequest {
    private String title;
    private String description;
//    private Map<UserProgramRole, List<String>> userProgramRoleToUserMap = new HashMap<>();

//    @JsonAnySetter
//    public void addRoleMapping(String key, Object value) {
//        try {
//            UserProgramRole userProgramRole = UserProgramRole.valueOf(key);
//
//            if (value instanceof List<?> list) {
//                List<String> stringList = new ArrayList<>();
//                for (Object o : list) {
//                    if (o instanceof String) {
//                        stringList.add((String) o);
//                    }
//                }
//                userProgramRoleToUserMap.put(userProgramRole, stringList);
//            }
//        } catch (IllegalArgumentException ignored) {
//        }
//    }
}

