package com.catdog.help.service;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.LostRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.lost.SaveLostForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class LostServiceTest {

    @InjectMocks
    private LostService lostService;

    @Mock
    private LostRepository lostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;


    @Test
    @DisplayName("실종글 저장 성공")
    void saveLostBoard() {
        //given
        User user = getUser();
        Lost board = getLost(user);
        SaveLostForm form = getSaveLostForm();

        doReturn(Optional.of(user)).when(userRepository)
                .findByNickname("닉네임");

        doNothing().when(imageService)
                .addImage(any(Lost.class), any(List.class));

        doReturn(board).when(lostRepository)
                .save(any(Lost.class));

        //when
        lostService.save(form, "닉네임");

        //then
        verify(userRepository, times(1)).findByNickname("닉네임");
        verify(imageService, times(1)).addImage(any(Lost.class), any(List.class));
        verify(lostRepository, times(1)).save(any(Lost.class));

    }

    private SaveLostForm getSaveLostForm() {
        return SaveLostForm.builder()
                .title("제목")
                .content("내용")
                .region("지역")
                .breed("품종")
                .lostDate(LocalDateTime.now())
                .lostPlace("실종장소")
                .gratuity(100000)
                .images(List.of())
                .build();
    }

    private Lost getLost(User user) {
        return Lost.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .breed("품종")
                .lostDate(LocalDateTime.now())
                .lostPlace("실종장소")
                .gratuity(100000)
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