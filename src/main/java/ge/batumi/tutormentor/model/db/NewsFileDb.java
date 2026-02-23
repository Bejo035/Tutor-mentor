package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document linking a GridFS file to a news article by key.
 */
@Document(collection = "newsFiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsFileDb {
    @Id
    private String id;

    @Indexed
    private String newsId;
    private String fileId;
    private String key;

    public NewsFileDb(String fileId, String key) {
        this.fileId = fileId;
        this.key = key;
    }
}
