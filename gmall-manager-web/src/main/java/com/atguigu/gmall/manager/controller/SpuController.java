package com.atguigu.gmall.manager.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.manager.util.MyUplaodUtil;
import com.atguigu.gmall.service.SpuServcie;
import org.csource.fastdfs.ClientGlobal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class SpuController {

    @Reference
    SpuServcie spuServcie;

    @RequestMapping("getSpuImageListBySpuId")
    @ResponseBody
    public List<SpuImage> getSpuImageListBySpuId(String spuId){
        List<SpuImage> spuImages=spuServcie.getSpuImageListBySpuId(spuId);
        return spuImages;
    }

    @RequestMapping("getSaleAttrListBySpuId")
    @ResponseBody
    public List<SpuSaleAttr> getSaleAttrListBySpuId(String spuId){
        List<SpuSaleAttr> spuList=spuServcie.getSaleAttrListBySpuId(spuId);
        return spuList;
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file){
        System.out.println("进入了上传文件方法!");
        String imgUrl = MyUplaodUtil.uploadImage(file);
        return imgUrl;
    }

    /**
     * 添加spu
     * @param
     * @return
     */
    @RequestMapping("deleteSpu")
    @ResponseBody
    public String deleteSpu(String  spuId){
        String result=spuServcie.deleteSpu(spuId);
        return result;
    }
    /**
     * 添加spu
     * @param
     * @return
     */
    @RequestMapping("saveSpu")
    @ResponseBody
    public String saveSpu(SpuInfo spuInfo){
       String result=spuServcie.saveSpu(spuInfo);
        return result;
    }

    /**
     * 查询销售属性列表
     * @param
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrs = spuServcie.baseSaleAttrList();
        return baseSaleAttrs;
    }

    /**
     * 查询spu列表
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> spuList(String catalog3Id){
        List<SpuInfo> spuInfos = spuServcie.spuList(catalog3Id);
        return spuInfos;
    }
}
