package ge.batumi.tutormentor.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSchemeRequest {
    private String title;
    private String description;
    private List<String> mentorIds;
    private List<String> tutorIds;
    private List<String> seekerIds;
}

