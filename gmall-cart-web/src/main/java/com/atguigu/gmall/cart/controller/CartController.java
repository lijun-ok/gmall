package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

        @Reference
     private SkuService skuService;
    @Reference
    private CartService cartService;

    /**
     * 订单系统中，必须登录才能访问的方法
     * @param cartInfo
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("toTrade")
    public String toTrade(Model model,HttpServletRequest request,HttpServletResponse response){
        String userId="";

        //需要被单点登录的拦截器
        return "toTrade";
    }

    @RequestMapping("checkCart")
    public String checkCart(CartInfo cartInfo,Model model,HttpServletRequest request,HttpServletResponse response){
        List<CartInfo> cartInfos=new ArrayList<>();
        String userId="2";
        //修改购物车的选中状态
      if(StringUtils.isNotBlank(userId)){
          //已登录  更新DB和缓存
          cartInfo.setUserId(userId);
          cartService.updateCartChecked(cartInfo);
          //取缓存中的数据
          cartInfos=cartService.getCartCache(userId);
      }else{
        //没有登录  更新cookie
          String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
          if(StringUtils.isNotBlank(cartListCookie)){
              cartInfos=JSON.parseArray(cartListCookie,CartInfo.class);
              for (CartInfo info : cartInfos) {
                  if(info.getSkuId().equals(cartInfo.getSkuId())){
                      info.setIsChecked(cartInfo.getIsChecked());
                  }
              }
          }
          CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);
      }

        model.addAttribute("cartList",cartInfos);
        model.addAttribute("totalPrice",getTotalPrice(cartInfos));
        return "cartListInner";
    }

        @RequestMapping("cartList")
        public String cartList(HttpServletRequest request, Model model) {
            List<CartInfo> cartInfos=new ArrayList<>();
            String userId="2";
            if(StringUtils.isBlank(userId)){
                //未登录:取cookie中的数据
                String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
                if(StringUtils.isNotBlank(cartListCookie)){
                    cartInfos=JSON.parseArray(cartListCookie,CartInfo.class);
                }
            }else{
                //取缓存中的数据
                cartInfos=cartService.getCartCache(userId);
            }
            model.addAttribute("cartList",cartInfos);
            model.addAttribute("totalPrice",getTotalPrice(cartInfos));
            return"cartList";
        }

    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {
            BigDecimal b=new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {
            if(cartInfo.getIsChecked().equals("1")) {
                b = b.add(cartInfo.getCartPrice());
            }
        }
        return b;
    }

    @RequestMapping("cartSuccess")
    public String cartSuccess(){

        return "success";
    }
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, CartInfo cartInfo){
        String id = cartInfo.getSkuId();
        SkuInfo sku= skuService.getSkuById(id);
        cartInfo.setCartPrice(sku.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setIsChecked("1");
        cartInfo.setImgUrl(sku.getSkuDefaultImg());
        cartInfo.setSkuPrice(sku.getPrice());
        cartInfo.setSkuName(sku.getSkuName());

        String userId="";
        List<CartInfo> cartInfos=new ArrayList<>();
        if(StringUtils.isBlank(userId)){
            //用户未登录
            String cartListCookieStr= CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookieStr)){
                //没有cookie
                cartInfos.add(cartInfo);
            }else{
                //有cookie
                 cartInfos=JSON.parseArray(cartListCookieStr,CartInfo.class);
                //判断是否重复的sku
               boolean b=ifnewCart(cartInfos,cartInfo);
               if(b){
                   cartInfos.add(cartInfo);  //添加
               }else{
                   for (CartInfo info : cartInfos) {
                       String skuId = info.getSkuId();
                       if(skuId.equals(cartInfo.getSkuId())){
                            info.setSkuName(info.getSkuName()+cartInfo.getSkuNum());
                            info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                       }
                   }
               }
            }
            //操作完成后覆盖cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);
        }else{
            //用户已登录，添加db
            String skuId = cartInfo.getSkuId();
            //select * from cart_info where sku_id=skuId and user_id=userId
            cartInfo.setUserId(userId);
            CartInfo cartInfoDB= cartService.ifCartExists(cartInfo);
            if(cartInfoDB!=null){
                //存在 更新
                cartInfoDB.setSkuNum(cartInfoDB.getSkuNum()+cartInfo.getSkuNum());
                cartInfoDB.setCartPrice(cartInfoDB.getSkuPrice().multiply(new BigDecimal(cartInfoDB.getSkuNum())));
                cartService.updateCart(cartInfoDB);
            }else{
                //不存在 插入
                cartService.saveCart(cartInfo);
            }
            //同步缓存
            cartService.syncCache(userId); //cart:userId:info
        }

        return "redirect:/cartSuccess";
    }

    private boolean ifnewCart(List<CartInfo> cartInfos, CartInfo cartInfo) {
        boolean b=true;
        for (CartInfo info : cartInfos) {
            String skuId = info.getSkuId();
            if(skuId.equals(cartInfo.getSkuId())){
            b=false;
            }
        }
        return b;
    }
}
