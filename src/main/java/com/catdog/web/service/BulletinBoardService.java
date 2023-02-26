package com.catdog.web.service;

import com.catdog.web.controller.BoardForm;
import com.catdog.web.domain.BulletinBoard;
import com.catdog.web.repository.BulletinBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BulletinBoardService {

    private final BulletinBoardRepository bulletinBoardRepository;

    public Long createBoard(BoardForm boardForm) {
        BulletinBoard board = createBulletinBoard(boardForm);
        bulletinBoardRepository.save(board);
        return board.getNo();
    }

    public BoardForm readBoard(Long boardNo) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(boardNo);
        BoardForm boardForm = createBoardForm(findBoard);
        boardForm.setReadOnly(true);
        return boardForm;
    }

    public BoardForm updateBoard(Long boardNo) {
        BulletinBoard findBoard = bulletinBoardRepository.findOne(boardNo);
        BoardForm boardForm = createBoardForm(findBoard);
        boardForm.setReadOnly(false);
        return boardForm;
    }

    public List<BoardForm> readAll() {
        List<BoardForm> boardForms = new ArrayList<>();
        List<BulletinBoard> boards = bulletinBoardRepository.findAll();
        for (BulletinBoard board : boards) {
            boardForms.add(createBoardForm(board));
        }
        return boardForms;
    }

    public BulletinBoard readOne(Long boardNo) {
        return bulletinBoardRepository.findOne(boardNo);
    }

    private static BulletinBoard createBulletinBoard(BoardForm boardForm) {
        BulletinBoard board = new BulletinBoard();
        board.setRegion(boardForm.getRegion());
        board.setTitle(boardForm.getTitle());
        board.setContent(boardForm.getContent());
        board.setImage(boardForm.getImage());
        board.setScore(0);
        board.setWriteDate(LocalDateTime.now());
        return board;
    }

    private static BoardForm createBoardForm(BulletinBoard findBoard) {
        BoardForm boardForm = new BoardForm();
        boardForm.setBoardNo(findBoard.getNo());
        boardForm.setRegion(findBoard.getRegion());
        boardForm.setTitle(findBoard.getTitle());
        boardForm.setContent(findBoard.getContent());
        boardForm.setImage(findBoard.getImage());
        boardForm.setWriteDate(findBoard.getWriteDate());
        return boardForm;
    }


}
