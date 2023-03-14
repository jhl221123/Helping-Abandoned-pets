package com.catdog.help.repository;

import com.catdog.help.domain.Board.LikeBoard;

public interface LikeBoardRepository {

    public Long save(LikeBoard likeBoard);

    public LikeBoard findByIds(Long boardId, Long userId);

    public void delete(LikeBoard likeBoard);
}
