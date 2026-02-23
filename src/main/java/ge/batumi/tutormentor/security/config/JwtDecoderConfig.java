package ge.batumi.tutormentor.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {
    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Bean
    public JwtDecoder jwtDecoder(OAuth2TokenValidator<Jwt> tokenBlacklistValidator) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(
                new javax.crypto.spec.SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")
        ).build();

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                tokenBlacklistValidator
        );
        decoder.setJwtValidator(validator);

        return decoder;
    }
}
