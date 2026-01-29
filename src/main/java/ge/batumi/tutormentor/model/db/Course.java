package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "course")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Course {
    @Id
    private String id;
    private String name;
    private Integer maxSize;
    private ProgramSchemeDb.RegistrationDates registrationDates;
    private String creatorUserId;

    private String programId;
}
