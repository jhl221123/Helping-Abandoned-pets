package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
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
class BulletinRepositoryTest {

    @Autowired
    BulletinRepository bulletinRepository;

    @Autowired UserRepository userRepository;


    @Test
    @DisplayName("게시글 저장")
    void save() {
        //given
        Bulletin bulletin = getBulletin();

        //when
        Bulletin savedBulletin = bulletinRepository.save(bulletin);

        //then
        assertThat(savedBulletin.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("id로 단건 조회")
    void findById() {
        //given
        Bulletin bulletin = getBulletin();
        bulletinRepository.save(bulletin);

        //when
        Bulletin findBulletin = bulletinRepository.findById(bulletin.getId()).get();

        //then
        assertThat(findBulletin.getTitle()).isEqualTo(bulletin.getTitle());
    }

    @Test
    @DisplayName("페이지 조회")
    void findPage() {
        //given
        setBulletinList();
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Bulletin> page = bulletinRepository.findPageBy(pageRequest);

        //then
        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("제목_5");
    }

    @Test
    @DisplayName("총 개수 조회")
    void countAll() {
        //given
        setBulletinList();

        //when
        long count = bulletinRepository.count();

        //then
        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() {
        //given
        Bulletin bulletin = getBulletin();
        bulletinRepository.save(bulletin);
        assertThat(bulletinRepository.count()).isEqualTo(1L);

        //when
        bulletinRepository.delete(bulletin);

        //then
        assertThat(bulletinRepository.count()).isEqualTo(0L);
    }


    private Bulletin getBulletin() {
        User user = getUser();
        userRepository.save(user);

        return Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }

    private void setBulletinList() {
        User user = getUser();
        userRepository.save(user);

        for (int i = 1; i <= 5; i++) {
            Bulletin board = Bulletin.builder()
                    .user(user)
                    .title("제목_" + i)
                    .content("내용_" + i)
                    .region("지역_" + i)
                    .build();
            bulletinRepository.save(board);
        }
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