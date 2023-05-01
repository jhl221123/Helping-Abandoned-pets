package com.catdog.help.web.controller;

import com.catdog.help.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ViewUpdater {

    private final ViewService viewService;

    public void addView(Long id, HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {// TODO: 2023-04-30 로그인, 비로그인 사용자를 진짜 놔눠야 할까?
            //로그인 사용자
            Optional<Cookie> target = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("view"))
                    .findAny();

            if (!target.isEmpty()) {
                Cookie viewCookie = target.get();
                if (!viewCookie.getValue().contains(String.valueOf(id))) {
                    //처음 조회한 게시글
                    viewService.addViews(id);
                    viewCookie.setValue(viewCookie.getValue() + "_" + id);
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
