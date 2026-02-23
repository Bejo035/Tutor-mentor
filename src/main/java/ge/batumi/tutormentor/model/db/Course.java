package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

 /**
 * MongoDB document representing a course within a program scheme.
 */
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
    @Indexed
    private String creatorUserId;

    @Indexed
    private String programId;
}
