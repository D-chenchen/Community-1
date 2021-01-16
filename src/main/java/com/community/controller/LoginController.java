package com.community.controller;


import com.community.entity.User;
import com.community.service.UserService;
import com.community.utils.Constant;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 用户登录注册 前端控制器
 */
@Controller
public class LoginController {

    private final UserService userService;
    private final Producer kaptchaProducer;

    public LoginController(UserService userService, Producer kaptchaProducer) {
        this.userService = userService;
        this.kaptchaProducer = kaptchaProducer;
    }

    /**
     * 跳转到用户登录页面
     */
    @GetMapping("/login")
    public String toLogin() {
        return "site/login";
    }

    /**
     * 跳转到用户注册界面
     */
    @GetMapping("/register")
    public String toRegister() {
        return "site/register";
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        // map 为空则注册成功
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，请到邮箱激活该账号!");
            model.addAttribute("target", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("UsernameMessage", map.get("UsernameMessage"));
            model.addAttribute("PasswordMessage", map.get("PasswordMessage"));
            model.addAttribute("EmailMessage", map.get("EmailMessage"));
            return "site/register";
        }
    }

    /**
     * 邮箱激活账号
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        // 根据用户 id 查询该用户的状态
        int result = userService.activation(userId, code);
        if (result == Constant.ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，你的账号可以正常使用!");
            model.addAttribute("target", "/login");
        } else if (result == Constant.ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，该账号激活码无效!");
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }

    /**
     * 获取验证码
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入 session
        session.setAttribute("kaptcha", text);
        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            // 该流不用关闭，Spring 会帮我们关
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response) {
        // 判断验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("CodeMessage", "验证码错误!");
            return "site/login";
        }

        // 检查账号和密码
        int expired = rememberMe ? Constant.REMEMBERME_EXPIRED_SECONDS : Constant.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> result = userService.login(username, password, expired);
        if (result.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", result.get("ticket").toString());
            cookie.setPath("/");
            cookie.setMaxAge(expired);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("UsernameMessage", result.get("UsernameMessage"));
            model.addAttribute("PasswordMessage", result.get("PasswordMessage"));
            return "site/login";
        }
    }

    /**
     * 用户退出
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

}