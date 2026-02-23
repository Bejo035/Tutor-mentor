package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document linking a GridFS file to a program scheme by key.
 */
@Document(collection = "programSchemeFiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSchemeFileDb {
    @Id
    private String id;

    @Indexed
    private String programSchemeId;
    private String fileId;
    private String key;

    public ProgramSchemeFileDb(String fileId, String key) {
        this.fileId = fileId;
        this.key = key;
    }
}
