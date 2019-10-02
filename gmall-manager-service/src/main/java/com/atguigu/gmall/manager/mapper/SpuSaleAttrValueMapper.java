package com.atguigu.gmall.manager.mapper;

import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrValueMapper extends Mapper<SpuSaleAttrValue> {
   List<SkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}
