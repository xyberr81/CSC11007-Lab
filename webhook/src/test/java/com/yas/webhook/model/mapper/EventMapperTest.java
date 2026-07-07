package com.yas.webhook.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.yas.webhook.model.enums.EventName;

class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventMapper = Mappers.getMapper(EventMapper.class);
    }

    @Test
    void toEventVm_ShouldMapCorrectly() {
        Event event = new Event();
        event.setId(10L);
        event.setName(EventName.ON_PRODUCT_UPDATED);

        EventVm vm = eventMapper.toEventVm(event);

        assertNotNull(vm);
        assertEquals(10L, vm.getId());
        assertEquals(EventName.ON_PRODUCT_UPDATED, vm.getName());

    }

    @Test
    void toEventVm_WhenNull_ShouldReturnNull() {
        assertNull(eventMapper.toEventVm(null));
    }
}
