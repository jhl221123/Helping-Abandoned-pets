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
import com.catdog.help.web.form.user.ReadUserForm;
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

        ReadUserForm readUserForm = getReadUserForm(findBoard.getUser());

        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(id);
        List<ReadUploadFileForm> readUploadFileForms = getReadUploadFileForms(uploadFiles);

        int likeSize = (int) jpaLikeBoardRepository.countByBoardId(id);
        ReadBulletinBoardForm readBulletinBoardForm = getReadBulletinBoardForm(findBoard, readUserForm, readUploadFileForms, likeSize);
        return readBulletinBoardForm;
    }

    public List<PageBulletinBoardForm> readPage(int page) {
        int offset = page * 10 - 10;
        int limit = 10;

        List<PageBulletinBoardForm> pageBoardForms = new ArrayList<>();
        List<BulletinBoard> boards = bulletinBoardRepository.findPage(offset, limit);
        for (BulletinBoard board : boards) {
            User user = board.getUser();
            pageBoardForms.add(getPageBulletinBoardForm(board, user.getNickname())); // TODO: 2023-03-12 작동 잘되면 User 대신 nickName으로 시도
        }
        return pageBoardForms;
    }

    public int countPages() {
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
        UpdateBulletinBoardForm updateForm = getUpdateBulletinBoardForm(id, findBoard);
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

    private ReadBulletinBoardForm getReadBulletinBoardForm(BulletinBoard findBoard, ReadUserForm readForm, List<ReadUploadFileForm> readUploadFileForms, int likeSize) {
        ReadBulletinBoardForm readBulletinBoardForm = new ReadBulletinBoardForm();
        readBulletinBoardForm.setId(findBoard.getId());
        readBulletinBoardForm.setReadUserForm(readForm);
        readBulletinBoardForm.setRegion(findBoard.getRegion());
        readBulletinBoardForm.setTitle(findBoard.getTitle());
        readBulletinBoardForm.setContent(findBoard.getContent());
        readBulletinBoardForm.setImages(readUploadFileForms);
        readBulletinBoardForm.setDates(findBoard.getDates());
        readBulletinBoardForm.setViews(findBoard.getViews());
        readBulletinBoardForm.setLikeSize(likeSize);
        return readBulletinBoardForm;
    }

    private PageBulletinBoardForm getPageBulletinBoardForm(BulletinBoard board, String nickName) {
        PageBulletinBoardForm boardForm = new PageBulletinBoardForm();
        boardForm.setId(board.getId());
        boardForm.setRegion(board.getRegion());
        boardForm.setTitle(board.getTitle());
        boardForm.setNickname(nickName);
        boardForm.setDates(board.getDates());
        boardForm.setViews(board.getViews());
        return boardForm;
    }

    private UpdateBulletinBoardForm getUpdateBulletinBoardForm(Long id, BulletinBoard findBoard) {
        UpdateBulletinBoardForm updateForm = new UpdateBulletinBoardForm();
        updateForm.setId(findBoard.getId());
        updateForm.setRegion(findBoard.getRegion());
        updateForm.setTitle(findBoard.getTitle());
        updateForm.setContent(findBoard.getContent());
        updateForm.setOldImages(getReadUploadFileForms(uploadFileRepository.findUploadFiles(id)));
        updateForm.setDates(findBoard.getDates()); //수정된 날짜로 변경
        return updateForm;
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

    private ReadUserForm getReadUserForm(User user) {
        ReadUserForm readForm = new ReadUserForm();
        readForm.setId(user.getId());
        readForm.setEmailId(user.getEmailId());
        readForm.setPassword(user.getPassword());
        readForm.setNickname(user.getNickname());
        readForm.setName(user.getName());
        readForm.setAge(user.getAge());
        readForm.setGender(user.getGender());
        readForm.setDates(user.getDates());
        return readForm;
    }

    private List<ReadUploadFileForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(u -> {
                    ReadUploadFileForm readForm = new ReadUploadFileForm();
                    readForm.setId(u.getId());
                    readForm.setStoreFileName(u.getStoreFileName());
                    readForm.setUploadFileName(u.getUploadFileName());
                    return readForm;
                }).collect(Collectors.toList());
    }
}
