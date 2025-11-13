package ge.batumi.tutormentor.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Value("${jwt.access-token.expiration-minutes:15}")
    private long accessTokenExpirationMinutes;

    @Value("${jwt.refresh-token.expiration-days:7}")
    private long refreshTokenExpirationDays;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

    public String generateAccessToken(UserDb userDb) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenExpirationMinutes * 60);

        return JWT.create()
                .withSubject(userDb.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("scope", String.join(" ", userDb.getRoles().stream().map(UserRole::name).toList()))
                .sign(algorithm());
    }

    public String generateRefreshToken(UserDb userDb) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60);

        return JWT.create()
                .withSubject(userDb.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("type", "refresh")
                .sign(algorithm());
    }
}
