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
        }
        return 0;
    }
}
