package com.single.code.router.api;

import com.single.code.annotation.RouterBean;

import java.util.Map;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public interface RouterPath {
    //   pathMap.put("/personal/Personal_MainActivity",
    //                RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
    //                                  Order_MainActivity.class,
    //                           "/personal/Personal_MainActivity",
    //                          "personal"));
    Map<String, RouterBean> getPathMap();
}
