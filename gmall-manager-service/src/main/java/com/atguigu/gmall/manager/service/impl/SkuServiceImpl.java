package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manager.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.manager.mapper.SkuImageMapper;
import com.atguigu.gmall.manager.mapper.SkuInfoMapper;
import com.atguigu.gmall.manager.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public List<SkuInfo> getSkuListBySpu(String spuId) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setSpuId(spuId);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);
        return skuInfos;
    }

    @Override
    public void saveSku(SkuInfo skuInfo) {

        skuInfoMapper.insertSelective(skuInfo);
        String skuId = skuInfo.getId();

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();

        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
    }

    @Override
    public SkuInfo getSkuById(String skuId) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
        } catch (Exception e) {
           return null;
        }
        SkuInfo skuInfo=null;
        //查询Redis
        String key="sku:"+skuId+":info";
        String val= jedis.get(key);

        if("empty".equals(val)){
                System.out.println(Thread.currentThread().getName()+"发现数据库中暂时没有该商品，直接返回空对象");
            return skuInfo;
        }


        if(StringUtils.isBlank(val)){
            System.out.println(Thread.currentThread().getName()+"发现缓存中没有数据，申请分布式锁");
            //申请缓存锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 5000);
            if("OK".equals(OK)){  //拿到缓存锁
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+"获得分布式锁,开始访问数据");
                //查询DB
                skuInfo=getSkuByIdFormDB(skuId);
               if(skuInfo!=null){
                   System.out.println(Thread.currentThread().getName()+"通过分布式锁，查询到数据，同步缓存，然后归还锁");
                   //同步缓存
                   jedis.set(key,JSON.toJSONString(skuInfo));
                   //归还缓存锁
               }else{
                   System.out.println(Thread.currentThread().getName()+"通过分布式锁，没有查询到数据，通知同伴在10秒内不要访问该sku");
                   //通知同伴
                   jedis.setex("sku:" + skuId + ":info",10,"empty");
               }
                System.out.println(Thread.currentThread().getName()+"归还分布式锁");
               jedis.del("sku:" + skuId + ":lock");
            }else{  //没有拿到缓存锁
                System.out.println(Thread.currentThread().getName()+"分布式锁被占用，开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //自旋
                getSkuById(skuId);
            }

        }else{
            //正常转换缓存数据
            System.out.println(Thread.currentThread().getName()+"正常从缓存中取得数据,返回结果");
            skuInfo = JSON.parseObject(val, SkuInfo.class);
        }
        return skuInfo;
    }

    private SkuInfo getSkuByIdFormDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        //查询该sku对应的图片列表
        SkuImage skuImage=new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> images = skuImageMapper.select(skuImage);
        //查询该sku对应的销售属性
        SkuAttrValue skuAttrValue=new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> attrValues = skuAttrValueMapper.select(skuAttrValue);
        //查询该sku对应的销售属性值列表
        SkuSaleAttrValue skuSaleAttrValue=new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        //将查询到的三个列表值添加到skuinfo
        skuInfo.setSkuImageList(images);
        skuInfo.setSkuAttrValueList(attrValues);
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValues);
        return  skuInfo;
    }

    @Override
    public List<SkuInfo> getSkuListByCatalog3Id(String catalog3Id) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfos =skuInfoMapper.select(skuInfo);
        for (SkuInfo info : skuInfos) {
            String id = info.getId();
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(id);
            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
            info.setSkuAttrValueList(skuAttrValues);
        }
        return skuInfos;
    }
}
