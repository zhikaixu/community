package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

// 都是Spring MVC的注解
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    // controller依赖于service
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    // url: localhost:8080/community/alpha/hello
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    // 没有返回对象的原因：通过response对象可以直接向浏览器提供任何数据
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据 发现用底层的response的输出流很麻烦，这种方法不使用
        // 请求行数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        // 请求header数据
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        // 请求body（参数）数据 假设传入了参数名为code的参数
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        // 使用底层封装的输出流
        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("finalized");
        }
        // 发现用底层的response的输出流很麻烦
    }

    // 常用方法
    // GET请求
    // /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody // 现在要获得path中请求的变量
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // Post请求
    // get请求也能传参，但是url的长度往往是有限制的，当有很多参数时，应该使用post来传参
    // 需要使用一个模版网页来上传数据
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    // 参数的名字要和表单中的name一致
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应动态的html数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    // 不加@ResponseBody 默认返回HTML数据
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", "30");
        mav.setViewName("/demo/view"); // 要在templates下创建demo包，在demo包内创建view.html
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        // DispatchServlet在调用这个方法时会发现Model类的参数，会自动地实例化成bean，
        // 把model数据装入model这个bean中，然后返回给模版引擎view
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    // 响应JSON数据（异步请求）当前网页不刷新，但访问了服务器，进行了判断
    //  Java对象 -> JSON字符串 -> JS对象  跨语言转换利用JSON
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody // 代表返回JSON字符串；如果不加就会返回HTML文件
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody // 代表返回JSON字符串；如果不加就会返回HTML文件
    public List<Map<String, Object>> getEmpList() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(emp);
        emp = new HashMap<>();
        emp.put("name", "李三");
        emp.put("age", 25);
        emp.put("salary", 10000.00);
        list.add(emp);
        return list;
    }

    // cookie实例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID()); // 必须要传入参数
        // cookie生效范围（浏览器返回cookie时在服务器的哪些路径下有效）
        cookie.setPath("/community/alpha");
        // 设置cookie的生效时间
        cookie.setMaxAge(60 * 10); // 10min
        // 发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) { // 得到某个key对应的某个cookie用这个注解
        System.out.println(code);
        return "get cookie";
    }

    // session实例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) { // 与cookie不同，只要声明session，springMVC会自动创建注入session
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // ajax实例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody // 异步请求向浏览器返回字符串
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "操作成功！");
    }
}
