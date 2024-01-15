package org.springframework.samples.petclinic.config;

import com.github.vssavin.usmancore.config.UsmanDataSourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

/**
 * @author vssavin on 05.01.2024.
 */
@Configuration
@Import(UsmanDataSourceConfig.class)
public class PetclinicDataSourcesConfig {

	private DataSource appDataSource;

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Value("${spring.datasource.username}")
	private String dbUserName;

	@Value("${spring.datasource.password}")
	private String dbPassword;

	@Bean
	@Primary
	@Conditional(NotMysqlAndNotPostgresCondition.class)
	public DataSource appDatasource() {
		if (appDataSource == null) {
			appDataSource = new EmbeddedDatabaseBuilder().generateUniqueName(true)
				.setType(H2)
				.setScriptEncoding("UTF-8")
				.ignoreFailedDrops(true)
				.build();
		}
		return appDataSource;
	}

	@Bean("appDatasource")
	@Profile("mysql")
	@Primary
	public DataSource mysqlDatasource() {
		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
		dataSourceBuilder.url(dbUrl);
		dataSourceBuilder.username(dbUserName);
		dataSourceBuilder.password(dbPassword);
		return dataSourceBuilder.build();
	}

	@Bean("appDatasource")
	@Profile("postgres")
	@Primary
	public DataSource postgresqlDatasource() {
		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName("org.postgresql.Driver");
		dataSourceBuilder.url(dbUrl);
		dataSourceBuilder.username(dbUserName);
		dataSourceBuilder.password(dbPassword);
		return dataSourceBuilder.build();
	}

}
