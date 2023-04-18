package com.catdog.help.service;

import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import com.catdog.help.repository.jpa.JpaUploadFileRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.bulletinboard.*;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BulletinBoardService {

    private final JpaBulletinBoardRepository bulletinBoardRepository;
    private final JpaUserRepository userRepository;
    private final JpaUploadFileRepository uploadFileRepository;
    private final ImageService imageService;
    private final JpaLikeBoardRepository jpaLikeBoardRepository;


    @Transactional
    public Long createBoard(SaveBulletinBoardForm form, String nickname) {
        BulletinBoard board = getBulletinBoard(nickname, form);
        return bulletinBoardRepository.save(board);
    }

    public ReadBulletinBoardForm readBoard(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(id);
        List<ReadUploadFileForm> imageForms = getReadUploadFileForms(uploadFileRepository.findUploadFiles(id));
        int likeSize = (int) jpaLikeBoardRepository.countByBoardId(id);

        return ReadBulletinBoardForm.builder()
                .board(findBoard)
                .imageForms(imageForms)
                .likeSize(likeSize)
                .build();
    }

    public List<PageBulletinBoardForm> readPage(int page) {
        int offset = page * 10 - 10;
        int limit = 10;

        return getPageBulletinBoardForms(bulletinBoardRepository.findPage(offset, limit));
    }

    public int countPages() {
        int total = (int) bulletinBoardRepository.countAll();
        if (total <= 10) {
            return 1;
        } else if (total % 10 == 0) {
            return total / 10;
        } else {
            return total / 10 + 1;
        }
    }

    public UpdateBulletinBoardForm getUpdateForm(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(id);
        List<ReadUploadFileForm> oldImages = getReadUploadFileForms(uploadFileRepository.findUploadFiles(id));

        return new UpdateBulletinBoardForm(findBoard, oldImages);
    }

    @Transactional
    public void updateBoard(UpdateBulletinBoardForm form) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(form.getId());
        updateBulletinBoard(findBoard, form);
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(boardId);
        bulletinBoardRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private BulletinBoard getBulletinBoard(String nickname, SaveBulletinBoardForm form) {
        User findUser = userRepository.findByNickname(nickname);
        BulletinBoard board = BulletinBoard.builder()
                .user(findUser)
                .form(form)
                .build();

        imageService.addImage(board, form.getImages());
        return board;
    }

    private static List<PageBulletinBoardForm> getPageBulletinBoardForms(List<BulletinBoard> boards) {
        return boards.stream()
                .map(PageBulletinBoardForm::new)
                .collect(Collectors.toList());
    }

    private void updateBulletinBoard(BulletinBoard findBoard, UpdateBulletinBoardForm form) {
        findBoard.updateBoard(form);
        imageService.updateImage(findBoard, form.getDeleteImageIds(), form.getNewImages());
    }

    private List<ReadUploadFileForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadUploadFileForm::new)
                .collect(Collectors.toList());
    }
}
