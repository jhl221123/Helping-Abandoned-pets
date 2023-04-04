package com.catdog.help.web.interceptor;

import com.catdog.help.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String param = request.getParameter("page");

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(SessionConst.LOGIN_USER) == null) {
            if (param != null) {
                response.sendRedirect("/users/login?redirectURL=" + requestURI + "?page=" + param);
            } else {
                response.sendRedirect("/users/login?redirectURL=" + requestURI);
            } 
            return false;
        }

        return true;
    }
}
