package com.catdog.help.service;

import com.catdog.help.TestData;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
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
    @Autowired JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired UserService userService;
    @Autowired TestData testData;


    @Test
    void createBoard() {
        //given
        SaveUserForm userForm = testData.getSaveUserForm("nickName", "password", "nickname");
        Long userId = userService.join(userForm);

        SaveBulletinBoardForm boardForm = testData.getSaveBulletinBoardForm("title");

        //when
        Long boardId = bulletinBoardService.createBoard(boardForm, userForm.getNickname());
        BulletinBoard findBoard = jpaBulletinBoardRepository.findById(boardId);

        //then
        assertThat(findBoard.getTitle()).isEqualTo(boardForm.getTitle());
        assertThat(findBoard.getUser().getId()).isEqualTo(userId);
    }

    @Test
    void readBoards() {
        //given
        SaveUserForm userForm = testData.getSaveUserForm("nickName", "password", "nickname");
        userService.join(userForm);
        Long firstBoardId = bulletinBoardService.createBoard(testData.getSaveBulletinBoardForm("firstTitle"), "nickName");
        Long secondBoardId = bulletinBoardService.createBoard(testData.getSaveBulletinBoardForm("secondTitle"), "nickName");

        //when
        ReadBulletinBoardForm findForm = bulletinBoardService.readBoard(firstBoardId);
        // TODO: 2023-03-27   pageRead 추가

        //then
        assertThat(findForm.getTitle()).isEqualTo("firstTitle");
        assertThat(findForm.getNickname()).isEqualTo("nickName");
    }

    @Test
    void getUpdateFormAndUpdateBoard() {
        //given
        SaveUserForm userForm = testData.getSaveUserForm("nickName", "password", "nickname");
        Long userId = userService.join(userForm);

        SaveBulletinBoardForm boardForm = testData.getSaveBulletinBoardForm("secondTitle");
        Long boardId = bulletinBoardService.createBoard(boardForm, userForm.getNickname());

        //when
        UpdateBulletinBoardForm updateForm = bulletinBoardService.getUpdateForm(boardId);
        updateForm.setTitle("updateTitle");
        updateForm.setContent("updateContent");
        updateForm.setRegion("updateRegion");
        Long updateBoardId = bulletinBoardService.updateBoard(updateForm);
        BulletinBoard updateBoard = jpaBulletinBoardRepository.findById(updateBoardId);

        //then
        assertThat(updateForm.getId()).isEqualTo(boardId);

        assertThat(updateBoardId).isEqualTo(boardId);
        assertThat(updateBoard.getTitle()).isEqualTo("updateTitle");
        assertThat(updateBoard.getContent()).isEqualTo("updateContent");
        assertThat(updateBoard.getRegion()).isEqualTo("updateRegion");
    }
}