package com.catdog.help;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.*;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import com.catdog.help.web.form.itemboard.SaveItemBoardForm;
import com.catdog.help.web.form.message.SaveMessageForm;
import com.catdog.help.web.form.user.SaveUserForm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestData {

    /** user */

    public User createUser(String emailId, String password, String nickName) {
        SaveUserForm form = getSaveUserForm(emailId, password, nickName);
        return new User(form);
    }

    public SaveUserForm getSaveUserForm(String emailId, String password, String nickName) {
        SaveUserForm form = new SaveUserForm();
        form.setEmailId(emailId);
        form.setPassword(password);
        form.setName("name");
        form.setNickname(nickName);
        form.setAge(28);
        form.setGender(Gender.MAN);
        return form;
    }


    /** bulletinBoard */

    public BulletinBoard createBulletinBoard(String title, User user) {
        SaveBulletinBoardForm form = getSaveBulletinBoardForm(title);
        BulletinBoard board = new BulletinBoard(user, form);
        return board;
    }

    public BulletinBoard createBulletinBoardOnlyTitle(String title) {
        SaveBulletinBoardForm form = getSaveBulletinBoardForm(title);
        BulletinBoard board = new BulletinBoard(new User(new SaveUserForm()), form);
        return board;
    }

    public SaveBulletinBoardForm getSaveBulletinBoardForm(String title) {
        SaveBulletinBoardForm form = new SaveBulletinBoardForm();
        form.setTitle(title);
        form.setContent("content");
        form.setRegion("region");
        return form;
    }


    /** itemBoard */

    public static ItemBoard createItemBoard(String itemName, int price, User user) {
        SaveItemBoardForm form = getSaveItemBoardForm(itemName, price);
        ItemBoard board = new ItemBoard(user, form);
        return board;
    }

    public static SaveItemBoardForm getSaveItemBoardForm(String itemName, int price) {
        SaveItemBoardForm form = new SaveItemBoardForm();
        form.setItemName(itemName);
        form.setPrice(price);
        form.setTitle("title");
        form.setContent("content");
        return form;
    }


    /** inquiry */

    public Inquiry getInquiry(User user, String title, boolean secret) {
        SaveInquiryForm form = getSaveInquiryForm(user.getNickname(), title, secret);
        Inquiry inquiry = new Inquiry(user, form);
        return inquiry;
    }

    public SaveInquiryForm getSaveInquiryForm(String nickname, String title, boolean secret) {
        SaveInquiryForm saveForm = new SaveInquiryForm();
        saveForm.setNickname(nickname);
        saveForm.setTitle(title);
        saveForm.setContent("content");
        saveForm.setSecret(secret);
        return saveForm;
    }


    /** likeBoard */

    public LikeBoard getLikeBoard(BulletinBoard board, User user) {
        LikeBoard likeBoard = new LikeBoard(board, user);
        return likeBoard;
    }


    /** comment */

    public Comment getComment(User user, BulletinBoard board, String content) {
        Comment comment = Comment.builder()
                .board(board)
                .user(user)
                .form(getCommentForm(content))
                .build();
        return comment;
    }

    private CommentForm getCommentForm(String content) {
        CommentForm commentForm = new CommentForm();
        commentForm.setContent(content);
        return commentForm;
    }


    /** message */

    public Message createMessage(User senderC, MessageRoom roomByAAndC) {
        return new Message(roomByAAndC, senderC, new SaveMessageForm());
    }

    public MessageRoom createMessageRoom(User recipientA, User senderC, ItemBoard itemBoardByA) {
        MessageRoom messageRoomByAAndC = new MessageRoom(itemBoardByA, senderC, recipientA);
        return messageRoomByAAndC;
    }


    /** uploadFile */

    public UploadFile getUploadFile(BulletinBoard board, String uploadName) {
        UploadFile uploadFile = UploadFile.builder()
                .uploadFileName(uploadName)
                .storeFileName("storeName")
                .build();
        uploadFile.addBoard(board);
        return uploadFile;
    }
}
