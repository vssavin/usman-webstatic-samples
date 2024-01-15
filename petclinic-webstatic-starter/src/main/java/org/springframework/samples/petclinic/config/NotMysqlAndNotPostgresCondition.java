package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author vssavin on 13.01.2024.
 */
class NotMysqlAndNotPostgresCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment environment = context.getEnvironment();
		return !environment.acceptsProfiles(Profiles.of("mysql", "postgres"));
	}

}
