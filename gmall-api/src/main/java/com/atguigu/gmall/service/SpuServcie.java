package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface SpuServcie {
    List<SpuInfo> spuList(String catalog3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    String saveSpu(SpuInfo spuInfo);

    String deleteSpu(String spuId);

    List<SpuSaleAttr> getSaleAttrListBySpuId(String spuId);

    List<SpuImage> getSpuImageListBySpuId(String spuId);

    List<SkuInfo> getSkuSaleAttrValueListBySpu(String spuId);
}
