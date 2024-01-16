/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.samples.petclinic.MySqlIntegrationTests.profileName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profileName)
@Testcontainers(disabledWithoutDocker = true)
@DisabledInNativeImage
class MySqlIntegrationTests {

	static final String profileName = "mysql";

	@ServiceConnection
	@Container
	static MySQLContainer<?> container;

	static {
		ApplicationPropertiesExtractor extractor = new ApplicationPropertiesExtractor(profileName);
		String userName = extractor.getProperty("spring.datasource.username");
		String password = extractor.getProperty("spring.datasource.password");
		String dbName = extractDbName(extractor.getProperty("spring.datasource.url"));
		container = new MySQLContainer<>("mysql:5.7").withDatabaseName(dbName)
			.withUsername(userName)
			.withPassword(password);
		container.setPortBindings(List.of(extractor.getProperty("testcontainers.portbindings")));
	}

	@LocalServerPort
	int port;

	@Autowired
	private VetRepository vets;

	@Autowired
	private RestTemplateBuilder builder;

	@Test
	void testFindAll() throws Exception {
		vets.findAll();
		vets.findAll(); // served from cache
	}

	@Test
	void testOwnerDetails() {
		RestTemplate template = builder.rootUri("http://localhost:" + port).build();
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/owners/1").build(), String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private static String extractDbName(String dbUrl) {
		String[] splitted = dbUrl.split("/");
		if (splitted.length > 1) {
			String dbName = splitted[splitted.length - 1];
			if (dbName.endsWith("?") || dbName.endsWith(":")) {
				int index = dbName.indexOf("?");
				if (index == -1) {
					index = dbName.indexOf(":");
				}
				return dbName.substring(0, index);
			}
			else {
				return dbName;
			}
		}
		return "";
	}

	private static class ApplicationPropertiesExtractor {

		private static final String APPLICATION_PROPS_FILE_NAME = "application.properties";

		private static final String TESTCONTAINERS_PROPS_FILE_NAME = "testcontainers.properties";

		private final String profile;

		private final List<Properties> propertiesList = new ArrayList<>();

		public ApplicationPropertiesExtractor(String profile) {
			this.profile = profile;
			Properties applicationProfileProperties = new Properties();
			Properties applicationProperties = new Properties();
			Properties testContainersProperties = new Properties();
			Properties testcontainersProfileProperties = new Properties();

			propertiesList.add(applicationProfileProperties);
			propertiesList.add(applicationProperties);
			propertiesList.add(testcontainersProfileProperties);
			propertiesList.add(testContainersProperties);

			URL profilePropsUrl = MySqlIntegrationTests.class.getClassLoader()
				.getResource(getResourceName(APPLICATION_PROPS_FILE_NAME));
			URL applicationPropsUrl = MySqlIntegrationTests.class.getClassLoader()
				.getResource(APPLICATION_PROPS_FILE_NAME);
			URL testcontainersPropsUrl = MySqlIntegrationTests.class.getClassLoader()
				.getResource(TESTCONTAINERS_PROPS_FILE_NAME);
			URL testcontainersProfilePropsUrl = MySqlIntegrationTests.class.getClassLoader()
				.getResource(getResourceName(TESTCONTAINERS_PROPS_FILE_NAME));

			loadFromResourceFile(applicationProfileProperties, profilePropsUrl);
			loadFromResourceFile(applicationProperties, applicationPropsUrl);
			loadFromResourceFile(testContainersProperties, testcontainersPropsUrl);
			loadFromResourceFile(testcontainersProfileProperties, testcontainersProfilePropsUrl);

		}

		public String getProperty(String key) {
			Optional<String> optionalProp = propertiesList.stream()
				.map(properties -> processAndGetProperty(properties, key))
				.filter(s -> !s.isEmpty())
				.findFirst();

			return optionalProp.isEmpty() ? "" : optionalProp.get();

		}

		private void loadFromResourceFile(Properties properties, URL url) {
			if (url != null) {
				try {
					properties.load(new FileInputStream(url.getPath()));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private String getResourceName(String basePropFileName) {
			if (profile == null || profile.isEmpty()) {
				return basePropFileName;
			}
			else {
				String[] splitted = basePropFileName.split("\\.");
				if (splitted.length > 1) {
					return splitted[0] + "-" + profile + "." + splitted[1];
				}
				else {
					return basePropFileName + "-" + profile;
				}
			}
		}

		private String processAndGetProperty(Properties properties, String key) {
			String propValue = properties.getProperty(key);
			if (propValue != null && propValue.startsWith("$")) {
				String[] splitted = propValue.split(":");
				if (splitted.length > 1) {
					StringJoiner joiner = new StringJoiner(":");
					for (int i = 1; i < splitted.length; i++) {
						joiner.add(splitted[i]);
					}
					propValue = joiner.toString().replaceAll("}", "");
				}
			}

			return propValue == null ? "" : propValue;
		}

	}

}
