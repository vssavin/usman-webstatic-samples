package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Configuration of user management template resolvers.
 *
 * @author vssavin on 05.01.2024.
 */
@Configuration
public class PetclinicTemplateResolverConfig implements WebMvcConfigurer {

	@Bean
	public SpringResourceTemplateResolver petclinicTemplateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setOrder(2);
		templateResolver.setCheckExistence(true);
		return templateResolver;
	}

	@Bean
	public ThymeleafViewResolver petclinicViewResolver(SpringTemplateEngine templateEngine) {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine);
		viewResolver.setOrder(2);
		viewResolver.setCharacterEncoding("UTF-8");
		templateEngine.addTemplateResolver(petclinicTemplateResolver());
		return viewResolver;
	}

}
