package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParam;
import com.atguigu.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;
    @Override
    public List<SkuLsInfo> search(SkuLsParam skuLsParam) {
        List<SkuLsInfo> skuLsInfos=new ArrayList<>();
        Search search= new Search.Builder(getMyDsl(skuLsParam)).addIndex("gmall0328").addType("SkuLsInfo").build();
        try {
            SearchResult execute = jestClient.execute(search);

            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if(highlight!=null&&highlight.size()>0){
                List<String> skuName = highlight.get("skuName");
                   String s = skuName.get(0);
                   source.setSkuName(s);
               }
                skuLsInfos.add(source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skuLsInfos;
    }
    public static String getMyDsl(SkuLsParam skuLsParam){
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();
        String catalog3Id = skuLsParam.getCatalog3Id();

        //创建一个dsl工具对象
        SearchSourceBuilder dsl=new SearchSourceBuilder();
        //创建一个先过滤后搜索的query对象
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

        if (StringUtils.isNotBlank(catalog3Id)){
            TermsQueryBuilder t= new TermsQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(t);
        }

        //query对象过滤语句
        if(null!=valueId&&valueId.length>0){
            for (int i = 0; i < valueId.length; i++) {
                TermsQueryBuilder t= new TermsQueryBuilder("skuAttrValueList.valueId",valueId[i]);
                boolQueryBuilder.filter(t);
            }

        }
        if(StringUtils.isNotBlank(keyword)){
             MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName",keyword);
             boolQueryBuilder.must(matchQueryBuilder);
        }

        dsl.query(boolQueryBuilder);
        dsl.size(100);
        dsl.from(0);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
        highlightBuilder.postTags("</span>");
        dsl.highlight(highlightBuilder);

        return dsl.toString();
    }
}
