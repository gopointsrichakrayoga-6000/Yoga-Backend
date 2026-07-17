package com.ashram.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:SriChakraYogaSacredSecretKeyForJwtAuthentication2026!}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    @Value("${app.seed-demo-data:false}")
    private boolean seedDemoData;

    @jakarta.annotation.PostConstruct
    public void validateSecret() {
        if ("SriChakraYogaSacredSecretKeyForJwtAuthentication2026!".equals(secret)) {
            logger.warn("==================================================================================");
            logger.warn("SECURITY WARNING: Using default development JWT_SECRET key!");
            logger.warn("In a production deployment, you MUST provide a unique, strong random JWT_SECRET environment variable.");
            if (!seedDemoData) {
                logger.warn("CRITICAL: SEED_DEMO_DATA is false (production mode), but default JWT_SECRET is still active!");
                logger.warn("Please set JWT_SECRET immediately to secure all authentication tokens.");
            }
            logger.warn("==================================================================================");
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
