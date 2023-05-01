package com.catdog.help.web.controller;

import com.catdog.help.service.ViewService;
import com.catdog.help.web.SessionConst;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewUpdaterTest {

    @InjectMocks
    ViewUpdater viewUpdater;

    @Mock
    ViewService viewService;


    @Test
    @DisplayName("해당 글을 처음 조회하는 경우 조회수가 올라가고 쿠키에 글 id를 추가한다.")
    void existViewAndFirstRead() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        setViewCookie(request);

        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(viewService)
                .addViews(3L);

        //when
        viewUpdater.addView(3L, request, response);

        //then
        Cookie respCookie = Arrays.stream(response.getCookies())
                .filter(c -> c.getName().equals("view"))
                .findAny().get();

        assertThat(respCookie.getValue()).isEqualTo("2_3");
    }

    @Test
    @DisplayName("이미 해당 글을 조회한 경우, 조회수는 올라가지 않는다.")
    void existViewAndNotFirstRead() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        setViewCookie(request);

        MockHttpServletResponse response = new MockHttpServletResponse();

        //expected
        viewUpdater.addView(2L, request, response);
        verify(viewService, times(0)).addViews(2L);
    }

    @Test
    @DisplayName("view 쿠키가 없는 경우, 조회수를 증가시키고 쿠키 생성 후 글 id를 추가한다.")
    void notExistView() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        setLoginCookie(request);

        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(viewService)
                .addViews(2L);

        //when
        viewUpdater.addView(2L, request, response);

        //then
        Cookie respCookie = Arrays.stream(response.getCookies())
                .filter(c -> c.getName().equals("view"))
                .findAny().get();

        assertThat(respCookie.getValue()).isEqualTo("2");
    }


    private void setLoginCookie(MockHttpServletRequest request) {
        Cookie[] cookies = new Cookie[]{
                new Cookie(SessionConst.LOGIN_USER, "닉네임")
        };
        request.setCookies(cookies);
    }

    private void setViewCookie(MockHttpServletRequest request) {
        setLoginCookie(request);
        Cookie[] cookies = new Cookie[]{
                new Cookie("view", "2")
        };
        request.setCookies(cookies);
    }
}