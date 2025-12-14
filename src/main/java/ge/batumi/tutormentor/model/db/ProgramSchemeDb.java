package ge.batumi.tutormentor.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "programScheme")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProgramSchemeDb {
    @Id
    private String id;
    private String title;
    private String description;
    private Integer maxSize;
    private RegistrationDates registrationDates;
    @JsonIgnore
    private String creatorUserId;
    private Map<UserProgramRole, List<String>> userProgramRoleToUserMap = new HashMap<>();

    public ProgramSchemeDb(ProgramSchemeRequest request) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.maxSize = request.getMaxSize();
        this.registrationDates = request.getRegistrationDates();
    }

    public record RegistrationDates(LocalDateTime start, LocalDateTime end) {
    }
}
