package com.single.code.router.api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class RouterBundle {
    // 携带的值，保存到这里  Intent 传输
    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return this.bundle;
    }

    // 对外界提供，可以携带参数的方法
    public RouterBundle withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    public RouterBundle withBoolean(@NonNull String key, @Nullable boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public RouterBundle withInt(@NonNull String key, @Nullable int value) {
        bundle.putInt(key, value);
        return this;
    }

    public RouterBundle withBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    // Derry只写到了这里，同学们可以自己增加 ...
    // 架构师后续扩展

    // 直接完成跳转
    public Object navigation(Context context) {
        // 单一原则
        // 把自己所有行为 都交给了  路由管理器
        return RouterApi.getManager().navigation(context, this);
    }
}
