package com.community.interceptor;

import com.community.entity.LoginTicket;
import com.community.entity.User;
import com.community.service.UserService;
import com.community.utils.CookieUtil;
import com.community.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private UserThreadLocal userThreadLocal;

    /**
     * 在 controller 之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 cookie 中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        // 判断 cookie 中是否有数据
        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.selectByTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.selectById(loginTicket.getUserId());
                // 存入 ThreadLocal 中
                userThreadLocal.setUsers(user);
            }
        }
        // 证明用户已登录，予以放行
        return true;
    }

    /**
     * 在 controller 之后，模板引擎之前执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 获取登录的用户，存到 modelAndView 里，在模板引擎中使用
        User user = userThreadLocal.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在模板引擎之后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理登录用户凭证
        userThreadLocal.clear();
    }
}
