package com.srs.SpringChat.filters;

import com.srs.SpringChat.services.JwtService;
import com.srs.SpringChat.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader in filter: " + authHeader);
        String token = null;
        String email = null;

        System.out.println("in jwt filter ---------------------- ");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            System.out.println("inside first if jwt filter");
            token = authHeader.substring(7);
            email = jwtService.extractUserName(token);
            System.out.println("email from jwt filter: " + email); // check
            System.out.println("token from jwt filter: " + token); // check
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("valid token, authenticating user ");
            UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(email);
            System.out.println("User Authorities: " + userDetails.getAuthorities());
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("SecurityContextHolder.getContext().getAuthentication(): " + SecurityContextHolder.getContext().getAuthentication());
            }
            System.out.println("user authenticated");
        }
        System.out.println("passed jwt filter");
        filterChain.doFilter(request, response);
    }
}
