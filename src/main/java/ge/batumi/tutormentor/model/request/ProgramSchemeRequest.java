package ge.batumi.tutormentor.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a program scheme.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSchemeRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
}
