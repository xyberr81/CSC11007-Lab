package com.yas.sampledata.service;

import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
