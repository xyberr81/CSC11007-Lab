package com.yas.sampledata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.yas.sampledata.utils.SqlScriptExecutor;
import com.yas.sampledata.viewmodel.SampleDataVm;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SampleDataServiceTest {

    @Mock(name = "productDataSource")
    private DataSource productDataSource;

    @Mock(name = "mediaDataSource")
    private DataSource mediaDataSource;

    @InjectMocks
    private SampleDataService sampleDataService;

    @Test
    void testServiceInstantiation() {
        assertNotNull(sampleDataService);
    }

    @Test
    void createSampleData_ShouldReturnSuccessVm_AndCallExecutorForBothDataSources() {
        try (MockedConstruction<SqlScriptExecutor> mocked = mockConstruction(
            SqlScriptExecutor.class,
            (mock, context) -> doNothing().when(mock)
                .executeScriptsForSchema(any(DataSource.class), anyString(), anyString())
        )) {
            SampleDataVm result = sampleDataService.createSampleData();

            assertNotNull(result);
            assertEquals("Insert Sample Data successfully!", result.message());

            // verify one SqlScriptExecutor was constructed and called twice (product + media)
            assertEquals(1, mocked.constructed().size());
            SqlScriptExecutor executor = mocked.constructed().getFirst();
            verify(executor, times(2))
                .executeScriptsForSchema(any(DataSource.class), anyString(), anyString());
        }

    }
}
