package com.yas.payment.controller;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentProviderController.class, excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentProviderService paymentProviderService;

    @Test
    void testCreatePaymentProvider() throws Exception {
        when(paymentProviderService.create(any(CreatePaymentVm.class)))
                .thenReturn(new PaymentProviderVm("id", "name", "url", 1, 1L, "iconUrl"));

        mockMvc.perform(post("/backoffice/payment-providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"id\", \"name\":\"name\", \"isEnable\":true, \"configureUrl\":\"http://example.com\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdatePaymentProvider() throws Exception {
        when(paymentProviderService.update(any(UpdatePaymentVm.class)))
                .thenReturn(new PaymentProviderVm("id", "name", "url", 1, 1L, "iconUrl"));

        mockMvc.perform(put("/backoffice/payment-providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"id\", \"name\":\"name\", \"isEnable\":true, \"configureUrl\":\"http://example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllEnabledPaymentProviders() throws Exception {
        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class)))
                .thenReturn(List.of(new PaymentProviderVm("id", "name", "url", 1, 1L, "iconUrl")));

        mockMvc.perform(get("/storefront/payment-providers"))
                .andExpect(status().isOk());
    }
}
