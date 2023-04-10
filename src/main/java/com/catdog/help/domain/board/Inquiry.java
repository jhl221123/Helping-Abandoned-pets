package com.catdog.help.domain.board;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("inquiry")
@Getter @Setter
public class Inquiry extends Board{
    private Boolean secret;
}
