package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final JpaUserRepository userRepository;
    private final JpaBulletinBoardRepository jpaBulletinBoardRepository;
    private final JpaItemBoardRepository itemBoardRepository;
    private final JpaLikeBoardRepository jpaLikeBoardRepository;

    public boolean checkLike(Long boardId, String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        LikeBoard likeBoard = jpaLikeBoardRepository.findByIds(boardId, findUser.getId());
        if (likeBoard == null) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public boolean clickLike(Long boardId, String nickName) {
        Board findBoard;
        if (!(jpaBulletinBoardRepository.findById(boardId) == null)) {
            findBoard = jpaBulletinBoardRepository.findById(boardId);
        } else {
            findBoard = itemBoardRepository.findById(boardId);
        }
        User findUser = userRepository.findByNickName(nickName);

        LikeBoard findLikeBoard = jpaLikeBoardRepository.findByIds(boardId, findUser.getId());
        if (findLikeBoard == null) {
            LikeBoard likeBoard = new LikeBoard(findBoard, findUser);
            jpaLikeBoardRepository.save(likeBoard);
            return true;
        } else {
            jpaLikeBoardRepository.delete(findLikeBoard);
            return false;
        }
    }
}
