package com.mrreporting.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"spring.jpa.show-sql=false",
		"jwt.secret=TestSecretKeyForJwtThatIsAtLeast32CharactersLong",
		"jwt.expiration=86400000"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
		// verifies the Spring context loads successfully with an in-memory H2 database.
		// no real PostgreSQL connection is needed for this check.
	}
}