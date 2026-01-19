package com.searchimage.search_image.filter;

import com.searchimage.search_image.security.UserPrincipal;
import com.searchimage.search_image.utility.JWTUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtility jwtUtil;

    public JwtFilter(JWTUtility jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        String token=extractTokenFromCookie(request);

            if (token!=null&&jwtUtil.isTokenValid(token)) {
                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_USER"));
                String email = jwtUtil.getEmail(token);
                Long userId=jwtUtil.getUserId(token);
                UserPrincipal up=new UserPrincipal(email,userId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                up,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("AUTH OBJECT = " + auth);
                System.out.println("IS AUTHENTICATED = " + auth.isAuthenticated());
                System.out.println("AUTHORITIES = " + auth.getAuthorities());
                System.out.println("PRINCIPAL CLASS = " + auth.getPrincipal().getClass());
            }


        filterChain.doFilter(request, response);
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("AUTH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
