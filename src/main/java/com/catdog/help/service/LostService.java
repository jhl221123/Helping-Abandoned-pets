package com.catdog.help.service;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.LostRepository;
import com.catdog.help.repository.SearchQueryRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.EditLostForm;
import com.catdog.help.web.form.lost.PageLostForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import com.catdog.help.web.form.search.LostSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LostService {

    private final LostRepository lostRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final ImageService imageService;
    private final SearchQueryRepository searchQueryRepository;


    @Transactional
    public Long save(SaveLostForm form, String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        Lost board = getLost(form, user);
        imageService.addImage(board, form.getImages());

        return lostRepository.save(board).getId();
    }

    public ReadLostForm read(Long id) {
        Lost findBoard = lostRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> imageForms = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
        return new ReadLostForm(findBoard, imageForms);
    }

    public Long countByNickname(String nickname) {
        return lostRepository.countByNickname(nickname);
    }

    public Page<PageLostForm> getPageByNickname(String nickname, Pageable pageable) {
        return lostRepository.findPageByNickname(nickname, pageable)
                .map(lost -> getPageLostForm(lost));
    }

    public Page<PageLostForm> search(LostSearch search, Pageable pageable) {
        return searchQueryRepository.searchLost(search.getRegion(), pageable)
                .map(lost -> getPageLostForm(lost));
    }

    public EditLostForm getEditForm(Long id) {
        Lost findBoard = lostRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> oldImages = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));

        return new EditLostForm(findBoard, oldImages);
    }

    @Transactional
    public void update(EditLostForm form) {
        Lost findBoard = lostRepository.findById(form.getId())
                .orElseThrow(BoardNotFoundException::new);
        updateLost(findBoard, form);
    }

    @Transactional
    public void delete(Long boardId) {
        Lost findBoard = lostRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        if (!findBoard.getImages().isEmpty()) {
            imageService.deleteImage(findBoard.getImages());
        }
        lostRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private void updateLost(Lost findBoard, EditLostForm form) {
        findBoard.updateBoard(form.getTitle(), form.getContent(), form.getRegion(), form.getBreed(), form.getLostDate(), form.getLostPlace(), form.getGratuity());
        // TODO: 2023-05-22 lostedInfo 등으로 값타입 생성 고민해보자
        imageService.updateImage(findBoard, form.getDeleteImageIds(), form.getNewImages());
    }

    private PageLostForm getPageLostForm(Lost board) {
        return new PageLostForm(board, new ReadImageForm(board.getImages().get(0)));
    }

    private List<ReadImageForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadImageForm::new)
                .collect(Collectors.toList());
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
