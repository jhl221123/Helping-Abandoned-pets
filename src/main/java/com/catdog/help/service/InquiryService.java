package com.catdog.help.service;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaInquiryRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.inquiry.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final JpaInquiryRepository inquiryRepository;
    private final JpaUserRepository userRepository;

    @Transactional
    public Long saveBoard(SaveInquiryForm form) {
        User findUser = userRepository.findByNickname(form.getNickname());
        Inquiry inquiry = Inquiry.builder()
                .user(findUser)
                .title(form.getTitle())
                .content(form.getContent())
                .secret(form.getSecret())
                .build();
        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    public ReadInquiryForm readBoard(Long id) {
        return new ReadInquiryForm(inquiryRepository.findById(id));
    }

    public List<PageInquiryForm> readPage(int page) {
        int offset = page * 10 - 10;
        int limit = 10;

        return getPageInquiryForms(inquiryRepository.findPage(offset, limit));
    }

    public int countPage() {
        int total = (int)inquiryRepository.countAll();
        if (total <= 10) {
            return 1;
        } else if (total % 10 == 0) {
            return total / 10;
        } else {
            return total / 10 + 1;
        }
    }

    public EditInquiryForm getEditForm(Long id) {
        Inquiry findBoard = inquiryRepository.findById(id);
        return new EditInquiryForm(findBoard);
    }

    @Transactional
    public void updateBoard(EditInquiryForm form) {
        Inquiry findBoard = inquiryRepository.findById(form.getId());
        findBoard.updateBoard(form.getTitle(), form.getContent(), form.getSecret());
    }

    @Transactional
    public void deleteBoard(Long id) {
        Inquiry findBoard = inquiryRepository.findById(id); //본인 검증 -> 영속성 컨텍스트에 이미 존재
        inquiryRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private List<PageInquiryForm> getPageInquiryForms(List<Inquiry> findPage) {
        return findPage.stream()
                .map(PageInquiryForm::new)
                .collect(Collectors.toList());
    }
}