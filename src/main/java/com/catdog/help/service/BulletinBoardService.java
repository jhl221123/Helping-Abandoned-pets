package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import com.catdog.help.repository.jpa.JpaUploadFileRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.bulletinboard.PageBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.ReadBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
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


    /** 게시글 로직 */

    @Transactional
    public Long createBoard(SaveBulletinBoardForm boardForm, String nickName) {
        User findUser = userRepository.findByNickname(nickName);
        BulletinBoard board = getBulletinBoard(findUser, boardForm);
        Long boardId = bulletinBoardRepository.save(board); //cascade All 설정 후 리스트에 추가해서 보드만 저장해도 될듯!? 고민 필요

        return boardId;
    }

    public ReadBulletinBoardForm readBoard(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(id);

        String nickname = findBoard.getUser().getNickname();

        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(id);
        List<ReadUploadFileForm> readUploadFileForms = getReadUploadFileForms(uploadFiles);

        int likeSize = (int) jpaLikeBoardRepository.countByBoardId(id);

        ReadBulletinBoardForm readForm = new ReadBulletinBoardForm(findBoard, nickname, readUploadFileForms, likeSize);
        return readForm;
    }

    public List<PageBulletinBoardForm> readPage(int page) {
        int offset = page * 10 - 10;
        int limit = 10;

        List<BulletinBoard> boards = bulletinBoardRepository.findPage(offset, limit);

        return getPageBulletinBoardForms(boards);
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
        List<ReadUploadFileForm> readUploadFileForms = getReadUploadFileForms(uploadFileRepository.findUploadFiles(id));
        UpdateBulletinBoardForm updateForm = new UpdateBulletinBoardForm(findBoard, readUploadFileForms);
        return updateForm;
    }

    @Transactional
    public Long updateBoard(UpdateBulletinBoardForm updateForm) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(updateForm.getId());
        updateBulletinBoard(findBoard, updateForm);//게시글 내용 변경 및 이미지 추가
        return findBoard.getId();
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(boardId);
        // TODO: 2023-03-20 삭제 시 복구 가능성 염두
        bulletinBoardRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private BulletinBoard getBulletinBoard(User user, SaveBulletinBoardForm form) {
        BulletinBoard board = new BulletinBoard(user, form);
        imageService.addImage(board, form.getImages());
        return board;
    }

    private static List<PageBulletinBoardForm> getPageBulletinBoardForms(List<BulletinBoard> boards) {
        return boards.stream().map(b -> {
            PageBulletinBoardForm pageForm = new PageBulletinBoardForm(b, b.getUser().getNickname());
            return pageForm;
        }).collect(Collectors.toList());
    }

    private void updateBulletinBoard(BulletinBoard findBoard, UpdateBulletinBoardForm form) {
        findBoard.updateBoard(form);
        imageService.updateImage(findBoard, form.getDeleteImageIds(), form.getNewImages());
    }

    private List<ReadUploadFileForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(u -> {
                    ReadUploadFileForm readForm = new ReadUploadFileForm(u);
                    return readForm;
                }).collect(Collectors.toList());
    }
}
