package ge.batumi.tutormentor.model.db;

import ge.batumi.tutormentor.model.request.NewsRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB document representing a news article.
 */
@Data
@Document(collection = "news")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewsDb {
    @Id
    private String id;

    private String title;
    private String description;
    private LocalDateTime addDate = LocalDateTime.now();

    public NewsDb(NewsRequest request) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.addDate = LocalDateTime.now();
    }
}
