package org.springframework.samples.petclinic.config.kafka;

import com.github.vssavin.usmancore.config.UsmanUrlsConfigurer;
import com.github.vssavin.usmancore.spring6.auth.AuthService;
import com.github.vssavin.usmancore.spring6.security.auth.UsmanAuthenticationSuccessHandler;
import com.github.vssavin.usmancore.spring6.user.User;
import com.github.vssavin.usmancore.spring6.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author vssavin on 06.02.2024.
 */
@Component
@Primary
class KafkaProcessingAuthenticationSuccessHandler extends UsmanAuthenticationSuccessHandler {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	private final UserService userService;

	public KafkaProcessingAuthenticationSuccessHandler(UserService userService, AuthService authService,
			UsmanUrlsConfigurer usmanUrlsConfigurer, KafkaTemplate<String, Object> kafkaTemplate) {
		super(authService, usmanUrlsConfigurer);
		this.userService = userService;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		super.onAuthenticationSuccess(request, response, authentication);
		User user = null;

		try {
			OAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
			user = this.userService.processOAuthPostLogin(oAuth2User);
		}
		catch (ClassCastException ignored) {
			// ignore, it's ok
		}

		if (user == null && authentication.getPrincipal() instanceof User principal) {
			user = this.userService.getUserByLogin(principal.getLogin());
		}

		if (user == null) {
			user = this.userService.getUserByLogin(authentication.getPrincipal().toString());
		}

		if (user != null) {
			PetclinicMessage message = new PetclinicMessage(user);
			kafkaTemplate.send("user-logged", message);
		}
	}

}
