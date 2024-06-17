package com.example.makersharks_task.Config;

import com.example.makersharks_task.ExceptionHandler.ApiErrorResponse;
import com.example.makersharks_task.ExceptionHandler.ApplicationException;
import com.example.makersharks_task.User.Model.User;
import com.example.makersharks_task.User.Services.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private UserServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, ApplicationException {
        if(request.getRequestURI().equals("/api/user/login") || request.getRequestURI().equals("/api/user/register")){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String token = getJWTfromRequest(request);
            if (token == null || !jwtGenerator.validateToken(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized: Invalid or missing token");
                return;
            }
            String _id = jwtGenerator.getUserIdFromJWT(token);
            User userDetails = userService.getUserById(_id);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                    null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (ApplicationException ex) {
            handleException(response, ex);
        } catch (Exception ex) {
            handleException(response, new ApplicationException("Unauthorized", ex.getMessage(), HttpStatus.UNAUTHORIZED));
        }
    }

    public String getJWTfromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken!=null &&  bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }

    private void handleException(HttpServletResponse response, ApplicationException ex) throws IOException {
        response.setStatus(ex.getHttpStatus().value());
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus().value(),
                ex.getHttpStatus().name(),
                null,
                null,
                LocalDateTime.now()
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }
}
