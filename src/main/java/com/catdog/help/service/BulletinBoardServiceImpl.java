package com.catdog.help.service;

import com.catdog.help.domain.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.UpdateBulletinBoardForm;
import com.catdog.help.domain.Board.BulletinBoard;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.web.form.SaveBulletinBoardForm;
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

    @Transactional
    public Long createBoard(SaveBulletinBoardForm boardForm, String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        BulletinBoard board = createBulletinBoard(boardForm, findUser);
        bulletinBoardRepository.save(board);
        return board.getId();
    }

    public BulletinBoardDto readBoard(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(id);
        User user = findBoard.getUser();
        log.info("User={}", user); // TODO: 2023-03-06 지연로딩 이라 일단 로그로 호출
        BulletinBoardDto bulletinBoardDto = getBulletinBoardDto(findBoard, user);
        return bulletinBoardDto;
    }

    public List<BulletinBoardDto> readAll() {
        List<BulletinBoardDto> bulletinBoardDtos = new ArrayList<>();
        List<BulletinBoard> boards = bulletinBoardRepository.findAll();
        for (BulletinBoard board : boards) {
            User user = board.getUser();
            log.info("User={}", user); // TODO: 2023-03-06 지연로딩 이라 일단 로그로 호출
            bulletinBoardDtos.add(getBulletinBoardDto(board, user));
        }
        return bulletinBoardDtos;
    }

    public UpdateBulletinBoardForm getUpdateForm(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(id);
        UpdateBulletinBoardForm updateForm = new UpdateBulletinBoardForm();
        updateForm.setId(findBoard.getId());
        updateForm.setRegion(findBoard.getRegion());
        updateForm.setTitle(findBoard.getTitle());
        updateForm.setContent(findBoard.getContent());
        updateForm.setImage(findBoard.getImage());
        updateForm.setWriteDate(findBoard.getWriteDate()); //수정된 날짜로 변경
        return updateForm;
    }

    @Transactional
    public Long updateBoard(UpdateBulletinBoardForm updateForm) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(updateForm.getId());
        updateBulletinBoard(findBoard, updateForm);  //변경감지 이용한 덕분에 user 값 변경없이 수정이 된다!
        return findBoard.getId();
    }


    /**============================= private method ==============================*/

    private static BulletinBoard createBulletinBoard(SaveBulletinBoardForm boardForm, User findUser) {
        BulletinBoard board = new BulletinBoard();
        board.setUser(findUser);
        board.setRegion(boardForm.getRegion());
        board.setTitle(boardForm.getTitle());
        board.setContent(boardForm.getContent());
        board.setImage(boardForm.getImage()); //todo -> 파일업로드 구현
        board.setScore(0);
        board.setWriteDate(LocalDateTime.now());
        return board;
    }

    private static BulletinBoardDto getBulletinBoardDto(BulletinBoard findBoard, User user) {
        BulletinBoardDto bulletinBoardDto = new BulletinBoardDto();
        bulletinBoardDto.setId(findBoard.getId());
        bulletinBoardDto.setUser(user);
        bulletinBoardDto.setRegion(findBoard.getRegion());
        bulletinBoardDto.setTitle(findBoard.getTitle());
        bulletinBoardDto.setContent(findBoard.getContent());
        bulletinBoardDto.setImage(findBoard.getImage());
        bulletinBoardDto.setWriteDate(findBoard.getWriteDate());
        bulletinBoardDto.setScore(findBoard.getScore());
        return bulletinBoardDto;
    }

    private static void updateBulletinBoard(BulletinBoard findBoard, UpdateBulletinBoardForm updateForm) {
        findBoard.setRegion(updateForm.getRegion());
        findBoard.setTitle(updateForm.getTitle());
        findBoard.setContent(updateForm.getContent());
        findBoard.setImage(updateForm.getImage());
    }
}
