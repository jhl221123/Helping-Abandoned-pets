package com.catdog.help.repository;

import com.catdog.help.domain.board.Inquiry;
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
class InquiryRepositoryTest {

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    UserRepository userRepository;



    @Test
    @DisplayName("게시글 저장")
    void save() {
        //given
        Inquiry board = getInquiry();

        //when
        Inquiry savedBoard = inquiryRepository.save(board);

        //then
        assertThat(savedBoard.getTitle()).isEqualTo("제목");
    }


    @Test
    @DisplayName("id로 단건 조회")
    void findById() {
        //given
        Inquiry board = getInquiry();
        inquiryRepository.save(board);

        //when
        Inquiry findBoard = inquiryRepository.findById(board.getId()).get();

        //then
        assertThat(findBoard.getTitle()).isEqualTo(board.getTitle());
    }

    @Test
    @DisplayName("페이지 조회")
    void findPage() {
        //given
        setInquiryList();
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Inquiry> page = inquiryRepository.findPageBy(pageRequest);

        //then
        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("제목_5");
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() {
        //given
        Inquiry board = getInquiry();
        inquiryRepository.save(board);
        assertThat(inquiryRepository.count()).isEqualTo(1L);

        //when
        inquiryRepository.delete(board);

        //then
        assertThat(inquiryRepository.count()).isEqualTo(0L);
    }


    private void setInquiryList() {
        User user = getUser();
        userRepository.save(user);

        for (int i = 1; i <= 5; i++) {
            Inquiry board = Inquiry.builder()
                    .user(user)
                    .title("제목_" + i)
                    .content("내용_" + i)
                    .secret(false)
                    .build();
            inquiryRepository.save(board);
        }
    }

    private Inquiry getInquiry() {
        User user = getUser();
        userRepository.save(user);

        return Inquiry.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .secret(false)
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