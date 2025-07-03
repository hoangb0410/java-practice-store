package com.store.store.common.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.store.store.common.CustomStoreDetails;
import com.store.store.common.CustomUserDetails;
import com.store.store.model.Store;
import com.store.store.model.User;
import com.store.store.modules.store.StoreRepository;
import com.store.store.modules.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            StoreRepository storeRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            DecodedJWT decodedJWT = jwtService.verifyToken(token);
            String subject = decodedJWT.getSubject();
            if (subject == null || !subject.contains(":")) {
                throw new RuntimeException("Invalid JWT subject format");
            }
            String[] parts = subject.split(":");
            String type = parts[0];
            Long id = Long.parseLong(parts[1]);

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            if ("user".equals(type)) {
                User user = userRepository.findById(id).orElseThrow();
                UserDetails userDetails = new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if ("store".equals(type)) {
                Store store = storeRepository.findById(id).orElseThrow();
                UserDetails storeDetails = new CustomStoreDetails(store);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        storeDetails, null, storeDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            System.out.println("Invalid JWT: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
