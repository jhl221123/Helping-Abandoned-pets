package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.repository.jpa.JpaBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final JpaBoardRepository boardRepository;
    @Transactional
    public void addViews(Long boardId) {
        Board findBoard = boardRepository.findById(boardId);
        findBoard.addViews();
        // TODO: 2023-03-29 조회수만 업데이트 하는데 findOne(fetch join) 쿼리가 불편. 리팩토링 필요
    }
}
