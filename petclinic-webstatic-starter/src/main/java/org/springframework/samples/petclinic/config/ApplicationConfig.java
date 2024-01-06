package org.springframework.samples.petclinic.config;

import com.github.vssavin.usman_webstatic_core.UsmanWebstaticConfigurer;
import com.github.vssavin.usman_webstatic_starter.spring6.EntityScanPackages;
import com.github.vssavin.usmancore.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * @author vssavin on 05.01.2024.
 */
@Configuration
@ComponentScan({ "org.springframework.samples.petclinic.*", "com.github.vssavin.usman_webstatic_starter.*" })
@EnableJpaRepositories(basePackages = { "org.springframework.samples.petclinic.*" })
public class ApplicationConfig {

	private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

	@Bean
	public UsmanWebstaticConfigurer usmanConfigurer(UsmanUrlsConfigurer urlsConfigurer, OAuth2Config oAuth2Config,
			List<PermissionPathsContainer> permissionPathsContainerList) {

		UsmanWebstaticConfigurer usmanConfigurer = new UsmanWebstaticConfigurer(urlsConfigurer, oAuth2Config,
				permissionPathsContainerList);

		usmanConfigurer.permission(new AuthorizedUrlPermission("/index.html", Permission.ANY_USER))
			.permission(new AuthorizedUrlPermission("/index", Permission.ANY_USER));

		usmanConfigurer.defaultLanguage("en").loginPageTitle("Spring petclinic service");

		usmanConfigurer.csrf(false);

		return usmanConfigurer.configure();
	}

	@Bean
	public UsmanUrlsConfigurer usmanUrlsConfigurer() {

		UsmanUrlsConfigurer usmanUrlsConfigurer = new UsmanUrlsConfigurer();

		usmanUrlsConfigurer.successUrl("/owners").adminSuccessUrl("/usman/v1/admin");

		return usmanUrlsConfigurer.configure();
	}

	@Bean
	public EntityScanPackages petclinicEntityPackages() {
		log.debug("Initializing usman entity packages...");
		return () -> new String[] { "org.springframework.samples.petclinic.*" };
	}

	/**
	 * An example bean to print an encoded password in a specified file. Used in the
	 * ${@link UsmanPasswordEncodingArgumentsHandler} class.
	 * @return PrintStream bean.
	 */
	@Bean
	public PrintStream passwordPrintStream() {
		try {
			return new PrintStream(new FileOutputStream("password-output.txt", true));
		}
		catch (FileNotFoundException e) {
			log.error("Initialize passwordPrintStream bean error!", e);
		}

		return null;
	}

}
