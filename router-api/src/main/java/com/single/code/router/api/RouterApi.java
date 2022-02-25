package com.single.code.router.api;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.RequiresApi;

import com.single.code.annotation.RouterBean;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 * 1、查找Group 和Path
 * 2、使用
 **/
public class RouterApi {
    private String group;
    private String path;
    private String TAG = "RouterApi";

    private static RouterApi manager;

    public static RouterApi getManager() {
        if(manager == null){
            synchronized (RouterApi.class){
                if(manager == null){
                    manager = new RouterApi();
                }
            }
        }
        return manager;
    }
    private LruCache<String,RouterGroup> groupCache;
    private LruCache<String,RouterPath> pathCache;
    private RouterApi(){
        groupCache = new LruCache<>(100);
        pathCache = new LruCache<>(100);
    }
    static final String PATH_FILE_NAME = "Router$Path$";
    static final String GROUP_FILE_NAME = "Router$Group$";
    /***
     * @param path 例如：/order/Order_MainActivity
     *      * @return
     */
    public RouterBundle build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        if (path.lastIndexOf("/") == 0) { // 只写了一个 /
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // 截取组名  /order/Order_MainActivity  finalGroup=order
        String finalGroup = path.substring(1, path.indexOf("/", 1)); // finalGroup = order

        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("不按常理出牌 path乱搞的啊，正确写法：如 /order/Order_MainActivity");
        }

        // TODO  证明没有问题，没有抛出异常
        this.path =  path;  // 最终的效果：如 /order/Order_MainActivity
        this.group = finalGroup; // 例如：order，personal

        return new RouterBundle();
    }

    // 真正完成跳转
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Object navigation(Context context, RouterBundle bundle) {
        // 拼接 ARouter$$Group$order 才能找到
        // 例如：寻找 ARouter$Group$$personal
        String packageName = RouterBean.ROUTER_DF_PKG+group;
        String groupClassName = packageName + "." + GROUP_FILE_NAME + group;
        Log.e(TAG, "navigation: groupClassName=" + groupClassName);

        try {

            /**
             * TODO  Group 缓存
             */
            // 读取路由组Group类文件
            RouterGroup loadGroup = groupCache.get(group);
            // 读取路由组Group类文件
            if (null == loadGroup) { // 缓存里面没有东东
                // 加载APT路由组Group类文件 例如：ARouter$$Group$$order
                Class<?> aClass = Class.forName(groupClassName);
                // 初始化类文件
                loadGroup = (RouterGroup) aClass.newInstance();

                // 保存到缓存
                groupCache.put(group, loadGroup);
            }

            if (loadGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表Group报废了...");
            }

            /**
             * TODO  PATH 缓存
             */
            // 读取路由Path类文件
            RouterPath loadPath = pathCache.get(path);
            if (null == loadPath) { // 缓存里面没有东东 Path
                // 1.invoke loadGroup
                // 2.Map<String, Class<? extends ARouterLoadPath>>
                Class<? extends RouterPath> clazz = loadGroup.getGroupMap().get(group);
                // 3.从map里面获取 ARouter$$Path$$order.class
                loadPath = clazz.newInstance();

                // 保存到缓存
                pathCache.put(path, loadPath);
            }

            if (loadPath != null) { // 健壮
                if (loadPath.getPathMap().isEmpty()) {
                    throw new RuntimeException("路由表Path报废了...");
                }

                // 我们已经进入 PATH 函数 ，开始拿 Class 进行跳转
                RouterBean routerBean = loadPath.getPathMap().get(path);
                if (routerBean != null) {
                    switch (routerBean.getTypeEnum()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getClazz()); // 例如：getClazz == Order_MainActivity.class
                            intent.putExtras(bundle.getBundle()); // 携带参数
                            context.startActivity(intent);
                            break;
                        case REQUEST:
                            Class<?> clazz = routerBean.getClazz();
                            return clazz.newInstance();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
