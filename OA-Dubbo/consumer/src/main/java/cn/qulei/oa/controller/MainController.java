package cn.qulei.oa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    //任何的游客访问主页
    @RequestMapping("/")
    public String index(){

        return "index";
    }
//登录成功返回到主页
    @RequestMapping("index")
    public String index1(){
        return "index";
    }
}
