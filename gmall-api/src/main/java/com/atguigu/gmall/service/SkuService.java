package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuInfo;

import java.util.List;

public interface SkuService {
    List<SkuInfo> getSkuListBySpu(String spuId);

    void saveSku(SkuInfo skuInfo);

    SkuInfo getSkuById(String skuId);

    List<SkuInfo> getSkuListByCatalog3Id(String s);
}
