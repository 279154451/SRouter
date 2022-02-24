package com.single.code.router.api;

import java.util.Map;

public interface RouterGroup {
    //  groupMap.put("personal", ARouter$$Path$$personal.class);
    Map<String,Class<? extends RouterPath>> getGroupMap();
}