package com.catdog.help.service;

import com.catdog.help.domain.Board.BulletinBoard;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.SaveBulletinBoardForm;
import com.catdog.help.web.form.SaveUserForm;
import com.catdog.help.web.form.UpdateBulletinBoardForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        BulletinBoard findBoard = bulletinBoardRepository.findOne(boardId);

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
        BulletinBoardDto firstBoardDto = bulletinBoardService.readBoard(firstBoardId);
        List<BulletinBoardDto> boardDtos = bulletinBoardService.readAll();

        //then
        assertThat(firstBoardDto.getTitle()).isEqualTo("firstTitle");
        assertThat(firstBoardDto.getUser().getNickName()).isEqualTo("nickName");
        assertThat(boardDtos.size()).isEqualTo(2);
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
        BulletinBoard updateBoard = bulletinBoardRepository.findOne(updateBoardId);

        //then
        assertThat(updateForm.getId()).isEqualTo(boardId);

        assertThat(updateBoardId).isEqualTo(boardId);
        assertThat(updateBoard.getTitle()).isEqualTo("updateTitle");
        assertThat(updateBoard.getContent()).isEqualTo("updateContent");
        assertThat(updateBoard.getRegion()).isEqualTo("updateRegion");
        assertThat(updateBoard.getImage()).isEqualTo("updateImage");
    }


    /**============================= private method ==============================*/

    private SaveUserForm getUserForm(String nickName) {
        SaveUserForm form = new SaveUserForm();
        form.setEmailId("emailId");
        form.setPassword("password");
        form.setNickName(nickName);
        form.setName("name");
        form.setAge(20);
        form.setGender("man");
        return form;
    }

    private static SaveBulletinBoardForm getBoardForm(String title) {
        SaveBulletinBoardForm boardForm = new SaveBulletinBoardForm();
        boardForm.setTitle(title);
        boardForm.setContent("content");
        boardForm.setRegion("region");
        boardForm.setImage("image");
        return boardForm;
    }

    private static void update(UpdateBulletinBoardForm updateForm) {
        updateForm.setTitle("updateTitle");
        updateForm.setContent("updateContent");
        updateForm.setRegion("updateRegion");
        updateForm.setImage("updateImage");
    }
}