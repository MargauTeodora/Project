package com.playtika.FinalProject.security.config;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.security.services.JwtTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenService jwtTokenService;


    public JwtTokenFilter(JwtTokenService jwtTokenProviderService) {
        this.jwtTokenService = jwtTokenProviderService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenService.parseToken(httpServletRequest);
        try {
            if (token != null && jwtTokenService.validateToken(token)) {
                Authentication auth = jwtTokenService.validateUser(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (UserException ex) {
            SecurityContextHolder.clearContext();
//            httpServletResponse.sendError(ex.get().value(), ex.getMessage());
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
