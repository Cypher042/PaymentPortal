package cypher.ayush.payment.controller;

import cypher.ayush.payment.model.User;
import cypher.ayush.payment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        System.err.println("User registered: " + user.getUsername());
        return "User registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.get("username"), credentials.get("password")));

        // System.err.println("User logged in: " + credentials.get("username"));
        if (auth.isAuthenticated()) {
            System.err.println("Authentication successful for user: " + credentials.get("username"));
        } else {
            System.err.println("Authentication failed for user: " + credentials.get("username"));
        }
        return auth.isAuthenticated() ? "Login successful" : "Invalid credentials";
    }
}
