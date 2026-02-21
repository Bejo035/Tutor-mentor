package ge.batumi.tutormentor.model.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime addDate;
    @JsonIgnore
    private MultiValueMap<String, String> keyToFileIdsMap = new LinkedMultiValueMap<>();

    @JsonAnyGetter
    public MultiValueMap<String, String> getFlattenedMap() {
        return keyToFileIdsMap;
    }
}
