package com.catdog.help.service;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.LikeRepository;
import com.catdog.help.repository.LostRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.lost.SaveLostForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LostService {

    private final LostRepository lostRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;


    @Transactional
    public Long save(SaveLostForm form, String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        Lost board = getLost(form, user);
        imageService.addImage(board, form.getImages());

        return lostRepository.save(board).getId();
    }




    private Lost getLost(SaveLostForm form, User user) {
        return Lost.builder()
                .user(user)
                .title(form.getTitle())
                .content(form.getContent())
                .region(form.getRegion())
                .breed(form.getBreed())
                .lostDate(form.getLostDate())
                .lostPlace(form.getLostPlace())
                .gratuity(form.getGratuity())
                .build();
    }
}
