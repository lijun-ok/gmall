package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.List;

@Service   //此service不是spring的service  是dubbo的serviceS
public class UserServiceImpl implements UserService {
    @Autowired
   private UserInfoMapper userInfoMapper;
    @Override
    public List<UserInfo> userInfoList() {
        System.out.println("sevice方法");
        return userInfoMapper.selectAll();
    }
}
