package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
        @Autowired
        CartInfoMapper cartInfoMapper;
        @Autowired
         RedisUtil redisUtil;
    @Override
    public CartInfo ifCartExists(CartInfo cartInfo) {
        CartInfo cartInfo1 = new CartInfo();
        cartInfo1.setUserId(cartInfo.getUserId());
        cartInfo1.setSkuId(cartInfo.getSkuId());
        CartInfo result= cartInfoMapper.selectOne(cartInfo1);
        return result;
    }

    @Override
    public void updateCart(CartInfo cartInfoDB) {
        cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
    }

    @Override
    public void saveCart(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }

    @Override
    public void syncCache(String userId) {
        Jedis jedis = redisUtil.getJedis();
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos = cartInfoMapper.select(cartInfo);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        for (CartInfo info : cartInfos) {
            stringStringHashMap.put(info.getId(), JSON.toJSONString(info));
        }
        jedis.hmset("carts:"+userId+":info",stringStringHashMap);
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartCache(String userId) {
        List<CartInfo> cartInfos=new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");//取出该用户所有在缓存中的购物车商品
      if(hvals!=null&&hvals.size()>0){
          for (String hval : hvals) {
              //将字符串转换为对象
              CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
              cartInfos.add(cartInfo);
          }
      }
        return cartInfos;
    }

    @Override
    public void updateCartChecked(CartInfo cartInfo) {
        Example example=new Example(CartInfo.class);
        example.createCriteria().andEqualTo("skuId",cartInfo.getSkuId()).andEqualTo("userId",cartInfo.getUserId());
        /**
         * updateByExampleSelective(s,w):第一个参数为更新的数据。第二个参数为更新数据的条件
         */
        cartInfoMapper.updateByExampleSelective(cartInfo,example);
        syncCache(cartInfo.getUserId());
    }
}
