package ge.batumi.tutormentor.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSchemeRequest {
    private String title;
    private String description;
}

