package com.yas.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    WebhookRepository webhookRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookEventRepository webhookEventRepository;
    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookMapper webhookMapper;
    @Mock
    WebhookApi webHookApi;

    @InjectMocks
    WebhookService webhookService;

    // ── notifyToWebhook ──────────────────────────────────────────────────────

    @Test
    void test_notifyToWebhook_ShouldNotException() {
        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto
            .builder()
            .notificationId(1L)
            .url("")
            .secret("")
            .build();

        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(notificationDto.getNotificationId()))
            .thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(notificationDto);

        verify(webhookEventNotificationRepository).save(notification);
        verify(webHookApi).notify(notificationDto.getUrl(), notificationDto.getSecret(), notificationDto.getPayload());
    }

    @Test
    void test_notifyToWebhook_WhenNotificationNotFound_ShouldThrow() {
        WebhookEventNotificationDto dto = WebhookEventNotificationDto.builder()
            .notificationId(99L).url("").secret("").build();

        when(webhookEventNotificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> webhookService.notifyToWebhook(dto));
    }

    // ── getPageableWebhooks ──────────────────────────────────────────────────

    @Test
    void test_getPageableWebhooks_ShouldReturnMappedResult() {
        Webhook webhook = new Webhook();
        Page<Webhook> page = new PageImpl<>(List.of(webhook), PageRequest.of(0, 10), 1);
        WebhookListGetVm expected = WebhookListGetVm.builder().pageNo(0).pageSize(10).build();

        when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(webhookMapper.toWebhookListGetVm(page, 0, 10)).thenReturn(expected);

        WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);

        assertNotNull(result);
        assertEquals(0, result.getPageNo());
    }

    // ── findAllWebhooks ──────────────────────────────────────────────────────

    @Test
    void test_findAllWebhooks_ShouldReturnMappedList() {
        Webhook webhook = new Webhook();
        WebhookVm vm = new WebhookVm();

        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id")))
            .thenReturn(List.of(webhook));
        when(webhookMapper.toWebhookVm(webhook)).thenReturn(vm);

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertEquals(1, result.size());
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void test_findById_WhenFound_ShouldReturnDetailVm() {
        Webhook webhook = new Webhook();
        WebhookDetailVm detailVm = new WebhookDetailVm();

        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(detailVm);

        WebhookDetailVm result = webhookService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void test_findById_WhenNotFound_ShouldThrowNotFoundException() {
        when(webhookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.findById(99L));
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void test_create_WithNoEvents_ShouldSaveWebhookAndReturnDetailVm() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(Collections.emptyList());

        Webhook created = new Webhook();
        created.setId(1L);
        WebhookDetailVm detailVm = new WebhookDetailVm();

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(created);
        when(webhookRepository.save(created)).thenReturn(created);
        when(webhookMapper.toWebhookDetailVm(created)).thenReturn(detailVm);

        WebhookDetailVm result = webhookService.create(postVm);

        assertNotNull(result);
        verify(webhookRepository).save(created);
    }

    @Test
    void test_create_WithEvents_ShouldSaveWebhookEventsAndReturnDetailVm() {
        EventVm eventVm = EventVm.builder().id(10L).build();
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(List.of(eventVm));

        Webhook created = new Webhook();
        created.setId(1L);
        Event event = new Event();
        WebhookDetailVm detailVm = new WebhookDetailVm();
        WebhookEvent savedEvent = new WebhookEvent();

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(created);
        when(webhookRepository.save(created)).thenReturn(created);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(webhookEventRepository.saveAll(any())).thenReturn(List.of(savedEvent));
        when(webhookMapper.toWebhookDetailVm(created)).thenReturn(detailVm);

        WebhookDetailVm result = webhookService.create(postVm);

        assertNotNull(result);
        verify(webhookEventRepository).saveAll(any());
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    void test_update_WhenFound_WithNoEvents_ShouldUpdateAndSave() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(Collections.emptyList());

        Webhook existing = new Webhook();
        existing.setWebhookEvents(Collections.emptyList());
        Webhook updated = new Webhook();

        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(webhookMapper.toUpdatedWebhook(existing, postVm)).thenReturn(updated);

        webhookService.update(postVm, 1L);

        verify(webhookRepository).save(updated);
        verify(webhookEventRepository).deleteAll(any());
    }

    @Test
    void test_update_WhenNotFound_ShouldThrowNotFoundException() {
        WebhookPostVm postVm = new WebhookPostVm();
        when(webhookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.update(postVm, 99L));
    }

    @Test
    void test_update_WhenFound_WithEvents_ShouldSaveEvents() {
        EventVm eventVm = EventVm.builder().id(5L).build();
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(List.of(eventVm));

        Webhook existing = new Webhook();
        existing.setId(1L);
        existing.setWebhookEvents(Collections.emptyList());
        Webhook updated = new Webhook();

        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(webhookMapper.toUpdatedWebhook(existing, postVm)).thenReturn(updated);
        when(eventRepository.findById(5L)).thenReturn(Optional.of(new Event()));

        webhookService.update(postVm, 1L);

        verify(webhookEventRepository).saveAll(any());
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    void test_delete_WhenExists_ShouldDeleteEventsAndWebhook() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);

        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }

    @Test
    void test_delete_WhenNotExists_ShouldThrowNotFoundException() {
        when(webhookRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> webhookService.delete(99L));
    }
}

