package com.catdog.help.service;

import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.UpdateBulletinBoardForm;
import com.catdog.help.domain.Board.BulletinBoard;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.web.form.SaveBulletinBoardForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BulletinBoardServiceImpl implements BulletinBoardService {

    private final BulletinBoardRepository bulletinBoardRepository;

    @Transactional
    public Long createBoard(SaveBulletinBoardForm boardForm) {
        BulletinBoard board = createBulletinBoard(boardForm);
        bulletinBoardRepository.save(board);
        return board.getId();
    }

    public BulletinBoardDto readBoard(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(id);
        BulletinBoardDto bulletinBoardDto = getBulletinBoardDto(findBoard);
        return bulletinBoardDto;
    }

    public UpdateBulletinBoardForm getUpdateForm(Long id) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(id);
        UpdateBulletinBoardForm updateForm = new UpdateBulletinBoardForm();
        updateForm.setId(findBoard.getId());
        updateForm.setRegion(findBoard.getRegion());
        updateForm.setTitle(findBoard.getTitle());
        updateForm.setContent(findBoard.getContent());
        updateForm.setImage(findBoard.getImage());
        updateForm.setWriteDate(findBoard.getWriteDate());
        return updateForm;
    }

    @Transactional
    public Long updateBoard(UpdateBulletinBoardForm updateForm) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(updateForm.getId());
        updateBulletinBoard(findBoard, updateForm);
        return findBoard.getId();
    }

    public List<BulletinBoardDto> readAll() {
        List<BulletinBoardDto> bulletinBoardDtos = new ArrayList<>();
        List<BulletinBoard> boards = bulletinBoardRepository.findAll();
        for (BulletinBoard board : boards) {
            bulletinBoardDtos.add(getBulletinBoardDto(board));
        }
        return bulletinBoardDtos;
    }

    private static BulletinBoard createBulletinBoard(SaveBulletinBoardForm boardForm) {
        BulletinBoard board = new BulletinBoard();
        //todo -> user
        board.setRegion(boardForm.getRegion());
        board.setTitle(boardForm.getTitle());
        board.setContent(boardForm.getContent());
        board.setImage(boardForm.getImage()); //todo -> 파일업로드 구현
        board.setScore(0);
        board.setWriteDate(LocalDateTime.now());
        return board;
    }

    private static BulletinBoardDto getBulletinBoardDto(BulletinBoard findBoard) {
        BulletinBoardDto bulletinBoardDto = new BulletinBoardDto();
        bulletinBoardDto.setId(findBoard.getId());
        bulletinBoardDto.setRegion(findBoard.getRegion());
        bulletinBoardDto.setTitle(findBoard.getTitle());
        bulletinBoardDto.setContent(findBoard.getContent());
        bulletinBoardDto.setImage(findBoard.getImage());
        bulletinBoardDto.setWriteDate(findBoard.getWriteDate());
        return bulletinBoardDto;
    }

    private static void updateBulletinBoard(BulletinBoard findBoard, UpdateBulletinBoardForm updateForm) {
        findBoard.setRegion(updateForm.getRegion());
        findBoard.setTitle(updateForm.getTitle());
        findBoard.setContent(updateForm.getContent());
        findBoard.setImage(updateForm.getImage());
    }
}
