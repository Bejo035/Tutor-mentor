package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "programSchemeFiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSchemeFileDb {
    @Id
    private String id;

    private String programSchemeId;
    private String fileId;
    private String key;

    public ProgramSchemeFileDb(String fileId, String key) {
        this.fileId = fileId;
        this.key = key;
    }
}
