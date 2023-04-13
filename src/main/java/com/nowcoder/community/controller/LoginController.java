package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.nowcoder.community.entity.User;
import org.springframework.ui.Model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    // 访问注册页的方法
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    // 访问登录页的方法
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    // 向服务器提交数据
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result"; // 将model的属性返回到跳转页面html文件,还要去设置html文件
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 激活邮件
    // http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经能够正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    // 浏览器进入login，向服务器请求login页面，返回之后，再向服务器请求验证码，服务器再返回
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    // 服务器返回的不是字符串、网页，是图片，要手动用Response返回
    // 验证码不能存到浏览器端，会被盗取；要存在服务端
    public void getKaptcha(HttpServletResponse response /*, HttpSession session */) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        // session.setAttribute("kaptcha", text);

        // 验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给服务器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code,
                        boolean rememberme, Model model, /* HttpSession session, */
                        HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 检查验证码
        // String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        // 检查账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath); // 路径不要写死，用配置类参数
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login"; // 重定向默认到get方法页面
    }

    // 打开忘记密码页面
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage() {
        return "/site/forget";
    }

    // 点击获取验证码
    @RequestMapping(path = "/forget/getEmailCode", method = RequestMethod.GET)
    @ResponseBody
    public String getEmailCode(String email, HttpSession session) {
        Map<String, Object> map = userService.getEmailCode(email);
        if (map.containsKey("emailMsg")) {
            return CommunityUtil.getJSONString(1, (String) map.get("emailMsg"));
        } else {
            // captcha和expiredTime存入session
            String captcha = (String) map.get("captcha");
            Long expiredTime = (Long) map.get("expiredTime");
            session.setAttribute("captcha", captcha);
            session.setAttribute("expiredTime", expiredTime);
        }
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/forget", method = RequestMethod.POST)
    public String resetForgetPassword(String email, String captcha, String newPassword, HttpSession session, Model model) {
        Map<String, Object> map = userService.resetForgetPassword(email, captcha, newPassword);
        if (map.containsKey("captchaMsg")) {
            model.addAttribute("captchaMsg", map.get("captchaMsg"));
            return "/site/forget";
        }
        if (map.containsKey("newPasswordMsg")) {
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/forget";
        }
        Long expiredTime = (Long) session.getAttribute("expiredTime");
        Long currentTime = new Date(System.currentTimeMillis()).getTime();
        String realCaptcha = (String) session.getAttribute("captcha");
        String userCaptcha = (String) map.get("captcha");
        if (currentTime > expiredTime) {
            model.addAttribute("captchaMsg", "验证码超时，请重新获取！");
            return "/site/forget";
        }
        if (!userCaptcha.equalsIgnoreCase(realCaptcha)) {
            model.addAttribute("captchaMsg", "验证码错误！");
            return "/site/forget";
        } else {
            int userId = (int) map.get("userId");
            userService.updatePassword(userId, newPassword);
            model.addAttribute("msg", "密码修改成功！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        }
    }

}
