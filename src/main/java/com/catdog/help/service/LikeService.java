package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Like;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.BoardRepository;
import com.catdog.help.repository.LikeRepository;
import com.catdog.help.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;


    public boolean isLike(Long boardId, String nickName) {
        User findUser = userRepository.findByNickname(nickName)
                .orElseThrow(UserNotFoundException::new);

        Optional<Like> findLike = likeRepository.findByIds(boardId, findUser.getId());

        return findLike.isPresent() ? true : false;
    }

    @Transactional
    public void clickLike(Long boardId, String nickName) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        User findUser = userRepository.findByNickname(nickName)
                .orElseThrow(UserNotFoundException::new);

        Optional<Like> findLike = likeRepository.findByIds(boardId, findUser.getId());
        if (findLike.isEmpty()) {
            likeRepository.save(getLike(findBoard, findUser));
        } else {
            likeRepository.delete(findLike.get());
        }
    }


    private Like getLike(Board findBoard, User findUser) {
        return Like.builder()
                .board(findBoard)
                .user(findUser)
                .build();
    }
}