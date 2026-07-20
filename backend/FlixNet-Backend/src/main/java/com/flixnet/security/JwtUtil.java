package com.flixnet.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final long JWT_TOKEN_VALIDITY = 30 * 24 * 60 * 60 * 1000L;

	@Value("${jwt.secret:defaultSecretKeyForFlixNetdefaultSecretKeyForFlixNet}")
	private String secret;

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public String getRoleFromToken(String token) {
		return getClaimFromToken(token, claims -> claims.get("role", String.class));
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(String username, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		return doGenerateToken(claims, username);
	}

	private String doGenerateToken(Map<String, Object> claims, String username) {
		return Jwts.builder().claims(claims).subject(username).issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)).signWith(getSecretKey())
				.compact();
	}

	public Boolean validateToken(String token) {
		try {
			getAllClaimsFromToken(token);
			return !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

}
