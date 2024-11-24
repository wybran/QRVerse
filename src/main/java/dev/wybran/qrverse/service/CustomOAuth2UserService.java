package dev.wybran.qrverse.service;

import dev.wybran.qrverse.model.User;
import dev.wybran.qrverse.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);

        log.debug("OAuth2User: {}", oauthUser.getAttributes());
        return processOAuth2User(oauthUser);
    }

    private OAuth2User processOAuth2User(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.debug("User not found with email: {}. Creating a new user.", email);
            user = new User();
            assert email != null;
            user.setEmail(email);
            user.setUserName(Objects.requireNonNull(oauthUser.getAttribute("name")));
            user.setGivenName(Objects.requireNonNull(oauthUser.getAttribute("given_name")));
            user.setFamilyName(Objects.requireNonNull(oauthUser.getAttribute("family_name")));
            user.setPicture(oauthUser.getAttribute("picture"));
            log.debug("Saving user: {}", user);
            userRepository.save(user);
        }

        return new DefaultOAuth2User(
                oauthUser.getAuthorities(),
                oauthUser.getAttributes(),
                "email"
        );
    }
}