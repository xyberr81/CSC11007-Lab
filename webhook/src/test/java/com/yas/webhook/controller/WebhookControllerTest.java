package com.yas.webhook.controller;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WebhookController.class, excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    @Test
    void testGetPageableWebhooks() throws Exception {
        when(webhookService.getPageableWebhooks(anyInt(), anyInt()))
                .thenReturn(WebhookListGetVm.builder().build());

        mockMvc.perform(get("/backoffice/webhooks/paging")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testListWebhooks() throws Exception {
        when(webhookService.findAllWebhooks()).thenReturn(List.of());

        mockMvc.perform(get("/backoffice/webhooks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetWebhook() throws Exception {
        when(webhookService.findById(1L)).thenReturn(new WebhookDetailVm());

        mockMvc.perform(get("/backoffice/webhooks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateWebhook() throws Exception {
        when(webhookService.create(any(WebhookPostVm.class)))
                .thenReturn(new WebhookDetailVm());

        mockMvc.perform(post("/backoffice/webhooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payloadUrl\":\"http://example.com\", \"contentType\":\"application/json\", \"events\":[]}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateWebhook() throws Exception {
        mockMvc.perform(put("/backoffice/webhooks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payloadUrl\":\"http://example.com\", \"contentType\":\"application/json\", \"events\":[]}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteWebhook() throws Exception {
        mockMvc.perform(delete("/backoffice/webhooks/1"))
                .andExpect(status().isNoContent());
    }
}
