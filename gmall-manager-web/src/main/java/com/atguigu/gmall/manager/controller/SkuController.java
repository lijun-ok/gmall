package com.atguigu.gmall.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuServcie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SkuController {

    @Reference
    SkuService skuService;


    @RequestMapping("saveSku")
    @ResponseBody
    public String  saveSku(SkuInfo skuInfo){
       /* List<SkuInfo> spuList=spuInfoService.getSkuListBySpu(spuId);*/
        skuService.saveSku(skuInfo);
        return "success";
    }

    @RequestMapping("getSkuListBySpu")
    @ResponseBody
    public List<SkuInfo> getSkuListBySpu(String spuId){
        List<SkuInfo> spuList=skuService.getSkuListBySpu(spuId);
        return spuList;
    }

}
