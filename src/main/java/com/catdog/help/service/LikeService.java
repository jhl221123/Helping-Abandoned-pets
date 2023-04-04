package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.LikeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final BulletinBoardRepository bulletinBoardRepository;
    private final JpaItemBoardRepository itemBoardRepository;
    private final LikeBoardRepository likeBoardRepository;

    public boolean checkLike(Long boardId, String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        LikeBoard likeBoard = likeBoardRepository.findByIds(boardId, findUser.getId());
        if (likeBoard == null) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public boolean clickLike(Long boardId, String nickName) {
        Board findBoard;
        if (!(bulletinBoardRepository.findById(boardId) == null)) {
            findBoard = bulletinBoardRepository.findById(boardId);
        } else {
            findBoard = itemBoardRepository.findById(boardId);
        }
        User findUser = userRepository.findByNickName(nickName);

        LikeBoard findLikeBoard = likeBoardRepository.findByIds(boardId, findUser.getId());
        if (findLikeBoard == null) {
            LikeBoard likeBoard = new LikeBoard(findBoard, findUser);
            likeBoardRepository.save(likeBoard);
            return true;
        } else {
            likeBoardRepository.delete(findLikeBoard);
            return false;
        }
    }
}
