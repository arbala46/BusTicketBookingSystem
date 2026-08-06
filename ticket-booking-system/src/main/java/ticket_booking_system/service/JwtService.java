package ticket_booking_system.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    private static final long expirationTimeMS = 60*60*1000;

    private Key getSigningKey()
    {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String email)
    {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expirationTimeMS))
                .signWith(getSigningKey())
                .compact();

    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token)
    {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token)
    {
        Claims claims = extractAllClaims(token);

        return claims.getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, String email)
    {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }


}
