package com.example.milkdelivery.controller;

import com.example.milkdelivery.dto.RegisterRequest;
import com.example.milkdelivery.dto.AuthRequest;
import com.example.milkdelivery.entity.Customer;
import com.example.milkdelivery.repository.CustomerRepository;
import com.example.milkdelivery.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // allow all origins for dev
public class AuthController {

    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthController(CustomerRepository customerRepository, JwtUtil jwtUtil, AuthenticationManager authManager) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    // -------------------------
    // Register
    // -------------------------
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        if (customerRepository.findByPhone(req.getPhone()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone already registered"));
        }

        Customer customer = new Customer();
        customer.setName(req.getName());
        customer.setPhone(req.getPhone());
        customer.setAddress(req.getAddress());
        customer.setPasswordHash(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()));

        // Generate refresh token for first login
        String refreshToken = jwtUtil.generateRefreshToken(customer.getPhone());
        customer.setRefreshToken(refreshToken);

        customerRepository.save(customer);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Registered successfully",
                        "refreshToken", refreshToken
                )
        );
    }

    // -------------------------
    // Login
    // -------------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        Customer customer = customerRepository.findByPhone(authRequest.getPhone())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String accessToken = jwtUtil.generateAccessToken(customer.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(customer.getPhone());

        // Persist refresh token
        customer.setRefreshToken(refreshToken);
        customerRepository.save(customer);

        return ResponseEntity.ok(
                Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken,
                        "customerId", customer.getId(),
                        "phone", customer.getPhone()
                )
        );
    }

    // -------------------------
    // Refresh token
    // -------------------------
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required"));
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);

            // Find customer and check stored refresh token
            Customer customer = customerRepository.findByPhone(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            if (!refreshToken.equals(customer.getRefreshToken()) || !jwtUtil.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
            }

            String newAccessToken = jwtUtil.generateAccessToken(username);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
        }
    }
}
