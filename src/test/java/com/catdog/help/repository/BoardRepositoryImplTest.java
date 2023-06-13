package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.BoardByRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BoardRepositoryImplTest {

    @Autowired
    private EntityManager em;

    private BoardRepositoryImpl boardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BulletinRepository bulletinRepository;

    @Autowired
    LostRepository lostRepository;

    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    public void init() {
        boardRepository = new BoardRepositoryImpl(em);
    }


    @Test
    @DisplayName("BoardByRegion 을 통해 지역별 실종글 수 조회 성공")
    void countLostByRegion() {
        //given
        User user = getUser();
        userRepository.save(user);

        Lost busan = getLost(user, "부산");
        Lost seoul = getLost(user, "서울");
        lostRepository.save(busan);
        lostRepository.save(seoul);

        //when
        List<BoardByRegion> result = boardRepository.countLostByRegion();

        //then
        assertThat(result.size()).isEqualTo(2L);
    }

    @Test
    @DisplayName("BoardByRegion 을 통해 지역별 게시글 수 조회 성공")
    void countBulletinByRegion() {
        //given
        User user = getUser();
        userRepository.save(user);

        Bulletin busan = getBulletin(user, "부산");
        Bulletin seoul = getBulletin(user, "서울");
        bulletinRepository.save(busan);
        bulletinRepository.save(seoul);

        //when
        List<BoardByRegion> result = boardRepository.countBulletinByRegion();

        //then
        assertThat(result.size()).isEqualTo(2L);
    }

    @Test
    @DisplayName("BoardByRegion 을 통해 지역별 나눔글 수 조회 성공")
    void countItemByRegion() {
        //given
        User user = getUser();
        userRepository.save(user);

        Item busan = getItem(user, "부산");
        Item seoul = getItem(user, "서울");
        itemRepository.save(busan);
        itemRepository.save(seoul);

        //when
        List<BoardByRegion> result = boardRepository.countItemByRegion();

        //then
        assertThat(result.size()).isEqualTo(2L);
    }


    private Lost getLost(User user, String region) {
        return Lost.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region(region)
                .breed("품종")
                .lostDate(LocalDate.now())
                .lostPlace("실종장소")
                .gratuity(100000)
                .build();
    }

    private Item getItem(User user, String region) {
        return Item.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .itemName("제품명")
                .region(region)
                .price(1000)
                .build();
    }

    private Bulletin getBulletin(User user, String region) {
        return Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region(region)
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