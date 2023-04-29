package com.catdog.help.service;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.InquiryRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
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

    public Page<PageInquiryForm> getPage(Pageable pageable) {
        return inquiryRepository.findPageBy(pageable)
                .map(PageInquiryForm::new);
    }
//
//    public int countPage() {
//        int total = (int)inquiryRepository.countAll();
//        if (total <= 10) {
//            return 1;
//        } else if (total % 10 == 0) {
//            return total / 10;
//        } else {
//            return total / 10 + 1;
//        }
//    }

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