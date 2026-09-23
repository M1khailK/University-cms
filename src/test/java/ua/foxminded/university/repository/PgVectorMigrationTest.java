package ua.foxminded.university.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.datasource.url="
                + "jdbc:tc:pgvector:0.8.6-pg17"
                + ":///pgvector-migration-test",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class PgVectorMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flyway_shouldInstallPgVectorExtension() {
        String installedVersion = jdbcTemplate.queryForObject(
                """
                        SELECT extversion
                        FROM pg_extension
                        WHERE extname = 'vector'
                        """,
                String.class
        );

        assertEquals("0.8.6", installedVersion);
    }
}