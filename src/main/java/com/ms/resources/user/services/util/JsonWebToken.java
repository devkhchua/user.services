package com.ms.resources.user.services.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
public class JsonWebToken {

    LocalDateTime currentDateTime = LocalDateTime.now();
    Date accessTokenExpiry = Date.from(currentDateTime.plus(Duration.of(10, ChronoUnit.MINUTES)).atZone(ZoneId.systemDefault()).toInstant());
    Date refreshTokenExpiry = Date.from(currentDateTime.plus(Duration.of(7, ChronoUnit.DAYS)).atZone(ZoneId.systemDefault()).toInstant());
    //update to retrieve secret from properties file/database/servers
    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

    public String generateAccessToken(String username, HttpServletRequest request, List<String> roles) {
        String accessToken = JWT.create()
                .withSubject(username)
                .withExpiresAt(accessTokenExpiry)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .sign(algorithm);

        return accessToken;
    }

    public String generateRefreshToken(String username) {
        String refreshToken = JWT.create()
                .withSubject(username)
                .withExpiresAt(refreshTokenExpiry)
                .sign(algorithm);
        return refreshToken;
    }

    public DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        }catch (JWTVerificationException exception){
            log.error("[JsonWebToken] - error verifying token" + exception.getMessage());
        }
        return null;
    }
}
