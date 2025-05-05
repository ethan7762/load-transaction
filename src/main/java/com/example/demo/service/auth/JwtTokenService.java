package com.example.demo.service.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.common.ObjectResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtTokenService {
	
	private final static Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

	@Value("${jwt.secret}")
	private String jwtSecret;
	@Value("${jwt.expiration}")
	private int jwtExpirationMs;
	
	private SecretKey key;
	
	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
	
	public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
	
	public ObjectResponse<Void> validateJwtToken(String token) {
		ObjectResponse<Void> objResponse = new ObjectResponse<>();
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            objResponse.setSuccess(true);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error("Invalid signature: " + e.getMessage());
            objResponse.setMessage("invalid signature");
        } catch (SecurityException e) {
            logger.error("Invalid signature: " + e.getMessage());
            objResponse.setMessage("invalid signature");
        } catch (MalformedJwtException e) {
        	logger.error("Invalid JWT token: " + e.getMessage());
        	 objResponse.setMessage("invalid token");
        } catch (ExpiredJwtException e) {
        	logger.error("JWT token is expired: " + e.getMessage());
        	objResponse.setMessage("token expired");
        } catch (UnsupportedJwtException e) {
        	logger.error("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
        	logger.error("JWT claims string is empty: " + e.getMessage());
        }
        return objResponse;
    }
}
