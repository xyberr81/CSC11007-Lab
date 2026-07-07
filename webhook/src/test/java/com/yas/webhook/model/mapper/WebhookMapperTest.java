package com.yas.webhook.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class WebhookMapperTest {

    private WebhookMapper webhookMapper;

    @BeforeEach
    void setUp() {
        webhookMapper = Mappers.getMapper(WebhookMapper.class);
    }

    @Test
    void toWebhookVm_ShouldMapCorrectly() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("http://test.com");
        webhook.setContentType("application/json");
        webhook.setIsActive(true);

        WebhookVm vm = webhookMapper.toWebhookVm(webhook);

        assertNotNull(vm);
        assertEquals(1L, vm.getId());
        assertEquals("http://test.com", vm.getPayloadUrl());
        assertEquals("application/json", vm.getContentType());
        assertTrue(vm.getIsActive());
    }

    @Test
    void toWebhookEventVms_WhenListIsNotEmpty_ShouldMapToEventVms() {
        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setId(10L);
        webhookEvent.setEventId(5L);

        List<EventVm> result = webhookMapper.toWebhookEventVms(List.of(webhookEvent));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
    }

    @Test
    void toWebhookEventVms_WhenListIsEmpty_ShouldReturnEmptyList() {
        List<EventVm> result = webhookMapper.toWebhookEventVms(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toWebhookEventVms_WhenListIsNull_ShouldReturnEmptyList() {
        List<EventVm> result = webhookMapper.toWebhookEventVms(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toWebhookListGetVm_ShouldMapCorrectly() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        Page<Webhook> page = new PageImpl<>(List.of(webhook), PageRequest.of(0, 10), 1);

        WebhookListGetVm vm = webhookMapper.toWebhookListGetVm(page, 0, 10);

        assertNotNull(vm);
        assertEquals(0, vm.getPageNo());
        assertEquals(10, vm.getPageSize());
        assertEquals(1, vm.getTotalElements());
        assertEquals(1, vm.getTotalPages());
        assertTrue(vm.isLast());
        assertEquals(1, vm.getWebhooks().size());
        assertEquals(1L, vm.getWebhooks().get(0).getId());
    }

    @Test
    void toUpdatedWebhook_ShouldUpdateFieldsExceptIgnored() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setContentType("application/xml");

        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://updated.com");
        postVm.setSecret("newSecret");
        postVm.setIsActive(false);

        Webhook updated = webhookMapper.toUpdatedWebhook(webhook, postVm);

        assertEquals(1L, updated.getId());
        assertEquals("application/xml", updated.getContentType()); // ignored field
        assertEquals("http://updated.com", updated.getPayloadUrl());
        assertEquals("newSecret", updated.getSecret());
        assertFalse(updated.getIsActive());
    }

    @Test
    void toCreatedWebhook_ShouldMapFields() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("http://created.com");
        postVm.setContentType("application/json");
        postVm.setSecret("secret123");
        postVm.setIsActive(true);

        Webhook created = webhookMapper.toCreatedWebhook(postVm);

        assertNotNull(created);
        assertNull(created.getId());
        assertEquals("http://created.com", created.getPayloadUrl());
        assertEquals("application/json", created.getContentType());
        assertEquals("secret123", created.getSecret());
        assertTrue(created.getIsActive());
    }

    @Test
    void toWebhookDetailVm_ShouldMapFieldsAndIgnoreSecret() {
        Webhook webhook = new Webhook();
        webhook.setId(2L);
        webhook.setPayloadUrl("http://detail.com");
        webhook.setContentType("application/json");
        webhook.setSecret("superSecret");
        webhook.setIsActive(true);
        
        WebhookEvent event = new WebhookEvent();
        event.setEventId(99L);
        webhook.setWebhookEvents(List.of(event));

        WebhookDetailVm detailVm = webhookMapper.toWebhookDetailVm(webhook);

        assertNotNull(detailVm);
        assertEquals(2L, detailVm.getId());
        assertEquals("http://detail.com", detailVm.getPayloadUrl());
        assertEquals("application/json", detailVm.getContentType());
        assertNull(detailVm.getSecret()); // ignored field
        assertTrue(detailVm.getIsActive());
        assertEquals(1, detailVm.getEvents().size());
        assertEquals(99L, detailVm.getEvents().get(0).getId());
    }
}
