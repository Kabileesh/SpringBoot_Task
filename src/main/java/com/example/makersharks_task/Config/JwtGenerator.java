package com.example.makersharks_task.Config;

import com.example.makersharks_task.ExceptionHandler.ApplicationException;
import com.example.makersharks_task.User.Model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;


@Component
public class JwtGenerator {
    private final long JWT_EXPIRATION_TIME;
    private final Key secretKey;

    @Autowired
    public JwtGenerator(@Value("${jwt.token.secret}") String jwtSecret,
                        @Value("${jwt.token.expiration-time}") long jwtExpirationTime) {
        this.JWT_EXPIRATION_TIME = jwtExpirationTime;
        this.secretKey = getSigningKey(jwtSecret);
    }
    public String generateToken(User user) {
        String _id = user.get_id();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + JWT_EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(_id)
                .claim("username", user.getUsername())
                .claim("name", user.getName())
                .setIssuedAt(currentDate)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    private Claims extractToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String getUserIdFromJWT(String token) {
        Claims claims = extractToken(token);
        return claims.getSubject();
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = extractToken(token);
        return claims.get("username", String.class);
    }

    public boolean validateToken(String token) throws ApplicationException {
        try{
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            throw new ApplicationException("Invalid JWT signature", "Invalid Token", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException ex) {
            throw new ApplicationException("Invalid JWT token", "Invalid Token", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException ex) {
            throw new ApplicationException("Expired JWT token", "Expired Token", HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException ex) {
            throw new ApplicationException("Unsupported JWT token", "Invalid Token", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException ex) {
            throw new ApplicationException("JWT claims string is empty.", "Invalid Token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Key getSigningKey(String Secret) {
        byte[] secretBytes = Secret.getBytes();
        byte[] paddedKeyBytes = new byte[32];
        if(secretBytes.length >= paddedKeyBytes.length){
            System.arraycopy(secretBytes, 0, paddedKeyBytes, 0, paddedKeyBytes.length);
        } else {
            System.arraycopy(secretBytes, 0, paddedKeyBytes, 0, secretBytes.length);
        }
        return new SecretKeySpec(paddedKeyBytes, SignatureAlgorithm.HS512.getJcaName());
    }
}
