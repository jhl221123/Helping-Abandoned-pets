package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("inquiry")
public class Inquiry extends Board{

    @Enumerated(value = EnumType.STRING)
    private SecretStatus secret;


    @Builder
    public Inquiry(User user, String title, String content, SecretStatus secret) {
        super(user, title, content);
        this.secret = secret;
    }


    public void updateBoard(String title, String content, SecretStatus secret) {
        super.updateBoard(title, content);
        this.secret = secret;
    }
}
