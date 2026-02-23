package ge.batumi.tutormentor.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "blacklistedTokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedTokenDb {
    @Id
    private String id;

    private String tokenId;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    public BlacklistedTokenDb(String tokenId, Instant expiresAt) {
        this.tokenId = tokenId;
        this.expiresAt = expiresAt;
    }
}
