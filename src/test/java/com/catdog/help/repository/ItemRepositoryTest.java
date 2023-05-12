package com.catdog.help.repository;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.board.Like;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LikeRepository likeRepository;


    @Test
    @DisplayName("나눔글 저장")
    void save() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Item board = getItem(user);

        //when
        Item savedBoard = itemRepository.save(board);

        //then
        assertThat(savedBoard).isEqualTo(board);
    }

    @Test
    @DisplayName("id로 나눔글 단건 조회")
    void findById() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Item board = getItem(user);
        itemRepository.save(board);

        //when
        Item findBoard = itemRepository.findById(board.getId()).get();

        //then
        assertThat(findBoard).isEqualTo(board);
    }

    @Test
    @DisplayName("닉네임으로 나눔글 페이지 조회")
    void findByNickname() {
        //given
        setItemList();
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Item> findBoards = itemRepository.findPageByNickname("닉네임", pageRequest);

        //then
        assertThat(findBoards.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자가 좋아하는 나눔글 페이지 조회 성공")
    void findLikeItemsByNickname() {
        //given
        User user_A = getUser("test@AAAA", "닉네임_A");
        User user_B = getUser("test@BBBB", "닉네임_B");
        userRepository.save(user_A);
        userRepository.save(user_B);

        Item board_A = getItem(user_A);
        Item board_B = getItem(user_B);
        itemRepository.save(board_A);
        itemRepository.save(board_B);

        //user_A가 좋아요를 누른 나눔글은 2개
        Like like_A = getLike(user_A, board_A);
        Like like_B = getLike(user_A, board_B);
        Like like_C = getLike(user_B, board_B);
        likeRepository.save(like_A);
        likeRepository.save(like_B);
        likeRepository.save(like_C);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "board_id");

        //when
        Page<Item> boards = itemRepository.findLikeItems(user_A.getId(), pageRequest);

        //then
        assertThat(boards.getContent().size()).isEqualTo(2L);
    }

    @Test
    @DisplayName("페이지 조회")
    void findPage() {
        //given
        setItemList();
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Item> page = itemRepository.findPageBy(pageRequest);

        //then
        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("제목_5");
    }

    @Test
    @DisplayName("나눔글 삭제")
    void delete() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Item board = getItem(user);
        itemRepository.save(board);
        assertThat(itemRepository.count()).isEqualTo(1L);

        //when
        itemRepository.delete(board);

        //then
        assertThat(itemRepository.count()).isEqualTo(0L);
    }


    private Like getLike(User user, Item board) {
        return Like.builder()
                .board(board)
                .user(user)
                .build();
    }

    private void setItemList() {
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        for (int i = 1; i <= 5; i++) {
            Item board = Item.builder()
                    .user(user)
                    .title("제목_" + i)
                    .content("내용_" + i)
                    .itemName("테스트상품_" + i)
                    .price(i * 1000)
                    .build();
            itemRepository.save(board);
        }
    }

    private Item getItem(User user) {
        return Item.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .itemName("테스트상품")
                .price(1000)
                .build();
    }

    private User getUser(String emailId, String nickname) {
        return User.builder()
                .emailId(emailId)
                .password("12345678")
                .nickname(nickname)
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}