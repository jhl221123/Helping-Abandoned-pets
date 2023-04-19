package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.exception.NotFoundBoard;
import com.catdog.help.repository.jpa.JpaBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final JpaBoardRepository boardRepository;

    public Boolean isBulletin(Long id) {
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(NotFoundBoard::new);
        return findBoard instanceof BulletinBoard;
    }
}
