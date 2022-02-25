package com.single.code.login;

import android.content.Context;

import com.single.code.annotation.SRouter;
import com.single.code.router.api.ResourceRequest;

/**
 * 创建时间：2022/2/24
 * 创建人：singleCode
 * 功能描述：
 **/
@SRouter(path = "/login/LoginDrawableRequest")
public class LoginDrawableRequest implements ResourceRequest {
    @Override
    public int getResource(RequestType type, Context context,String sourceName) {
        switch (type){
            case DRAWABLE:
                int drawableId = context.getResources().getIdentifier(sourceName, "drawable", context.getPackageName());
                return drawableId;
            case COLOR:
                int colorId = context.getResources().getIdentifier(sourceName, "color", context.getPackageName());
                return colorId;
            case STRING:
                int stringId = context.getResources().getIdentifier(sourceName, "string", context.getPackageName());
                return stringId;
            case LAYOUT:
                int layoutId = context.getResources().getIdentifier(sourceName, "layout", context.getPackageName());
                return layoutId;
        }
        new RuntimeException("未找到对应资源");
        return 0;
    }
}
