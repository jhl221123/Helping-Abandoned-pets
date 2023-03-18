package com.catdog.help.repository;

import com.catdog.help.domain.board.LikeBoard;

import java.util.List;

public interface LikeBoardRepository {

    public Long save(LikeBoard likeBoard);

    public LikeBoard findOneByIds(Long boardId, Long userId);

    public List<LikeBoard> findByBoardId(Long boardId);

    public void delete(LikeBoard likeBoard);
}
