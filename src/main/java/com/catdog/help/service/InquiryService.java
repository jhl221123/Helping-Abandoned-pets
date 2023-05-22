package com.catdog.help.service;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.InquiryRepository;
import com.catdog.help.repository.SearchQueryRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import com.catdog.help.web.form.search.InquirySearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final SearchQueryRepository searchQueryRepository;


    @Transactional
    public Long save(SaveInquiryForm form, String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);

        Inquiry inquiry = Inquiry.builder()
                .user(findUser)
                .title(form.getTitle())
                .content(form.getContent())
                .secret(form.getSecret())
                .build();

        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    public ReadInquiryForm read(Long id) {
        Inquiry findBoard = inquiryRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        return new ReadInquiryForm(findBoard);
    }

    public Long countByNickname(String nickname) {
        return inquiryRepository.countByNickname(nickname);
    }

    public Page<PageInquiryForm> getPageByNickname(String nickname, Pageable pageable) {
        return inquiryRepository.findPageByNickname(nickname, pageable)
                .map(PageInquiryForm::new);
    }

    public Page<PageInquiryForm> search(InquirySearch search, Pageable pageable) {
        return searchQueryRepository.searchInquiry(search.getTitle(), pageable)
                .map(PageInquiryForm::new);
    }

    public EditInquiryForm getEditForm(Long id) {
        Inquiry findBoard = inquiryRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        return new EditInquiryForm(findBoard);
    }

    @Transactional
    public void update(EditInquiryForm form) {
        Inquiry findBoard = inquiryRepository.findById(form.getId())
                .orElseThrow(BoardNotFoundException::new);
        findBoard.updateBoard(form.getTitle(), form.getContent(), form.getSecret());
    }

    @Transactional
    public void delete(Long id) {
        Inquiry findBoard = inquiryRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);//본인 검증 -> 영속성 컨텍스트에 이미 존재
        inquiryRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private Page<PageInquiryForm> getPageInquiryForms(Page<Inquiry> findPage) {
        return findPage.map(PageInquiryForm::new);
    }
}