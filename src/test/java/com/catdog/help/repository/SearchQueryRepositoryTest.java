package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SearchQueryRepositoryTest {

    @Autowired
    EntityManager em;

    SearchQueryRepository searchQueryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BulletinRepository bulletinRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @BeforeEach
    public void init() {
        searchQueryRepository = new SearchQueryRepository(em);
    }


    @Test
    @DisplayName("검색 조건으로 게시글 조회 시 제목, 지역 조건 모두 있는 경우")
    void findBulletinByAllCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Bulletin allCond = getBulletin(user, "검색제목", "검색지역");
        Bulletin regionCond = getBulletin(user, "제목", "검색지역");
        Bulletin titleCond = getBulletin(user, "검색제목", "지역");
        bulletinRepository.save(allCond);
        bulletinRepository.save(regionCond);
        bulletinRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Bulletin> pages = searchQueryRepository.searchBulletin("검색", "검색지역", pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("검색 조건으로 게시글 조회 시 제목 조건만 있는 경우")
    void findBulletinByTitleCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Bulletin allCond = getBulletin(user, "검색제목", "검색지역");
        Bulletin regionCond = getBulletin(user, "제목", "검색지역");
        Bulletin titleCond = getBulletin(user, "검색제목", "지역");
        bulletinRepository.save(allCond);
        bulletinRepository.save(regionCond);
        bulletinRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Bulletin> pages = searchQueryRepository.searchBulletin("검색", null, pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("검색 조건으로 게시글 조회 시 지역 조건만 있는 경우")
    void findBulletinByRegionCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Bulletin allCond = getBulletin(user, "검색제목", "검색지역");
        Bulletin regionCond = getBulletin(user, "제목", "검색지역");
        Bulletin titleCond = getBulletin(user, "검색제목", "지역");
        bulletinRepository.save(allCond);
        bulletinRepository.save(regionCond);
        bulletinRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Bulletin> pages = searchQueryRepository.searchBulletin(null, "검색지역", pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("검색 조건으로 게시글 조회 시 조건이 없는 경우")
    void findBulletinByNoCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Bulletin allCond = getBulletin(user, "검색제목", "검색지역");
        Bulletin regionCond = getBulletin(user, "제목", "검색지역");
        Bulletin titleCond = getBulletin(user, "검색제목", "지역");
        bulletinRepository.save(allCond);
        bulletinRepository.save(regionCond);
        bulletinRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Bulletin> pages = searchQueryRepository.searchBulletin(null, null, pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("검색 조건으로 나눔글 조회 시 제목, 상품명 조건 모두 있는 경우")
    void findItemByAllCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Item allCond = getItem(user, "검색제목", "검색상품");
        Item titleCond = getItem(user, "검색제목", "상품");
        Item itemNameCond = getItem(user, "제목", "검색상품");
        itemRepository.save(allCond);
        itemRepository.save(itemNameCond);
        itemRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Item> pages = searchQueryRepository.searchItem("검색", "검색", pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("검색 조건으로 나눔글 조회 시 제목 조건만 있는 경우")
    void findItemByTitleCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Item allCond = getItem(user, "검색제목", "검색상품");
        Item titleCond = getItem(user, "검색제목", "상품");
        Item itemNameCond = getItem(user, "제목", "검색상품");
        itemRepository.save(allCond);
        itemRepository.save(itemNameCond);
        itemRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Item> pages = searchQueryRepository.searchItem("검색", null, pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("검색 조건으로 나눔글 조회 시 상품명 조건만 있는 경우")
    void findItemByItemNameCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Item allCond = getItem(user, "검색제목", "검색상품");
        Item titleCond = getItem(user, "검색제목", "상품");
        Item itemNameCond = getItem(user, "제목", "검색상품");
        itemRepository.save(allCond);
        itemRepository.save(itemNameCond);
        itemRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Item> pages = searchQueryRepository.searchItem(null, "검색", pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("검색 조건으로 나눔글 조회 시 조건이 없는 경우")
    void findItemByNoCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Item allCond = getItem(user, "검색제목", "검색상품");
        Item titleCond = getItem(user, "검색제목", "상품");
        Item itemNameCond = getItem(user, "제목", "검색상품");
        itemRepository.save(allCond);
        itemRepository.save(itemNameCond);
        itemRepository.save(titleCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Item> pages = searchQueryRepository.searchItem(null, null, pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("검색 조건으로 문의글 조회 시 제목 조건이 있는 경우")
    void findInquiryByTitleCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Inquiry titleCond = getInquiry(user, "검색제목");
        Inquiry noCond = getInquiry(user, "제목");
        inquiryRepository.save(titleCond);
        inquiryRepository.save(noCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Inquiry> pages = searchQueryRepository.searchInquiry("검색제목", pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("검색 조건으로 문의글 조회 시 조건이 없는 경우")
    void findInquiryByNoCond() {
        //given
        User user = getUser();
        userRepository.save(user);

        Inquiry titleCond = getInquiry(user, "검색제목");
        Inquiry noCond = getInquiry(user, "제목");
        inquiryRepository.save(titleCond);
        inquiryRepository.save(noCond);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Inquiry> pages = searchQueryRepository.searchInquiry(null, pageRequest);

        //then
        assertThat(pages.getContent().size()).isEqualTo(2);
    }


    private Inquiry getInquiry(User user, String title) {
        return Inquiry.builder()
                .user(user)
                .title(title)
                .content("내용")
                .secret(false)
                .build();
    }

    private Item getItem(User user, String title, String itemName) {
        return Item.builder()
                .user(user)
                .title(title)
                .content("내용")
                .itemName(itemName)
                .price(1000)
                .build();
    }

    private Bulletin getBulletin(User user, String title, String region) {
        return Bulletin.builder()
                .user(user)
                .title(title)
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