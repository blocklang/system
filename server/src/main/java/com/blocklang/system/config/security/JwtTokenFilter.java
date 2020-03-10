package com.blocklang.system.config.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.blocklang.system.service.JwtService;
import com.blocklang.system.service.UserService;

public class JwtTokenFilter extends OncePerRequestFilter {

	private String header = "Authorization";

	@Autowired
	private UserService userService;
	@Autowired
	private JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		getTokenString(request.getHeader(header)).ifPresent(token -> jwtService.getSubFromToken(token).ifPresent(userId -> {
			if(SecurityContextHolder.getContext().getAuthentication() == null) {
				userService.findById(userId).ifPresent( user -> {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				});
			}
		}));

		filterChain.doFilter(request, response);
	}

	private Optional<String> getTokenString(String header) {
		if (header == null) {
			return Optional.empty();
		} else {
			String[] split = header.split(" ");
			if (split.length < 2) {
				return Optional.empty();
			} else {
				return Optional.ofNullable(split[1]);
			}
		}
	}
}
