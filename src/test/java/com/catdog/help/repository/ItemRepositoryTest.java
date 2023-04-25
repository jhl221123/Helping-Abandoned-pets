package com.catdog.help.repository;

import com.catdog.help.domain.board.Item;
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


    @Test
    @DisplayName("나눔글 저장")
    void save() {
        //given
        Item board = getItem();

        //when
        Item savedBoard = itemRepository.save(board);

        //then
        assertThat(savedBoard).isEqualTo(board);
    }

    @Test
    @DisplayName("id로 나눔글 단건 조회")
    void findById() {
        //given
        Item board = getItem();
        itemRepository.save(board);

        //when
        Item findBoard = itemRepository.findById(board.getId()).get();

        //then
        assertThat(findBoard).isEqualTo(board);
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
        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("제목_5");
    }

    @Test
    @DisplayName("총 개수 조회")
    void countAll() {
        //given
        setItemList();

        //when
        Long count = itemRepository.count();

        //then
        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() {
        //given
        Item board = getItem();
        itemRepository.save(board);
        assertThat(itemRepository.count()).isEqualTo(1L);

        //when
        itemRepository.delete(board);

        //then
        assertThat(itemRepository.count()).isEqualTo(0L);
    }


    private void setItemList() {
        User user = getUser();
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

    private Item getItem() {
        User user = getUser();
        userRepository.save(user);

        return Item.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .itemName("테스트상품")
                .price(1000)
                .build();
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}