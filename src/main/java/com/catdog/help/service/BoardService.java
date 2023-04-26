package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Boolean isBulletin(Long id) {
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        return findBoard instanceof Bulletin;
    }

    public String getWriter(Long id) {
        return boardRepository.findNicknameById(id);
    }
}
