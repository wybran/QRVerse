package dev.wybran.qrverse.controller;

import dev.wybran.qrverse.dto.response.UserResponse;
import dev.wybran.qrverse.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(Principal principal) {
        String currentPrincipalName = principal.getName();
        return userService.getUserResponse(currentPrincipalName);
    }
}
