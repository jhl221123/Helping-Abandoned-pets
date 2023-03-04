package com.catdog.help.service;

import com.catdog.help.web.UpdateBoardForm;
import com.catdog.help.domain.Board.BulletinBoard;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.web.SaveBoardForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BulletinBoardServiceImpl implements BulletinBoardService {

    private final BulletinBoardRepository bulletinBoardRepository;

    public Long createBoard(SaveBoardForm boardForm) {
        BulletinBoard board = createBulletinBoard(boardForm);
        bulletinBoardRepository.save(board);
        return board.getNo();
    }

    public UpdateBoardForm readBoard(Long boardNo) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(boardNo);
        UpdateBoardForm updateBoardForm = createUpdateBoardForm(findBoard);
        return updateBoardForm;
    }

    public Long updateBoard(UpdateBoardForm updateBoardForm) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(updateBoardForm.getBoardNo());
        updateBulletinBoard(findBoard, updateBoardForm);
        return findBoard.getNo();
    }

    public List<UpdateBoardForm> readAll() {
        List<UpdateBoardForm> updateBoardForms = new ArrayList<>();
        List<BulletinBoard> boards = bulletinBoardRepository.findAll();
        for (BulletinBoard board : boards) {
            updateBoardForms.add(createUpdateBoardForm(board));
        }
        return updateBoardForms;
    }

    public BulletinBoard readOne(Long boardNo) {
        return bulletinBoardRepository.findOne(boardNo);
    }

    private static BulletinBoard createBulletinBoard(SaveBoardForm boardForm) {
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

    private static UpdateBoardForm createUpdateBoardForm(BulletinBoard findBoard) {
        UpdateBoardForm updateBoardForm = new UpdateBoardForm();
        updateBoardForm.setBoardNo(findBoard.getNo());
        updateBoardForm.setRegion(findBoard.getRegion());
        updateBoardForm.setTitle(findBoard.getTitle());
        updateBoardForm.setContent(findBoard.getContent());
        updateBoardForm.setImage(findBoard.getImage());
        updateBoardForm.setWriteDate(findBoard.getWriteDate());
        return updateBoardForm;
    }

    private static void updateBulletinBoard(BulletinBoard findBoard, UpdateBoardForm updateBoardForm) {
        findBoard.setRegion(updateBoardForm.getRegion());
        findBoard.setTitle(updateBoardForm.getTitle());
        findBoard.setContent(updateBoardForm.getContent());
        findBoard.setImage(updateBoardForm.getImage());
    }
}
