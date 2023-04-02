package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.*;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.bulletinboard.PageBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BulletinBoardService {

    private final BulletinBoardRepository bulletinBoardRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final FileStore fileStore;
    private final LikeBoardRepository likeBoardRepository;


    /** 게시글 로직 */

    @Transactional
    public Long createBoard(SaveBulletinBoardForm boardForm, String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        BulletinBoard board = createBulletinBoard(boardForm, findUser);
        Long boardId = bulletinBoardRepository.save(board); //cascade All 설정 후 리스트에 추가해서 보드만 저장해도 될듯!? 고민 필요

        return boardId;
    }

    public BulletinBoardDto readBoard(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(id);
        User user = findBoard.getUser();
        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(id);
        int likeBoardSize = likeBoardRepository.findAllByBoardId(id).size();
        BulletinBoardDto bulletinBoardDto = getBulletinBoardDto(findBoard, user, uploadFiles, likeBoardSize);
        return bulletinBoardDto;
    }

    public List<PageBulletinBoardForm> readPage(int page) {
        int start = page * 10 - 10;
        int total = 10;

        List<PageBulletinBoardForm> pageBoardForms = new ArrayList<>();
        List<BulletinBoard> boards = bulletinBoardRepository.findPage(start, total);
        for (BulletinBoard board : boards) {
            User user = board.getUser();
            pageBoardForms.add(getPageBulletinBoardForm(board, user.getNickName())); // TODO: 2023-03-12 작동 잘되면 User 대신 nickName으로 시도
        }
        return pageBoardForms;
    }

    public int getNumberOfPages() {
        int totalBoards = bulletinBoardRepository.findAll().size(); // TODO: 2023-03-28 카운트 쿼리로 대체 고려
        if (totalBoards <= 10) {
            return 1;
        } else if (totalBoards % 10 == 0) {
            return totalBoards / 10;
        } else {
            return totalBoards / 10 + 1;
        }
    }

    public UpdateBulletinBoardForm getUpdateForm(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(id);
        UpdateBulletinBoardForm updateForm = new UpdateBulletinBoardForm();
        updateForm.setId(findBoard.getId());
        updateForm.setRegion(findBoard.getRegion());
        updateForm.setTitle(findBoard.getTitle());
        updateForm.setContent(findBoard.getContent());
        updateForm.setOldImages(uploadFileRepository.findUploadFiles(id));
        updateForm.setDates(findBoard.getDates()); //수정된 날짜로 변경
        return updateForm;
    }

    @Transactional
    public Long updateBoard(UpdateBulletinBoardForm updateForm) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(updateForm.getId());

        //이미지 삭제
        for (Integer id : updateForm.getDeleteImageIds()) {
            UploadFile target = uploadFileRepository.findById(Long.valueOf(id));
            uploadFileRepository.delete(target);
        }
        
        //게시글 내용 변경 및 이미지 추가
        updateBulletinBoard(findBoard, updateForm);
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

    @Transactional
    public void addViews(Long boardId) {
        BulletinBoard findBoard = bulletinBoardRepository.findById(boardId);
        findBoard.addViews();
        // TODO: 2023-03-29 조회수만 업데이트 하는데 findOne(fetch join) 쿼리가 불편. 리팩토링 필요
    }

    /**============================= private method ==============================*/

    private BulletinBoard createBulletinBoard(SaveBulletinBoardForm boardForm, User findUser) {
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

    private BulletinBoardDto getBulletinBoardDto(BulletinBoard findBoard, User user, List<UploadFile> uploadFiles, int likeBoardSize) {
        BulletinBoardDto bulletinBoardDto = new BulletinBoardDto();
        bulletinBoardDto.setId(findBoard.getId());
        bulletinBoardDto.setUser(user);
        bulletinBoardDto.setRegion(findBoard.getRegion());
        bulletinBoardDto.setTitle(findBoard.getTitle());
        bulletinBoardDto.setContent(findBoard.getContent());
        bulletinBoardDto.setImages(uploadFiles);
        bulletinBoardDto.setDates(findBoard.getDates());
        bulletinBoardDto.setViews(findBoard.getViews());
        bulletinBoardDto.setLikeBoardSize(likeBoardSize);
        return bulletinBoardDto;
    }

    private PageBulletinBoardForm getPageBulletinBoardForm(BulletinBoard board, String nickName) {
        PageBulletinBoardForm boardForm = new PageBulletinBoardForm();
        boardForm.setId(board.getId());
        boardForm.setRegion(board.getRegion());
        boardForm.setTitle(board.getTitle());
        boardForm.setUserNickName(nickName);
        boardForm.setDates(board.getDates());
        boardForm.setViews(board.getViews());
        return boardForm;
    }

    private void updateBulletinBoard(BulletinBoard findBoard, UpdateBulletinBoardForm updateForm) {
        findBoard.setRegion(updateForm.getRegion());
        findBoard.setTitle(updateForm.getTitle());
        findBoard.setContent(updateForm.getContent());
        // TODO: 2023-03-29 기존 이미지 삭제 로직 -> storeName으로 이미지 삭제, id로 데이터 삭제

        List<UploadFile> uploadFiles = fileStore.storeFiles(updateForm.getNewImages());
        for (UploadFile uploadFile : uploadFiles) {
            findBoard.addImage(uploadFile);
            uploadFileRepository.save(uploadFile);
        }
        findBoard.setDates(new Dates(findBoard.getDates().getCreateDate(), LocalDateTime.now(), null));
    }
}
