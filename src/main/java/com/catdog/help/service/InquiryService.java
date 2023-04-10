package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaInquiryRepository;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final JpaInquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveBoard(SaveInquiryForm saveForm) {
        Inquiry inquiry = getInquiry(saveForm);
        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    public ReadInquiryForm readForm(Long id) {
        Inquiry findBoard = inquiryRepository.findById(id);
        ReadInquiryForm readForm = getReadInquiryForm(findBoard);
        return readForm;
    }

    public List<PageInquiryForm> readPage(int offset, int limit) {
        List<Inquiry> page = inquiryRepository.findPage(offset, limit);
        return page.stream().map(i -> new PageInquiryForm(i)).collect(Collectors.toList());
    }

    /**============================= private method ==============================*/

    private Inquiry getInquiry(SaveInquiryForm saveForm) {
        Inquiry inquiry = new Inquiry();
        inquiry.setUser(userRepository.findByNickName(saveForm.getNickname()));
        inquiry.setTitle(saveForm.getTitle());
        inquiry.setContent(saveForm.getContent());
        inquiry.setDates(new Dates(LocalDateTime.now(), null, null));
        inquiry.setSecret(saveForm.getSecret());
        return inquiry;
    }

    private ReadInquiryForm getReadInquiryForm(Inquiry findBoard) {
        ReadInquiryForm readForm = new ReadInquiryForm();
        readForm.setId(findBoard.getId());
        readForm.setNickname(findBoard.getUser().getNickName());
        readForm.setTitle(findBoard.getTitle());
        readForm.setContent(findBoard.getContent());
        readForm.setCreateDate(findBoard.getDates().getCreateDate());
        readForm.setSecret(findBoard.getSecret());
        return readForm;
    }
}
