package com.authservice.services.filter;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String jwtHeader;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${cookie.expiry}")
    private long cookieExpiry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	
        String jwt = request.getHeader(jwtHeader);
        
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);

            try {
                SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = claims.get("email", String.class);
                List<GrantedAuthority> authoritiesList = AuthorityUtils
                        .commaSeparatedStringToAuthorityList(claims.get("authorities", String.class));

                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authoritiesList);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Optionally create a cookie to store the JWT
                Cookie cookie = new Cookie("JwtToken", jwt);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge((int) cookieExpiry);
                response.addCookie(cookie);

            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
