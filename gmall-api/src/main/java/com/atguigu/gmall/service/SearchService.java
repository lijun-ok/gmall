package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParam;

import java.util.List;

public interface SearchService {

    List<SkuLsInfo> search(SkuLsParam skuLsParam);
}
