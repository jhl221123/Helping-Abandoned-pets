package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.Dates;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final FileStore fileStore;
    private final JpaLikeBoardRepository jpaLikeBoardRepository;


    /** 게시글 로직 */

    @Transactional
    public Long createBoard(SaveBulletinBoardForm boardForm, String nickName) {
        User findUser = userRepository.findByNickname(nickName);
        BulletinBoard board = getBulletinBoard(boardForm, findUser);
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
        //삭제 날짜 추가
        findBoard.setDates(new Dates(findBoard.getDates().getCreateDate(),
                findBoard.getDates().getLastModifiedDate(), LocalDateTime.now()));
        bulletinBoardRepository.delete(findBoard); // TODO: 2023-03-20 복구 가능성을 위해 서비스 계층에서 아이디 보관
    }

    /**============================= private method ==============================*/

    private BulletinBoard getBulletinBoard(SaveBulletinBoardForm boardForm, User findUser) {
        BulletinBoard board = new BulletinBoard();
        board.setUser(findUser);
        board.setRegion(boardForm.getRegion());
        board.setTitle(boardForm.getTitle());
        board.setContent(boardForm.getContent());
        if (!boardForm.getImages().isEmpty()) {
            List<UploadFile> uploadFiles = fileStore.storeFiles(boardForm.getImages());
            for (UploadFile uploadFile : uploadFiles) {
                board.addImage(uploadFile); //uploadFile 에 board 주입, 안하면 uploadFile저장 불가.
                uploadFileRepository.save(uploadFile);
            }
        }
        Dates dates = new Dates(LocalDateTime.now(), null, null);
        board.setDates(dates);
        return board;
    }

    private static List<PageBulletinBoardForm> getPageBulletinBoardForms(List<BulletinBoard> boards) {
        return boards.stream().map(b -> {
            PageBulletinBoardForm pageForm = new PageBulletinBoardForm(b, b.getUser().getNickname());
            return pageForm;
        }).collect(Collectors.toList());
    }

    private void updateBulletinBoard(BulletinBoard findBoard, UpdateBulletinBoardForm updateForm) {
        findBoard.setRegion(updateForm.getRegion());
        findBoard.setTitle(updateForm.getTitle());
        findBoard.setContent(updateForm.getContent());

        //이미지 삭제
        for (Integer id : updateForm.getDeleteImageIds()) {
            UploadFile target = uploadFileRepository.findById(Long.valueOf(id));
            uploadFileRepository.delete(target);
        }

        // TODO: 2023-04-02 file 경로에 있는 이미지 삭제 -> storeName으로

        //이미지 추가
        if (!updateForm.getNewImages().isEmpty()) {
            List<UploadFile> uploadFiles = fileStore.storeFiles(updateForm.getNewImages());
            for (UploadFile uploadFile : uploadFiles) {
                findBoard.addImage(uploadFile);
                uploadFileRepository.save(uploadFile);
            }
        }
        findBoard.setDates(new Dates(findBoard.getDates().getCreateDate(), LocalDateTime.now(), null));
    }

    private List<ReadUploadFileForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(u -> {
                    ReadUploadFileForm readForm = new ReadUploadFileForm(u);
                    return readForm;
                }).collect(Collectors.toList());
    }
}
