package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;
    @Mock
    private PaypalService paypalService;

    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
            .thenReturn("{\"clientId\":\"test\"}");
    }

    @Test
    void getProviderId_ShouldReturnPAYPAL() {
        assertEquals(PaymentMethod.PAYPAL.name(), paypalHandler.getProviderId());
    }

    @Test
    void initPayment_ShouldReturnInitiatedPayment() {
        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
            .status("CREATED")
            .paymentId("PAY-123")
            .redirectUrl("https://paypal.com/pay/PAY-123")
            .build();

        when(paypalService.createPayment(any())).thenReturn(paypalResponse);

        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .totalPrice(BigDecimal.TEN)
            .checkoutId("checkout-001")
            .build();

        InitiatedPayment result = paypalHandler.initPayment(request);

        assertNotNull(result);
        assertEquals("CREATED", result.getStatus());
        assertEquals("PAY-123", result.getPaymentId());
        assertEquals("https://paypal.com/pay/PAY-123", result.getRedirectUrl());
    }

    @Test
    void capturePayment_ShouldReturnCapturedPayment() {
        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
            .checkoutId("checkout-001")
            .amount(BigDecimal.valueOf(99.99))
            .paymentFee(BigDecimal.valueOf(2.50))
            .gatewayTransactionId("TXN-001")
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .paymentStatus(PaymentStatus.COMPLETED.name())
            .failureMessage(null)
            .build();

        when(paypalService.capturePayment(any())).thenReturn(paypalResponse);

        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .token("TOKEN-XYZ")
            .build();

        CapturedPayment result = paypalHandler.capturePayment(request);

        assertNotNull(result);
        assertEquals("checkout-001", result.getCheckoutId());
        assertEquals(BigDecimal.valueOf(99.99), result.getAmount());
        assertEquals(PaymentMethod.PAYPAL, result.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
    }
}
