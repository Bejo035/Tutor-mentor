package ge.batumi.tutormentor.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.db.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

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
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(userDb.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("scope", String.join(" ", userDb.getRoles().stream().map(UserRole::name).toList()))
                .withClaim("program_roles", userDb.getProgramRoles().stream().map(UserProgramRole::name).toList())
                .sign(algorithm());
    }

    public String generateRefreshToken(UserDb userDb) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60);

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(userDb.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("type", "refresh")
                .sign(algorithm());
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            JWT.require(algorithm()).withClaim("type", "refresh").build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsernameFromRefreshToken(String token) {
        return JWT.require(algorithm()).withClaim("type", "refresh").build().verify(token).getSubject();
    }

    public String getJtiFromToken(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getId();
    }

    public Instant getExpirationFromToken(String token) {
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getExpiresAtAsInstant();
    }
}
