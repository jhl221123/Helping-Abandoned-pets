package com.catdog.help.service;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.InquiryRepository;
import com.catdog.help.repository.SearchQueryRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.search.InquirySearch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.catdog.help.domain.board.SecretStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @InjectMocks
    InquiryService inquiryService;

    @Mock
    InquiryRepository inquiryRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    SearchQueryRepository searchQueryRepository;


    @Test
    @DisplayName("문의글 단건 조회")
    void readOne() {
        //given
        Inquiry board = getInquiry("제목");

        doReturn(Optional.ofNullable(board)).when(inquiryRepository)
                .findById(board.getId());

        //when
        ReadInquiryForm form = inquiryService.read(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("닉네임으로 문의글 수 조회")
    void getCountByNickname() {
        //given
        Inquiry board = getInquiry("제목");
        List<Inquiry> boards = new ArrayList<>();
        boards.add(board);

        doReturn(1L).when(inquiryRepository)
                .countByNickname("닉네임");

        //when
        Long result = inquiryService.countByNickname("닉네임");

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("닉네임으로 문의글 페이지 조회")
    void readPage() {
        //given
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        Page<Inquiry> page = Page.empty();

        doReturn(page).when(inquiryRepository)
                .findPageByNickname("닉네임", pageable);

        //expected
        Page<PageInquiryForm> formPage = inquiryService.getPageByNickname("닉네임", pageable);
        verify(inquiryRepository, times(1)).findPageByNickname("닉네임", pageable); // TODO: 2023-04-25 map이 잘 작동하는지 확인 부족함.
    }

    @Test
    @DisplayName("검색 조건에 맞는 문의글 페이지 조회")
    void searchPageByCond() {
        //given
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        Page<Inquiry> page = Page.empty();

        InquirySearch search = InquirySearch.builder()
                .title("검색제목")
                .build();

        doReturn(page).when(searchQueryRepository)
                .searchInquiry(search.getTitle(), pageable);

        //expected
        Page<PageInquiryForm> formPage = inquiryService.search(search, pageable);
        verify(searchQueryRepository, times(1)).searchInquiry(search.getTitle(), pageable); // TODO: 2023-04-25 map이 잘 작동하는지 확인 부족함.
    }

    @Test
    @DisplayName("문의글 수정 양식 호출")
    void getEditForm() {
        //given
        Inquiry board = getInquiry("제목");

        doReturn(Optional.ofNullable(board)).when(inquiryRepository)
                .findById(board.getId());

        //when
        EditInquiryForm form = inquiryService.getEditForm(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo(board.getTitle());

        //verify
        verify(inquiryRepository, times(1)).findById(board.getId());
    }

    @Test
    @DisplayName("문의글 수정")
    void update() {
        //given
        Inquiry board = getInquiry("제목");

        EditInquiryForm form = getAfterEditForm("제목수정");

        doReturn(Optional.ofNullable(board)).when(inquiryRepository)
                .findById(form.getId());

        //when
        inquiryService.update(form);

        //then
        assertThat(board.getTitle()).isEqualTo("제목수정");
    }

    @Test
    @DisplayName("문의글 삭제")
    void delete() {
        //given
        Inquiry board = getInquiry("제목");

        doReturn(Optional.ofNullable(board)).when(inquiryRepository)
                .findById(board.getId());

        //expected
        inquiryService.delete(board.getId());
        verify(inquiryRepository, times(1)).delete(board);
    }


    private EditInquiryForm getAfterEditForm(String title) {
        Inquiry updatedBoard = getInquiry(title);
        return new EditInquiryForm(updatedBoard);
    }

    private Inquiry getInquiry(String title) {
        User user = getUser();
        userRepository.save(user);

        return Inquiry.builder()
                .user(user)
                .title(title)
                .content("내용")
                .secret(OPEN)
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