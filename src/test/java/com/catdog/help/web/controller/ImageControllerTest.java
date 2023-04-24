package com.catdog.help.web.controller;

import com.catdog.help.FileStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;

import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @InjectMocks
    ImageController imageController;

    @Mock
    FileStore fileStore;

    private MockMvc mockMvc;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    @DisplayName("이미지 호출 성공")
    void getImage() throws Exception {
        //given
        String storeFileName = "testImage_A.jpg";
        String fullPath = "src/test/resources/testImage/" + storeFileName;

        File file = new File(fullPath);
        byte[] target = Files.readAllBytes(file.toPath());

        doReturn(fullPath).when(fileStore)
                .getFullPath(storeFileName);

        //when
        mockMvc.perform(get("/images/{fileName}", storeFileName)
                        .contentType(APPLICATION_JSON))
                .andExpect(content().bytes(target));
    }
}