package com.practice;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.auth0.jwt.interfaces.DecodedJWT;

public class AuthHandler extends Authenticator {
    @Override
    public Result authenticate(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new Failure(401);
        }
        String token = authHeader.substring(7);
        DecodedJWT jwt = JwtProvider.verifyToken(token);
        if (jwt == null) {
            return new Failure(401);
        }
        return new Success(new HttpPrincipal(jwt.getSubject(), "warehouse-realm"));
    }
}
