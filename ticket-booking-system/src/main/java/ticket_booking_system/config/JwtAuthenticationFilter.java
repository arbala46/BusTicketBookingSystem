package ticket_booking_system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ticket_booking_system.entity.User;
import ticket_booking_system.exception.UserNotFoundException;
import ticket_booking_system.repository.userRepository;
import ticket_booking_system.service.JwtService;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    userRepository userRepository;

    @Override
    protected void doFilterInternal
            (HttpServletRequest request,
             HttpServletResponse response,
             FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ") )
        {
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtService.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User Not Found"));

            boolean valid = jwtService.isTokenValid(token,user.getEmail());
            if(!valid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request,response);

        }catch (Exception ex)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("Invalid JWT Token: {}",ex.getMessage());
            return;
        }

    }
}
