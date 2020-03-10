package com.blocklang.system.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.blocklang.system.model.UserInfo;
import com.blocklang.system.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImpl implements JwtService {

	private String secret;
	private int sessionTime;

	@Autowired
	public JwtServiceImpl(@Value("${jwt.secret}") String secret, @Value("${jwt.sessionTime}") int sessionTime) {
		this.secret = secret;
		this.sessionTime = sessionTime;
	}

	@Override
	public String toToken(UserInfo user) {
		return Jwts.builder().setSubject(user.getId().toString()).setExpiration(expireTimeFromNow())
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	@Override
	public Optional<Integer> getSubFromToken(String token) {
		try {
			Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
			return Optional.ofNullable(Integer.valueOf(claimsJws.getBody().getSubject()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	private Date expireTimeFromNow() {
		return new Date(System.currentTimeMillis() + sessionTime * 1000);
	}

}
