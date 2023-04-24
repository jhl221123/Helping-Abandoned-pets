package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Spy
    @InjectMocks
    ImageService imageService;

    @Mock
    UploadFileRepository uploadFileRepository;

    @Mock
    FileStore fileStore;

    @Mock
    UserRepository userRepository;


    @Test
    @DisplayName("이미지 저장 경로에 저장 후 Board 주입")
    void addImage() throws IOException {
        //given
        List<MultipartFile> images = new ArrayList<>();
        images.add(getMultipartFile("testImage_A", "jpg"));
        images.add(getMultipartFile("testImage_B", "jpg"));

        List<UploadFile> storedImages = new ArrayList<>();
        storedImages.add(getUploadFile("testImage_A.jpg", "storeName_A"));
        storedImages.add(getUploadFile("testImage_B.jpg", "storeName_B"));

        UploadFile emptyFile = UploadFile.builder().build();

        doReturn(storedImages).when(fileStore)
                .storeFiles(images);

        doReturn(emptyFile).when(uploadFileRepository)
                .save(any(UploadFile.class));

        Bulletin board = getBulletin();

        //expected
        imageService.addImage(board, images);
        verify(fileStore, times(1)).storeFiles(images);
        verify(uploadFileRepository, times(2)).save(any(UploadFile.class));
    }

    @Test
    @DisplayName("id로 해당 이미지 삭제 후 새 이미지 저장")
    void update() {
        //given
        List<Long> deleteIds = getDeleteIds();
        UploadFile emptyFile = UploadFile.builder().build();
        Bulletin board = getBulletin();
        List<MultipartFile> emptyMpf = new ArrayList<>();

        doReturn(Optional.ofNullable(emptyFile)).when(uploadFileRepository)
                .findById(any(Long.class));

        doNothing().when(uploadFileRepository)
                .delete(any(UploadFile.class));

        doNothing().when(imageService)
                .addImage(board, emptyMpf);

        //expected
        imageService.updateImage(board, deleteIds, emptyMpf);
        verify(uploadFileRepository, times(2)).findById(any(Long.class));
        verify(uploadFileRepository, times(2)).delete(any(UploadFile.class));
    }

    @Test
    @DisplayName("대표이미지 교체")
    void updateLeadImage() throws IOException {
        //given
        MockMultipartFile newLeadImage = getMultipartFile("testImage_B", "jpg");
        UploadFile afterImage = getUploadFile("testImage_B", "store_B");

        List<UploadFile> beforeImages = new ArrayList<>();
        beforeImages.add(getUploadFile("testImage_A", "store_A"));

        doReturn(afterImage).when(fileStore)
                .storeFile(newLeadImage);

        doReturn(beforeImages).when(uploadFileRepository)
                .findByBoardId(1L);

        //when
        imageService.updateLeadImage(newLeadImage, 1L);

        //then
        Assertions.assertThat(beforeImages.get(0).getUploadFileName()).isEqualTo(afterImage.getUploadFileName());

        //verify
        verify(fileStore, times(1)).storeFile(newLeadImage);
        verify(uploadFileRepository, times(1)).findByBoardId(1L);
    }


    private List<Long> getDeleteIds() {
        List<Long> deleteIds = new ArrayList<>();
        deleteIds.add(1L);
        deleteIds.add(2L);
        return deleteIds;
    }

    private UploadFile getUploadFile(String uploadFileName, String storeFileName) {
        return UploadFile.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(storeFileName)
                .build();
    }

    private MockMultipartFile getMultipartFile(String fileName, String ext) throws IOException {
        String filePath = getFullPath(fileName, ext);
        FileInputStream fileInputStream = new FileInputStream(filePath);

        return new MockMultipartFile("images", fileName + "." + ext, ext, fileInputStream);
    }

    private String getFullPath(String fileName, String ext) {
        return "src/test/resources/testImage/" + fileName + "." + ext;
    }

    private Bulletin getBulletin() {
        User user = getUser();
        userRepository.save(user);

        return Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}