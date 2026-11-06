package br.com.finatech.infrastructure.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.finatech.domain.user.entity.User;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /** Expiração em horas, configurável via JWT_EXPIRY_HOURS. Padrão: 2h. */
    @Value("${api.security.token.expiry-hours:2}")
    private long expiryHours;

    public String generateToken(User entity) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            String token = JWT.create()
                    .withIssuer("finaobra-api")
                    .withSubject(entity.getUserName())
                    .withClaim("id", entity.getId().toString())
                    .withExpiresAt(this.generateExpireAt())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar o token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer("finaobra-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant generateExpireAt() {
        return Instant.now().plus(expiryHours, ChronoUnit.HOURS);
    }

    public UUID getClaimId(String token) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return UUID.fromString(JWT.require(algorithm)
                .withIssuer("finaobra-api") // mude para o seu issuer real se houver
                .build()
                .verify(token)
                .getClaim("id").asString());
    } catch (Exception exception) {
        return null;
    }
}
}
