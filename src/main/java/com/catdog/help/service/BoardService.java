package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Lost;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.web.form.BoardByRegion;
import com.catdog.help.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;


    public Boolean isLost(Long id) {
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        return findBoard instanceof Lost;
    }

    public Boolean isBulletin(Long id) {
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        return findBoard instanceof Bulletin;
    }

    public String getWriter(Long id) {
        return boardRepository.findNicknameById(id);
    }

    public List<BoardByRegion> getLostByRegion() {
        return boardRepository.countLostByRegion();
    }

    public List<BoardByRegion> getBulletinByRegion() {
        return boardRepository.countBulletinByRegion();
    }

    public List<BoardByRegion> getItemByRegion() {
        return boardRepository.countItemByRegion();
    }
}
