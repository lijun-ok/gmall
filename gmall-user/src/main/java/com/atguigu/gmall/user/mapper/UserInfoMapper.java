package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.bean.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserInfoMapper {
        List<UserInfo> selectAll();
}
