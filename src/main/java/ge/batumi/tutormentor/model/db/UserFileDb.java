package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userFiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFileDb {
    @Id
    private String id;

    private String userId;
    private String fileId;
    private String key;

    public UserFileDb(String fileId, String key) {
        this.fileId = fileId;
        this.key = key;
    }
}
