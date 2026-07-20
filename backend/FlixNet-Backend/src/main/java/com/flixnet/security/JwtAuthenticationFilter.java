package com.flixnet.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwt = extractJwtToken(request);
		String userName = null;

		if (jwt != null) {
			userName = jwtUtil.getUsernameFromToken(jwt);
		}

		if (shouldProcessAuthentication(request, userName)) {
			processAuthentication(request, jwt, userName);
		}

		filterChain.doFilter(request, response);
	}

	private String extractJwtToken(HttpServletRequest request) {
		final String authorizationHeader = request.getHeader("Authorization");
		final String requestURI = request.getRequestURI();

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		} else if ((requestURI.contains("/api/files/video") || requestURI.contains("api/files/image"))
				&& request.getParameter("token") != null) {
			return request.getParameter("token");
		}
		return null;
	}

	private boolean shouldProcessAuthentication(HttpServletRequest request, String userName) {
		return userName != null && SecurityContextHolder.getContext().getAuthentication() == null;
	}

	private void processAuthentication(HttpServletRequest request, String jwt, String userName) {
		if (jwtUtil.validateToken(jwt)) {
			UserDetails userDetails = createUserDetailsFromToken(jwt, userName);
			setAuthenticationInContext(request, userDetails);
		}
	}

	private UserDetails createUserDetailsFromToken(String jwt, String userName) {
		String role = jwtUtil.getRoleFromToken(jwt);
		return User.builder().username(userName).password("")
				.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))).build();
	}

	private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}
