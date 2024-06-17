package com.example.makersharks_task.User.Controllers;

import com.example.makersharks_task.Config.JwtAuthenticationFilter;
import com.example.makersharks_task.Config.JwtGenerator;
import com.example.makersharks_task.ExceptionHandler.ApiErrorResponse;
import com.example.makersharks_task.ExceptionHandler.ApplicationException;
import com.example.makersharks_task.User.Model.User;
import com.example.makersharks_task.User.Services.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    JwtGenerator jwtGenerator;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @NotNull
    private ResponseEntity<?> getResponseEntity(Map<?, ?> user) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", user);
        responseBody.put("accessToken", user.get("accessToken"));
        user.remove("accessToken");
        responseBody.put("message", "Successful");
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @PostMapping("/api/user/register")
    public ResponseEntity<?> RegisterController(@RequestBody User registerRequest) {
        try{
            Map<?, ?> user = userService.Register(registerRequest.getUsername(), registerRequest.getName(), registerRequest.getPassword());
            return getResponseEntity(user);
        } catch (ApplicationException e) {
            var response = new ApiErrorResponse(
                    e.getErrorCode(),
                    e.getMessage(),
                    e.getHttpStatus().value(),
                    e.getHttpStatus().name(),
                    "/api/user/register",
                    "POST",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(e.getHttpStatus()).body(response);
        }
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<?> LoginController(@RequestBody User loginRequest) {
        try{
            Map<?,?> user = userService.Login(loginRequest.getUsername().trim(), loginRequest.getPassword());
            return getResponseEntity(user);
        } catch (ApplicationException e){
            var response = new ApiErrorResponse(
                    e.getErrorCode(),
                    e.getMessage(),
                    e.getHttpStatus().value(),
                    e.getHttpStatus().name(),
                    "/api/user/login",
                    "POST",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(e.getHttpStatus()).body(response);
        }
    }

    @GetMapping("/api/user/fetch")
    public ResponseEntity<?> fetchUserDetails(HttpServletRequest request, @RequestParam String username) {
        try{
            String token = jwtAuthenticationFilter.getJWTfromRequest(request);
            String reqUserEmail = jwtGenerator.getUsernameFromJwt(token);
            Map<?,?> user = userService.GetUserDetails(username, reqUserEmail);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (ApplicationException e) {
            var response = new ApiErrorResponse(
                    e.getErrorCode(),
                    e.getMessage(),
                    e.getHttpStatus().value(),
                    e.getHttpStatus().name(),
                    "/api/user/fetch",
                    "GET",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(e.getHttpStatus()).body(response);
        }
    }

}
