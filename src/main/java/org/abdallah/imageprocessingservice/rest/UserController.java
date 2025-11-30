package org.abdallah.imageprocessingservice.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.abdallah.imageprocessingservice.base.BaseController;
import org.abdallah.imageprocessingservice.dto.LoginRequest;
import org.abdallah.imageprocessingservice.dto.RegisterRequest;
import org.abdallah.imageprocessingservice.user.User;
import org.abdallah.imageprocessingservice.user.UserService;
import org.abdallah.imageprocessingservice.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class UserController extends BaseController<User> {

    private final UserService service;
    private final JwtUtil jwtUtil;

    public UserController(UserService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Register a new user", description = "Creates a new account and returns a JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @RequestBody RegisterRequest req
    ) {
        User user = service.register(req);
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of(
                "user", user,
                "token", token
        ));
    }

    @Operation(summary = "Login a user", description = "Logs in using username/password and returns a JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @RequestBody LoginRequest req
    ) {
        User user = service.login(req);
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "user", user,
                "token", token
        ));
    }
}
