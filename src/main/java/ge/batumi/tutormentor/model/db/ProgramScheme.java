package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "programScheme")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProgramScheme {
    @Id
    private String id;
    private String title;
    private String description;
    private Map<UserProgramRole, List<String>> userProgramRoleToUserMap = new HashMap<>();

    public ProgramScheme(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
