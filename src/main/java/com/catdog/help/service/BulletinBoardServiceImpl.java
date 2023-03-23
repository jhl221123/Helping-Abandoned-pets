package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.DateList;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.*;
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
public class BulletinBoardServiceImpl implements BulletinBoardService {

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
        for (UploadFile image : board.getImages()) {
            uploadFileRepository.save(image);
        }
        return boardId;
    }

    public BulletinBoardDto readBoard(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(id);
        User user = findBoard.getUser();
        log.info("User={}", user); // TODO: 2023-03-06 지연로딩 이라 일단 로그로 호출  -> fetch 조인 써야하네 여기!
        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(id);
        int likeBoardSize = likeBoardRepository.findByBoardId(id).size();
        BulletinBoardDto bulletinBoardDto = getBulletinBoardDto(findBoard, user, uploadFiles, likeBoardSize);
        return bulletinBoardDto;
    }

    public List<PageBulletinBoardForm> readAll() {
        List<PageBulletinBoardForm> pageBoardForms = new ArrayList<>();
        List<BulletinBoard> boards = bulletinBoardRepository.findAll();
        for (BulletinBoard board : boards) {
            User user = board.getUser();
            log.info("User={}", user); // TODO: 2023-03-06 지연로딩 이라 일단 로그로 호출  -> fetch 조인 써야하네 여기!
            pageBoardForms.add(getPageBulletinBoardForm(board, user.getNickName())); // TODO: 2023-03-12 작동 잘되면 User 대신 nickName으로 시도
        }
        return pageBoardForms;
    }

    public UpdateBulletinBoardForm getUpdateForm(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(id);
        UpdateBulletinBoardForm updateForm = new UpdateBulletinBoardForm();
        updateForm.setId(findBoard.getId());
        updateForm.setRegion(findBoard.getRegion());
        updateForm.setTitle(findBoard.getTitle());
        updateForm.setContent(findBoard.getContent());
        //images 는 생성과 동시에 초기화
        updateForm.setDateList(findBoard.getDateList()); //수정된 날짜로 변경
        return updateForm;
    }

    @Transactional
    public Long updateBoard(UpdateBulletinBoardForm updateForm) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(updateForm.getId());
        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(findBoard.getId());
        updateBulletinBoard(findBoard, updateForm);  //변경감지 이용한 덕분에 user 값 변경없이 수정이 된다!
        return findBoard.getId();
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(boardId);
        //삭제 날짜 추가
        findBoard.setDateList(new DateList(findBoard.getDateList().getCreateDate(),
                findBoard.getDateList().getLastModifiedDate(), LocalDateTime.now()));
        bulletinBoardRepository.delete(findBoard); // TODO: 2023-03-20 복구 가능성을 위해 서비스 계층에서 아이디 보관
    }

    /** 좋아요 로직 */

    public boolean checkLike(Long boardId, String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        LikeBoard likeBoard = likeBoardRepository.findOneByIds(boardId, findUser.getId());
        if (likeBoard == null) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public boolean clickLike(Long boardId, String nickName) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(boardId);
        User findUser = userRepository.findByNickName(nickName);
        LikeBoard findLikeBoard = likeBoardRepository.findOneByIds(findBoard.getId(), findUser.getId());
        if (findLikeBoard == null) {
            LikeBoard likeBoard = new LikeBoard(findBoard, findUser);
            likeBoardRepository.save(likeBoard);
            return true;
        } else {
            likeBoardRepository.delete(findLikeBoard);
            return false;
        }
    }

    @Transactional
    public void addViews(Long boardId) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(boardId);
        findBoard.addViews();
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
                board.addImage(uploadFile); //uploadFile 에 board 주입
            }
        }
        DateList dateList = new DateList(LocalDateTime.now(), null, null);
        board.setDateList(dateList);
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
        bulletinBoardDto.setDateList(findBoard.getDateList());
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
        boardForm.setDateList(board.getDateList());
        boardForm.setViews(board.getViews());
        return boardForm;
    }

    private void updateBulletinBoard(BulletinBoard findBoard, UpdateBulletinBoardForm updateForm) {
        findBoard.setRegion(updateForm.getRegion());
        findBoard.setTitle(updateForm.getTitle());
        findBoard.setContent(updateForm.getContent());
        List<UploadFile> uploadFiles = fileStore.storeFiles(updateForm.getImages());
        for (UploadFile uploadFile : uploadFiles) {
            findBoard.addImage(uploadFile);
            uploadFileRepository.save(uploadFile);
        }
        findBoard.setDateList(new DateList(findBoard.getDateList().getCreateDate(), LocalDateTime.now(), null));
    }
}
