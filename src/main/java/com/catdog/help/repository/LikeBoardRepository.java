package com.catdog.help.repository;

import com.catdog.help.domain.board.LikeBoard;

import java.util.List;

public interface LikeBoardRepository {

    public Long save(LikeBoard likeBoard);

    public LikeBoard findById(Long likeBoardId);

    public LikeBoard findByIds(Long boardId, Long userId);

    public List<LikeBoard> findAllByBoardId(Long boardId);

    public void delete(LikeBoard likeBoard);
}
