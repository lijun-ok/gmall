package com.atguigu.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.manager.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.manager.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;

@Service
public class AttrServiceImpl implements AttrService {
        @Autowired
       private BaseAttrInfoMapper baseAttrInfoMapper;
        @Autowired
        private BaseAttrValueMapper baseAttrValueMapper;
    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);
        return baseAttrInfos;
    }

    @Override
    public void saveAttr(BaseAttrInfo baseAttrInfo) {
           baseAttrInfoMapper.insertSelective(baseAttrInfo);
           List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
           for (BaseAttrValue baseAttrValue : attrValueList) {
               baseAttrValue.setAttrId(baseAttrInfo.getId());
               baseAttrValueMapper.insert(baseAttrValue);
       }
    }

    @Override
    public void deleteAttr(String attrInfoId) {
        BaseAttrValue baseAttrValue=new BaseAttrValue();
        baseAttrValue.setAttrId(attrInfoId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        for (BaseAttrValue attrValue : baseAttrValues) {
            baseAttrValueMapper.delete(attrValue);
        }
        baseAttrInfoMapper.deleteByPrimaryKey(attrInfoId);
    }

    @Override
    public List<BaseAttrValue> editAttr(String attrInfoId) {
        BaseAttrValue baseAttrValue=new BaseAttrValue();
        baseAttrValue.setAttrId(attrInfoId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        for (BaseAttrValue attrValue : baseAttrValues) {
        }
        return  baseAttrValues;
    }

    @Override
    public List<BaseAttrInfo> getAttrListByCtg3Id(String catalog3Id) {
        BaseAttrInfo baseAttrInfo=new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.select(baseAttrInfo);
        for (BaseAttrInfo attrInfo : baseAttrInfos) {
            String attrId= attrInfo.getId();
            BaseAttrValue baseAttrValue=new BaseAttrValue();
            baseAttrValue.setAttrId(attrId);
            List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);

            attrInfo.setAttrValueList(baseAttrValues);
        }
        return baseAttrInfos;
    }

    @Override
    public List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds) {
        String join = StringUtils.join(valueIds, ",");
        List<BaseAttrInfo> baseAttrInfos=baseAttrValueMapper.selectAttrListByValueIds(join);
        return baseAttrInfos;
    }
}
