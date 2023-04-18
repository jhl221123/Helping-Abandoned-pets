package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final JpaUserRepository userRepository;
    private final JpaBoardRepository boardRepository;
    private final JpaLikeBoardRepository jpaLikeBoardRepository;


    public boolean checkLike(Long boardId, String nickName) {
        User findUser = userRepository.findByNickname(nickName);
        LikeBoard likeBoard = jpaLikeBoardRepository.findByIds(boardId, findUser.getId());

        return likeBoard == null ? false : true;
    }

    @Transactional
    public boolean clickLike(Long boardId, String nickName) {
        Board findBoard = boardRepository.findById(boardId);
        User findUser = userRepository.findByNickname(nickName);

        LikeBoard findLikeBoard = jpaLikeBoardRepository.findByIds(boardId, findUser.getId());
        if (findLikeBoard == null) {
            jpaLikeBoardRepository.save(getLikeBoard(findBoard, findUser));
            return true;
        } else {
            jpaLikeBoardRepository.delete(findLikeBoard);
            return false;
        }
    }


    private LikeBoard getLikeBoard(Board findBoard, User findUser) {
        LikeBoard likeBoard = LikeBoard.builder()
                .board(findBoard)
                .user(findUser)
                .build();
        return likeBoard;
    }
}