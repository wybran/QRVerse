package dev.wybran.qrverse.service;

import dev.wybran.qrverse.dto.response.UserResponse;
import dev.wybran.qrverse.model.User;
import dev.wybran.qrverse.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getUserResponse(String email) {
        User user = loadUserByEmail(email);
        return new UserResponse(user.getId(), user.getEmail(), user.getUserName(), user.getPicture());
    }

    private User loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email)
        );
    }
}
