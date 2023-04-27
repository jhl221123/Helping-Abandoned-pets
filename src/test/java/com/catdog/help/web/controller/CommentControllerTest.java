package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.EditCommentForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private static final String BOARD_ID = "boardId";
    private static final String CONTENT = "content";
    private static final String CLICK_REPLY = "clickReply";
    private static final String PARENT_ID = "parentId";

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private BoardService boardService;

    private MockMvc mockMvc;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }


    @Test
    @DisplayName("부모 댓글 저장 성공")
    void saveParentComment() throws Exception {
        //given
        doReturn(3L).when(commentService)
                .save(any(CommentForm.class), eq(-1L));

        doReturn(true).when(boardService)
                .isBulletin(2L);

        //expected
        mockMvc.perform(post("/comments/parent")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(BOARD_ID, String.valueOf(2L))
                        .param(CONTENT, "댓글내용")
                )
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("자식 댓글 양식 호출 성공")
    void getChildForm() throws Exception {
        //given
        doReturn(true).when(boardService)
                .isBulletin(2L);

        //expected
        mockMvc.perform(get("/comments/child")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(BOARD_ID, String.valueOf(2L))
                        .param(CLICK_REPLY, String.valueOf(3L))
                )
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("자식 댓글 저장 성공")
    void saveChildComment() throws Exception {
        //given
        doReturn(true).when(boardService)
                .isBulletin(2L);

        doReturn(4L).when(commentService)
                .save(any(CommentForm.class), eq(3L));

        //expected
        mockMvc.perform(post("/comments/child")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(BOARD_ID, String.valueOf(2L))
                        .param(PARENT_ID, String.valueOf(3L))
                        .param(CONTENT, "댓글내용")
                )
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("검증으로 인해 댓글 저장 실패")
    void failSaveByValidate() throws Exception {
        //given
        doReturn(true).when(boardService)
                .isBulletin(2L);

        //expected
        mockMvc.perform(post("/comments/child")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(BOARD_ID, String.valueOf(2L))
                        .param(PARENT_ID, String.valueOf(3L))
                        .param(CONTENT, "")
                )
                .andExpect(flash().attributeExists("bindingResult"))
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("댓글 수정 양식 호출 성공")
    void getEditForm() throws Exception {
        //given
        doReturn("닉네임").when(commentService)
                .getWriter(3L);

        doReturn(true).when(boardService)
                .isBulletin(2L);

        //expected
        mockMvc.perform(get("/comments/{id}/edit", 3L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(BOARD_ID, String.valueOf(2L))
                )
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void editComment() throws Exception {
        //given
        doReturn("닉네임").when(commentService)
                .getWriter(3L);

        doNothing().when(commentService)
                        .update(any(EditCommentForm.class));

        doReturn(true).when(boardService)
                .isBulletin(2L);

        //expected
        mockMvc.perform(post("/comments/{id}/edit", 3L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(BOARD_ID, String.valueOf(2L))
                        .param(CONTENT, "댓글내용")
                )
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("검증으로 인해 댓글 수정 실패")
    void failEditByValidate() throws Exception {
        //given
        doReturn("닉네임").when(commentService)
                .getWriter(3L);

        doReturn(true).when(boardService)
                .isBulletin(2L);

        //expected
        mockMvc.perform(post("/comments/{id}/edit", 3L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(BOARD_ID, String.valueOf(2L))
                        .param(CONTENT, "")
                )
                .andExpect(flash().attributeExists("bindingResult"))
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment() throws Exception {
        //given
        doReturn("닉네임").when(commentService)
                .getWriter(3L);

        doNothing().when(commentService)
                .delete(3L);

        doReturn(true).when(boardService)
                .isBulletin(2L);

        //expected
        mockMvc.perform(get("/comments/{id}/delete", 3L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(BOARD_ID, String.valueOf(2L))
                )
                .andExpect(redirectedUrl("/boards/" + 2L));
    }

    @Test
    @DisplayName("작성자 외 접근으로 요청 실패")
    void failRequestByWriterMismatch() throws Exception {
        //given
        doReturn("닉네임").when(commentService)
                .getWriter(3L);

        //expected
        mockMvc.perform(get("/comments/{id}/delete", 3L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "다른사용자")
                        .param(BOARD_ID, String.valueOf(2L))
                )
                .andExpect(redirectedUrl("/"));
    }
}