package com.practice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;

public class JwtProvider {
    private static final String SECRET = "my_super_secret_key";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    public static String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .sign(ALGORITHM);
    }

    public static DecodedJWT verifyToken(String token) {
        try {
            return JWT.require(ALGORITHM).build().verify(token);
        } catch (Exception e) {
            return null;
        }
    }
}
