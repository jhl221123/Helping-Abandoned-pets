package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.*;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.search.BulletinSearch;
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
public class BulletinService {

    private final BulletinRepository bulletinRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;
    private final SearchQueryRepository searchQueryRepository;


    @Transactional
    public Long save(SaveBulletinForm form, String nickname) {
        Bulletin board = getBulletin(nickname, form);
        imageService.addImage(board, form.getImages());
        return bulletinRepository.save(board).getId();
    }

    public ReadBulletinForm read(Long id) {
        Bulletin findBoard = bulletinRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);

        List<ReadImageForm> imageForms = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
        int likeSize = Math.toIntExact(likeRepository.countByBoardId(id));

        return ReadBulletinForm.builder()
                .board(findBoard)
                .imageForms(imageForms)
                .likeSize(likeSize)
                .build();
    }

    public Long countByNickname(String nickname) {
        return bulletinRepository.countByNickname(nickname);
    }

    public Page<PageBulletinForm> getPageByNickname(String nickname, Pageable pageable) {
        return bulletinRepository.findPageByNickname(nickname, pageable)
                .map(PageBulletinForm::new);
    }

    public Long countLikeBulletin(String nickname) {
        return bulletinRepository.countLikeBulletinByNickname(nickname);
    }

    public Page<PageBulletinForm> getLikeBulletins(String nickname, Pageable pageable) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);

        return bulletinRepository.findLikeBulletins(user.getId(), pageable)
                .map(PageBulletinForm::new);
    }

    public Page<PageBulletinForm> search(BulletinSearch search, Pageable pageable) {
        return searchQueryRepository.searchBulletin(search.getTitle(), search.getRegion(), pageable)
                .map(PageBulletinForm::new);
    }

    public EditBulletinForm getEditForm(Long id) {
        Bulletin findBoard = bulletinRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> oldImages = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));

        return new EditBulletinForm(findBoard, oldImages);
    }

    @Transactional
    public void update(EditBulletinForm form) {
        Bulletin findBoard = bulletinRepository.findById(form.getId())
                .orElseThrow(BoardNotFoundException::new);
        updateBulletin(findBoard, form);
    }

    @Transactional
    public void delete(Long boardId) {
        Bulletin findBoard = bulletinRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        if (!findBoard.getImages().isEmpty()) {
            imageService.deleteImage(findBoard.getImages());
        }
        bulletinRepository.delete(findBoard);
    }


    /**============================= private method ==============================*/


    private Bulletin getBulletin(String nickname, SaveBulletinForm form) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        return Bulletin.builder()
                .user(findUser)
                .title(form.getTitle())
                .content(form.getContent())
                .region(form.getRegion())
                .build();
    }

    private void updateBulletin(Bulletin findBoard, EditBulletinForm form) {
        findBoard.updateBoard(form.getTitle(), form.getContent(), form.getRegion());
        imageService.updateImage(findBoard, form.getDeleteImageIds(), form.getNewImages());
    }

    private List<ReadImageForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadImageForm::new)
                .collect(Collectors.toList());
    }
}
