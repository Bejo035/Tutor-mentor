package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document linking a GridFS file to a user by key (e.g. "avatar", "cv").
 */
@Document(collection = "userFiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFileDb {
    @Id
    private String id;

    @Indexed
    private String userId;
    private String fileId;
    private String key;

    public UserFileDb(String fileId, String key) {
        this.fileId = fileId;
        this.key = key;
    }
}
