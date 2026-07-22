package ru.ivanov.financetracker.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    public String createToken(Long userId, String username){
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + validityInMilliseconds);

        return JWT.create()
                .withSubject(username)
                .withClaim("userId", userId)
                .withIssuedAt(now)
                .withExpiresAt(expiredAt)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public boolean validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public String getUsername(String token) {
        return decode(token).getSubject();
    }

    public Long getUserId(String token) {
        return decode(token).getClaim("userId").asLong();
    }

    private DecodedJWT decode(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
    }

}
