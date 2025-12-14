package ge.batumi.tutormentor.model.request;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSchemeRequest {
    private String title;
    private String description;
    private Integer maxSize;
    private ProgramSchemeDb.RegistrationDates registrationDates;
}

