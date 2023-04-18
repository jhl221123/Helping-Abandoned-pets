package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("inquiry")
public class Inquiry extends Board{
    private Boolean secret;


    @Builder
    public Inquiry(User user, String title, String content, Boolean secret) {
        super(user, title, content);
        this.secret = secret;
    }


    public void updateBoard(String title, String content, Boolean secret) {
        super.updateBoard(title, content);
        this.secret = secret;
    }
}
