package br.com.curso.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.curso.data.vo.v1.security.TokenVO;
import br.com.curso.exceptions.InvalidJwtAuthenticationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key:secret}")
	private String secrectKey = "secret";	
	
	@Value("${security.jwt.token.expire-lenght:secret}")
	private long validyInMilliseconds = 3600000; //1h
	
	@Autowired
	private UserDetailsService userDetailService;
	
	Algorithm algorithm = null;
	
	@PostConstruct
	protected void init() {
		secrectKey = Base64.getEncoder().encodeToString(secrectKey.getBytes());
		algorithm = Algorithm.HMAC256(secrectKey.getBytes());
	}
	
	public TokenVO createAcessToken(String username, List<String> roles) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validyInMilliseconds);
		
		var acessToken = getAcessToken(username, roles, now, validity);
		var refreshToken = getRefreshToken(username, roles, now);
		
		return new TokenVO(username, true, now, validity, acessToken, refreshToken);
	}
	
	private String getAcessToken(String username, List<String> roles, Date now, Date validity) {
		
		
		String issuerUrl = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.build()
				.toUriString();
		
		return JWT.create()
				.withClaim("roles", roles)
				.withIssuedAt(now)
				.withExpiresAt(validity)
				.withSubject(username)
				.withIssuer(issuerUrl)
				.sign(algorithm)
				.strip();
	}
	
	
	private String getRefreshToken(String username, List<String> roles, Date now) {
		
		Date RefreshTokenvalidity = new Date(now.getTime() + validyInMilliseconds * 3);
		
		return JWT.create()
				.withClaim("roles", roles)
				.withIssuedAt(now)
				.withExpiresAt(RefreshTokenvalidity)
				.withSubject(username)
				.sign(algorithm)
				.strip();
	}
	
	public Authentication getAuthentication(String token) {
		DecodedJWT decodeJWT = decodedToken(token);	
		UserDetails userDetails = this.userDetailService.loadUserByUsername(decodeJWT.getSubject());
		return new UsernamePasswordAuthenticationToken(userDetails, "" , userDetails.getAuthorities());
	}
	private DecodedJWT decodedToken(String token) {
		Algorithm alg = Algorithm.HMAC256(secrectKey.getBytes());
		JWTVerifier verifier = JWT.require(alg).build();
		DecodedJWT decodeJWT = verifier.verify(token);
		
		return decodeJWT;
	}

	public String resolveToken(HttpServletRequest req) {
	
		String bearerToken = req.getHeader("Authorization");
		
		if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring("Bearer ".length());
		}
		
		return null;
	}
	
	public boolean validadeToken(String token) {
		DecodedJWT decodedJWT = decodedToken(token);
		
		
		try {
			if(decodedJWT.getExpiresAt().before(new Date())) {
				return false;
				}
			return true;
		}catch(Exception e) {
			throw new InvalidJwtAuthenticationException("Expired or invalid token");		}
	}
	
	

}

