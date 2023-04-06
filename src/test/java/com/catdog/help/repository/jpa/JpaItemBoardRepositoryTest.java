package com.catdog.help.repository.jpa;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.*;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.CommentRepository;
import com.catdog.help.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class JpaItemBoardRepositoryTest {

    @Autowired
    JpaItemBoardRepository itemBoardRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    LikeBoardRepository likeBoardRepository;

    @Test
    void 저장_단건조회_삭제() {
        /**
         * comment, likeBoard 존재
         */
        //유저 생성
        User user1 = createUser("id1@123", "123");
        userRepository.save(user1);

        //상품글 생성
        ItemBoard itemBoard = createItemBoard("강아지 집", 0, user1);
        Long boardId = itemBoardRepository.save(itemBoard);
//
//        //댓글 생성
//        Comment comment = getComment(user1, user1Board1, "user1이 board1을 작성하다.");
//        commentRepository.save(comment);
//
//        //좋아요 생성
//        LikeBoard likeBoard = getLikeBoard(user1, user1Board1);
//        likeBoardRepository.save(likeBoard);

        //게시글 조회
        ItemBoard findBoard = itemBoardRepository.findById(boardId);

        //itemName
        assertThat(findBoard.getItemName()).isEqualTo("강아지 집");

        //price
        assertThat(findBoard.getPrice()).isEqualTo(0);

        //status
        assertThat(findBoard.getStatus()).isEqualTo(ItemStatus.STILL);

        //title
        assertThat(findBoard.getTitle()).isEqualTo("title");

        //user
        assertThat(findBoard.getUser().getEmailId()).isEqualTo("id1@123");

//        //comment
//        assertThat(findBoard1.getComments().stream().findAny()).isEqualTo(1); // TODO: 2023-03-28 게시글 조회 시 유저, 댓글 테이블 다중 조인 후 테스트

        // TODO: 2023-03-25 삭제 테스트 수행
//        assertThat(findBoard1).isEqualTo(board1);
//        assertThat(findBoard2).isEqualTo(board2); todo jdbcTemplate 에서는 유효하지 않은 테스트.. 테스트 방향성을 고민해보자.
    }

    @Test
    void findPage() throws InterruptedException {
        //given
        User user1 = createUser("id1@123", "123");
        User user2 = createUser("id2@123", "123");
        userRepository.save(user1);
        userRepository.save(user2);

        ItemBoard itemBoard1 = createItemBoard("강아지 집", 0, user1);
        TimeUnit.SECONDS.sleep(1);
        ItemBoard itemBoard2 = createItemBoard("강아지 옷", 5000, user1);
        TimeUnit.SECONDS.sleep(1);
        ItemBoard itemBoard3 = createItemBoard("장난감", 0, user2);
        itemBoardRepository.save(itemBoard1);
        itemBoardRepository.save(itemBoard2);
        itemBoardRepository.save(itemBoard3);

        //when
        List<ItemBoard> findBoards = itemBoardRepository.findPage(0, 2);

        //then
        //게시판 생성 날짜 기준으로 내림차순 정렬
        assertThat(findBoards.get(0).getItemName()).isEqualTo("장난감");
        assertThat(findBoards.get(1).getItemName()).isEqualTo("강아지 옷");

        //페이징 범위 초과
        assertThatThrownBy(()->findBoards.get(2).getItemName()).isInstanceOf(IndexOutOfBoundsException.class);

        assertThat(findBoards.size()).isEqualTo(2);
    }

    private static ItemBoard createItemBoard(String itemName, int price, User user) {
        ItemBoard board = new ItemBoard();
        board.setItemName(itemName);
        board.setPrice(price);
        board.setTitle("title");
        board.setContent("content");
        board.setStatus(ItemStatus.STILL);
        board.setUser(user);
        board.setDates(new Dates(LocalDateTime.now(), null, null));
        return board;
    }

    private static User createUser(String emailId, String password) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        return user;
    }

    private static Comment getComment(User user, BulletinBoard board, String content) {
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setUser(user);
        comment.setContent(content);
//        comment.setParent(new Comment()); //jpa 아니면 여기서 error
        comment.setDates(new Dates(LocalDateTime.now(), null, null));

        return comment;
    }

    private static LikeBoard getLikeBoard(User user, BulletinBoard board) {
        LikeBoard likeBoard = new LikeBoard();
        likeBoard.setBoard(board);
        likeBoard.setUser(user);
        return likeBoard;
    }
}