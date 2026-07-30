package com.payflow.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Generate Access Token
     */
    public String generateAccessToken(String username,
                                      Map<String, Object> extraClaims) {

        return buildToken(username, extraClaims, accessTokenExpiration);
    }

    /**
     * Generate Refresh Token
     */
    public String generateRefreshToken(String username) {

        return buildToken(username, Map.of(), refreshTokenExpiration);
    }

    /**
     * Common Token Builder
     */
    private String buildToken(String username,
                              Map<String, Object> claims,
                              long expiration) {

        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extract Username
     */
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract Expiration
     */
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract Custom Claim
     */
    public <T> T extractClaim(String token,
                              Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Extract All Claims
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validate Token
     */
    public boolean isTokenValid(String token,
                                String username) {

        String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }

    /**
     * Check Expiration
     */
    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * Create Secret Key
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}