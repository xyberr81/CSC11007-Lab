package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaVm;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MediaController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    @Test
    void create_WhenValidRequest_ReturnsOk() throws Exception {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("file.png");
        media.setMediaType("image/png");
        when(mediaService.saveMedia(any())).thenReturn(media);

        byte[] validPng = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
            (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54, 0x08, (byte) 0xD7, 0x63, 0x60, 0x00, 0x02,
            0x00, 0x00, 0x05, 0x00, 0x01, 0x0D, 0x26, (byte) 0xE5, 0x2E, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
        MockMultipartFile file = new MockMultipartFile("multipartFile", "file.png", "image/png", validPng);

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "caption"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void delete_WhenValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/medias/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void get_WhenNotFound_ReturnsNotFound() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(null);
        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void get_WhenFound_ReturnsOk() throws Exception {
        MediaVm vm = new MediaVm(1L, "caption", "file.png", "image/png", "url");
        when(mediaService.getMediaById(1L)).thenReturn(vm);
        mockMvc.perform(get("/medias/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getByIds_WhenEmpty_ReturnsNotFound() throws Exception {
        when(mediaService.getMediaByIds(anyList())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/medias").param("ids", "1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getByIds_WhenNotEmpty_ReturnsOk() throws Exception {
        MediaVm vm = new MediaVm(1L, "caption", "file.png", "image/png", "url");
        when(mediaService.getMediaByIds(anyList())).thenReturn(List.of(vm));
        mockMvc.perform(get("/medias").param("ids", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getFile_WhenValidRequest_ReturnsOk() throws Exception {
        MediaDto dto = MediaDto.builder()
            .content(new ByteArrayInputStream("content".getBytes()))
            .mediaType(MediaType.IMAGE_PNG)
            .build();
        when(mediaService.getFile(anyLong(), anyString())).thenReturn(dto);

        mockMvc.perform(get("/medias/1/file/file.png"))
            .andExpect(status().isOk())
            .andExpect(status().isOk());
    }
}
