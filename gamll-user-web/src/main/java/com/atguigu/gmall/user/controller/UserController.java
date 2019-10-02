package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
          /*  @Autowired*/
            @Reference
            UserService userService;

            @RequestMapping("/userInfoList")
            @ResponseBody
            public List<UserInfo> userInfoList(){
                System.out.println("调用了controller方法");
                List<UserInfo> userInfoList=userService.userInfoList();
                return userInfoList;
            }
}
