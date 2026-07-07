package com.yas.payment.controller;

import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class, excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void testInitPayment() throws Exception {
        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(new InitPaymentResponseVm(null, null, null));

        mockMvc.perform(post("/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentMethod\":\"PAYPAL\", \"totalPrice\":10.0, \"checkoutId\":\"123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testCapturePayment() throws Exception {
        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(new CapturePaymentResponseVm(null, null, null, null, null, null, null, null));

        mockMvc.perform(post("/capture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentId\":\"PAY-123\", \"payerId\":\"P-123\", \"paymentMethod\":\"PAYPAL\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelPayment() throws Exception {
        mockMvc.perform(get("/cancel"))
                .andExpect(status().isOk());
    }
}
