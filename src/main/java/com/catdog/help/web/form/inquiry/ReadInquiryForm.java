package com.catdog.help.web.form.inquiry;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.board.SecretStatus;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.catdog.help.domain.board.SecretStatus.SECRET;

@Getter
public class ReadInquiryForm {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private SecretStatus secret;

    public ReadInquiryForm(Inquiry findBoard) {
        this.id = findBoard.getId();
        this.nickname = findBoard.getUser().getNickname();
        this.title = findBoard.getTitle();
        this.content = findBoard.getContent();
        this.createdDate = findBoard.getCreatedDate();
        this.secret = findBoard.getSecret();
    }


    public Boolean isSecret() {
        if (this.secret.equals(SECRET)) {
            return true;
        } else return false;
    }
}
