package com.catdog.help.web.controller;

import com.catdog.help.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ViewUpdater {

    private final ViewService viewService;

    public void addView(Long id, HttpServletRequest request, HttpServletResponse response) {
        //start views using cookie
        if (request.getCookies() != null) {
            //로그인 사용자
            Cookie viewCookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("view"))
                    .findAny()
                    .orElse(null);
            if (viewCookie != null) {
                //조회한 게시글 이미 존재, 해당 게시글 조회 여부 확인
                if (!viewCookie.getValue().contains(String.valueOf(id))) {
                    //처음 조회한 게시글
                    viewService.addViews(id);
                    viewCookie.setValue(viewCookie.getValue() + "_" + String.valueOf(id));
                    viewCookie.setMaxAge(60 * 60 * 12);
                    response.addCookie(viewCookie);
                } else {
                    //이미 조회 한 게시글
                }
            } else {
                //조회한 게시글이 없어 view cookie 가 없는 경우
                viewService.addViews(id);
                Cookie newViewCookie = new Cookie("view", String.valueOf(id));
                newViewCookie.setMaxAge(60 * 60 * 12);
                response.addCookie(newViewCookie);
            }
        } else {
            // TODO: 2023-03-18 비회원 사용자
        }
        //end views using cookie
    }
}
