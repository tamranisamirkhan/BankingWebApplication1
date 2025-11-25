package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.DTOs.LoginRequestDTO;
import com.sam.BankingWebApplication1.Services.ServicesImpl.JWTService;
import com.sam.BankingWebApplication1.Services.ServicesImpl.TokenBlacklistService;
import com.sam.BankingWebApplication1.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {
        "https://smartbankofficial.netlify.app",
        "http://localhost:5500"
})
@RestController
@RequestMapping("/smartBank/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;


    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @GetMapping("/csrfToken")
    public CsrfToken getCSRFToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO request, HttpServletResponse response){

       String jwt = userService.validateUser(request);

        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        String role = jwtService.extractRole(jwt);
        ResponseCookie cookie = ResponseCookie.from("jwtToken", jwt)
                .httpOnly(true)
                .secure(false) // for localhost only
                .path("/")
                .maxAge(Duration.ofMinutes(10))
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());


        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");
        responseBody.put("role", role);

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "jwtToken", required = false) String token,HttpServletResponse response) {
        if (token != null) tokenBlacklistService.blacklist(token);
        ResponseCookie deleteCookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", deleteCookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

}
