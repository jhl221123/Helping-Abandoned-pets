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

        Optional<Like> like = likeRepository.findByIds(boardId, findUser.getId());

        return like.isPresent() ? true : false;
    }

    @Transactional
    public void clickLike(Long boardId, String nickName) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        User findUser = userRepository.findByNickname(nickName)
                .orElseThrow(UserNotFoundException::new); // TODO: 2023-04-23 테스트 작성하면서 isLike 로 수정하기

        Optional<Like> findLike = likeRepository.findByIds(boardId, findUser.getId());
        if (findLike.isEmpty()) {
            likeRepository.save(getLikeBoard(findBoard, findUser));
        } else {
            likeRepository.delete(findLike.get());
        }
    }


    private Like getLikeBoard(Board findBoard, User findUser) {
        Like like = Like.builder()
                .board(findBoard)
                .user(findUser)
                .build();
        return like;
    }
}