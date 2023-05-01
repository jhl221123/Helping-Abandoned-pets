package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final BoardRepository boardRepository;
    @Transactional
    public void addViews(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        findBoard.addViews();
    }
}
