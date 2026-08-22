package com.kfd.api.kfd_backend.auth;

import com.kfd.api.kfd_backend.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    /**
     * The value this property used to default to. It came from a widely-published
     * tutorial and appears in thousands of public repositories, so any token signed
     * with it can be forged by anyone. Rejected outright.
     */
    private static final String KNOWN_LEAKED_KEY =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private static final int MIN_KEY_BITS = 256;

    /**
     * Deliberately has no default. A signing key is a secret: if it is missing the
     * application must say so, not silently fall back to a value an attacker could
     * read in the source. Set JWT_SECRET_KEY in the environment.
     */
    @Value("${application.security.jwt.secret-key:}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:86400000}")
    private long jwtExpiration; // 24 hours by default

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /** Resolved once at startup rather than decoded on every token operation. */
    private SecretKey signInKey;

    @PostConstruct
    void initSignInKey() {
        if (KNOWN_LEAKED_KEY.equalsIgnoreCase(secretKey)) {
            throw new IllegalStateException("""
                    The configured JWT signing key is a publicly known value that ships in \
                    countless tutorials and public repositories. Anyone can forge admin tokens \
                    with it. Generate a new key with `openssl rand -base64 32` and set it as \
                    JWT_SECRET_KEY.""");
        }

        if (secretKey == null || secretKey.isBlank()) {
            if (isLocalProfile()) {
                // Safe fallback for development: random per boot, never a known value.
                // Tokens do not survive a restart, which is fine locally.
                this.signInKey = Jwts.SIG.HS256.key().build();
                log.warn("No JWT signing key configured — generated an ephemeral one for the "
                        + "'{}' profile. Tokens will be invalidated on restart. Set JWT_SECRET_KEY "
                        + "to make sessions persist.", activeProfile);
                return;
            }
            throw new IllegalStateException("""
                    application.security.jwt.secret-key is not set. Generate one with \
                    `openssl rand -base64 32` and set it as the JWT_SECRET_KEY environment \
                    variable. Refusing to start without a signing key.""");
        }

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "The JWT signing key is not valid Base64. Generate one with "
                            + "`openssl rand -base64 32`.", e);
        }

        if (keyBytes.length * 8 < MIN_KEY_BITS) {
            throw new IllegalStateException(String.format(
                    "The JWT signing key is %d bits; HS256 requires at least %d. "
                            + "Generate one with `openssl rand -base64 32`.",
                    keyBytes.length * 8, MIN_KEY_BITS));
        }

        this.signInKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT signing key loaded ({} bits).", keyBytes.length * 8);
    }

    private boolean isLocalProfile() {
        return activeProfile == null
                || activeProfile.isBlank()
                || activeProfile.contains("local")
                || activeProfile.contains("dev")
                || activeProfile.equals("default");
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return signInKey;
    }
}
