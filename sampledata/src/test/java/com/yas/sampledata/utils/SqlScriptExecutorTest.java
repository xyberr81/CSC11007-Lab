package com.yas.sampledata.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class SqlScriptExecutorTest {

    private SqlScriptExecutor sqlScriptExecutor;
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        sqlScriptExecutor = new SqlScriptExecutor();
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void executeScriptsForSchema_WithNoMatchingResources_ShouldCompleteWithoutError() {
        // pattern that resolves to zero resources
        assertDoesNotThrow(() ->
            sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath*:nonexistent-dir/*.sql")
        );
    }

    @Test
    void executeScriptsForSchema_WhenGetConnectionThrowsSQLException_ShouldLogAndNotThrow()
            throws SQLException {
        // Arrange: make getConnection() throw SQLException
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused"));

        // Act & Assert: exception must be swallowed (logged only)
        assertDoesNotThrow(() ->
            sqlScriptExecutor.executeScriptsForSchema(
                dataSource, "public", "classpath*:db/product/*.sql")
        );
    }

    @Test
    void executeScriptsForSchema_WhenConnectionSucceeds_ShouldSetSchema() throws SQLException {
        // Use a real SQL file that exists on the test classpath (empty or valid)
        // We just verify the connection's setSchema was attempted
        when(dataSource.getConnection()).thenReturn(connection);

        // Using a pattern with a known file to trigger executeSqlScript path
        // db/product/product.sql is on the classpath at runtime
        assertDoesNotThrow(() ->
            sqlScriptExecutor.executeScriptsForSchema(
                dataSource, "public", "classpath*:db/product/*.sql")
        );

        verify(connection).setSchema("public");
    }
}
