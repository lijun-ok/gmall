package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.bean.SpuSaleAttrValue;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuServcie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    SpuServcie spuServcie;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId,ModelMap modelMap){

        SkuInfo skuInfo=skuService.getSkuById(skuId);

        modelMap.put("skuInfo",skuInfo);
        String spuId = skuInfo.getSpuId();
        //spu的sku和销售属性对应关系的hash表
        List<SkuInfo> skuInfos=spuServcie.getSkuSaleAttrValueListBySpu(spuId);

        Map<String, String> stringStringHashMap = new HashMap<>();

        for (SkuInfo info : skuInfos) {
            String v = info.getId();
            String k="";
            List<SkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                    k=k+"|"+skuSaleAttrValue.getSaleAttrValueId();
            }
            stringStringHashMap.put(k,v);
        }

        String  skuJson = JSON.toJSONString(stringStringHashMap);

        modelMap.put("skuJson",skuJson);

        //当前sku所包含的销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
       
        //spu的销售属性列表
        List<SpuSaleAttr> saleAttrListBySpuId = spuServcie.getSaleAttrListBySpuId(spuId);
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            for (SpuSaleAttr spuSaleAttr : saleAttrListBySpuId) {
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                    if(skuSaleAttrValue.getSaleAttrValueId().equals(spuSaleAttrValue.getId())){
                        spuSaleAttrValue.setIsChecked("1");
                    }
                }
            }
        }
        modelMap.put("spuSaleAttrListCheckBySku",saleAttrListBySpuId);
        return "item";
    }
}
