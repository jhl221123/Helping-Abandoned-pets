package com.catdog.help.service;

import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.web.form.bulletinboard.ReadBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BulletinBoardServiceTest {

    @Autowired BulletinBoardService bulletinBoardService;
    @Autowired BulletinBoardRepository bulletinBoardRepository;
    @Autowired UserService userService;

    @Test
    void createBoard() {
        //given
        SaveUserForm userForm = getUserForm("nickName");
        Long userId = userService.join(userForm);

        SaveBulletinBoardForm boardForm = getBoardForm("title");

        //when
        Long boardId = bulletinBoardService.createBoard(boardForm, userForm.getNickName());
        BulletinBoard findBoard = bulletinBoardRepository.findById(boardId);

        //then
        assertThat(findBoard.getTitle()).isEqualTo(boardForm.getTitle());
        assertThat(findBoard.getUser().getId()).isEqualTo(userId);
    }

    @Test
    void readBoards() {
        //given
        SaveUserForm userForm = getUserForm("nickName");
        userService.join(userForm);
        Long firstBoardId = bulletinBoardService.createBoard(getBoardForm("firstTitle"), "nickName");
        Long secondBoardId = bulletinBoardService.createBoard(getBoardForm("secondTitle"), "nickName");

        //when
        ReadBulletinBoardForm firstBoardDto = bulletinBoardService.readBoard(firstBoardId);
        // TODO: 2023-03-27   pageRead 추가

        //then
        assertThat(firstBoardDto.getTitle()).isEqualTo("firstTitle");
        assertThat(firstBoardDto.getReadUserForm().getNickName()).isEqualTo("nickName");
    }

    @Test
    void getUpdateFormAndUpdateBoard() {
        //given
        SaveUserForm userForm = getUserForm("nickName");
        Long userId = userService.join(userForm);

        SaveBulletinBoardForm boardForm = getBoardForm("title");
        Long boardId = bulletinBoardService.createBoard(boardForm, userForm.getNickName());

        //when
        UpdateBulletinBoardForm updateForm = bulletinBoardService.getUpdateForm(boardId);
        update(updateForm);
        Long updateBoardId = bulletinBoardService.updateBoard(updateForm);
        BulletinBoard updateBoard = bulletinBoardRepository.findById(updateBoardId);

        //then
        assertThat(updateForm.getId()).isEqualTo(boardId);

        assertThat(updateBoardId).isEqualTo(boardId);
        assertThat(updateBoard.getTitle()).isEqualTo("updateTitle");
        assertThat(updateBoard.getContent()).isEqualTo("updateContent");
        assertThat(updateBoard.getRegion()).isEqualTo("updateRegion");
        assertThat(updateBoard.getImages()).isEqualTo("updateImage");
    }




    /**============================= private method ==============================*/

    private SaveUserForm getUserForm(String nickName) {
        SaveUserForm form = new SaveUserForm();
        form.setEmailId("emailId");
        form.setPassword("password");
        form.setNickName(nickName);
        form.setName("name");
        form.setAge(20);
        form.setGender(Gender.MAN);
        return form;
    }

    private static SaveBulletinBoardForm getBoardForm(String title) {
        SaveBulletinBoardForm boardForm = new SaveBulletinBoardForm();
        boardForm.setTitle(title);
        boardForm.setContent("content");
        boardForm.setRegion("region");
//        boardForm.setImages("image");
        return boardForm;
    }

    private static void update(UpdateBulletinBoardForm updateForm) {
        updateForm.setTitle("updateTitle");
        updateForm.setContent("updateContent");
        updateForm.setRegion("updateRegion");
//        updateForm.setImage("updateImage");
    }
}