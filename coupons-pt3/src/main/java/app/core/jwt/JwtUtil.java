package app.core.jwt;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import app.core.login.ServiceType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {
	
	private String signatureAlgorithm = SignatureAlgorithm.HS256.getJcaName();
	private String encodedSecretKey = "+K4Hj/6h8+uy1i/3bu5v4t+7y9/e8c5v+1Y3/U7/B2+4N6Yo9+8/5m4i";
	private Key decodedSecretKey = new SecretKeySpec(Base64.getDecoder().decode(encodedSecretKey),
			signatureAlgorithm);

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userType", userDetails.serviceType);
		claims.put("firstName", userDetails.firstName);
		claims.put("lastName", userDetails.lastName);
		claims.put("valid", true);
		return createToken(claims, userDetails.email);
	}

	private String createToken(Map<String, Object> claims, String subject) {

		Instant now = Instant.now();

		return Jwts.builder()
				.setClaims(claims)

				.setSubject(subject)

				.setIssuedAt(Date.from(now))

				.setExpiration(Date.from(now.plus(12, ChronoUnit.HOURS)))
				
				.signWith(decodedSecretKey)

				.compact();
	}

	private Claims extractAllClaims(String token) throws ExpiredJwtException {
		JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(decodedSecretKey).build();
		return jwtParser.parseClaimsJws(token).getBody();
	}

	/** returns the JWT subject - in our case the email address */
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	private boolean isTokenExpired(String token) {
		try {
			extractAllClaims(token);
			return false;
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	/**
	 * returns true if the user (email) in the specified token equals the one in the
	 * specified user details and the token is not expired
	 */
	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.email) && !isTokenExpired(token));
	}

	public static class UserDetails {
		public String email;
		public String firstName;
		public String lastName;
		public ServiceType serviceType;

		public UserDetails(String email, String firstName, String lastName, ServiceType serviceType) {
			super();
			this.email = email;
			this.firstName = firstName;
			this.lastName = lastName;
			this.serviceType = serviceType;
		}
	}
}