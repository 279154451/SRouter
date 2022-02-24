package com.single.code.router.api;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 创建时间：2022/2/24
 * 创建人：singleCode
 * 功能描述：
 **/
public interface ResourceRequest extends Request{
    int getResource(@NonNull RequestType type, @NonNull Context context, String sourceName);
}
