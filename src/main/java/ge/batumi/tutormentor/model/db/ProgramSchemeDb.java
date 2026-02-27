package ge.batumi.tutormentor.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * MongoDB document representing a program scheme (mentoring program template).
 */
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
    @JsonIgnore
    private String creatorUserId;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public ProgramSchemeDb(ProgramSchemeRequest request) {
        this.title = request.getTitle();
        this.description = request.getDescription();
    }

    public record RegistrationDates(LocalDateTime start, LocalDateTime end) {
    }
}
