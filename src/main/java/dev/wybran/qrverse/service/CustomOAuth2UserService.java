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

@Service
@Slf4j
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        return processOAuth2User(oauthUser);
    }

    private OAuth2User processOAuth2User(OAuth2User oauthUser) {
        String email = oauthUser.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email attribute is missing in OAuth2 response.");
        }

        userRepository.findByEmail(email).orElseGet(() -> createNewUser(oauthUser, email));

        return new DefaultOAuth2User(
                oauthUser.getAuthorities(),
                oauthUser.getAttributes(),
                "email"
        );
    }

    private User createNewUser(OAuth2User oauthUser, String email) {
        log.debug("User not found with email: {}. Creating a new user.", email);

        User user = new User();
        user.setEmail(email);
        user.setUserName(getAttribute(oauthUser, "name"));
        user.setGivenName(getAttribute(oauthUser, "given_name"));
        user.setFamilyName(getAttribute(oauthUser, "family_name"));
        user.setPicture(oauthUser.getAttribute("picture"));

        return userRepository.save(user);
    }

    private String getAttribute(OAuth2User oauthUser, String attributeName) {
        String value = oauthUser.getAttribute(attributeName);

        if (value == null) {
            throw new OAuth2AuthenticationException("Missing required attribute: " + attributeName);
        }

        return value;
    }
}
