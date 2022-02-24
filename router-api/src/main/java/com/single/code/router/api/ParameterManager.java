package com.single.code.router.api;


import android.app.Activity;
import android.util.LruCache;

import androidx.fragment.app.Fragment;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：查找并绑定属性
 **/
public class ParameterManager {
    private static ParameterManager manager;

    public static ParameterManager getManager() {
        if(manager == null){
            synchronized (ParameterManager.class){
                if(manager == null){
                    manager = new ParameterManager();
                }
            }
        }
        return manager;
    }
    private LruCache<String,RouterParameter> cache;
    private ParameterManager(){
        cache = new LruCache<>(100);
    }

    static final String FILE_SUFFIX_NAME="$Parameter";

    public void loadParameter(Activity activity){
        String className = activity.getClass().getName();
        RouterParameter iParameter = cache.get(className);
        if(iParameter == null){
            try{
                Class<?> clazz = Class.forName(className+FILE_SUFFIX_NAME);
                iParameter = (RouterParameter) clazz.newInstance();
                cache.put(className,iParameter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        iParameter.bindParameter(activity);
    }
    public void loadParameter(Fragment fragment){
        String className = fragment.getClass().getName();
        RouterParameter iParameter = cache.get(className);
        if(iParameter == null){
            try{
                Class<?> clazz = Class.forName(className+FILE_SUFFIX_NAME);
                iParameter = (RouterParameter) clazz.newInstance();
                cache.put(className,iParameter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        iParameter.bindParameter(fragment);
    }
}
