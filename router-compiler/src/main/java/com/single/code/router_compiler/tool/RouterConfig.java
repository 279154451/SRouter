package com.single.code.router_compiler.tool;

import com.single.code.annotation.RouterBean;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public interface RouterConfig {
    String MODULE_NAME ="moduleName";
    String APT_PKG_NAME ="aptPkgName";
    String ACTIVITY_PACKAGE = "android.app.Activity";
    String API_PACKAGE = "com.single.code.router.api";
    String PATH_API = API_PACKAGE+".RouterPath";
    String GROUP_API =  API_PACKAGE+".RouterGroup";
    String REQUEST_API =  API_PACKAGE+".Request";
    String PARAMETER_API =  API_PACKAGE+".RouterParameter";
    String PARAMETER_TARGET_VAR= "target";
    String T_VAR= "t";
    String PATH_METHOD = "getPathMap";
    String GROUP_METHOD = "getGroupMap";
    String PARAMETER_METHOD = "bindParameter";
    String PATH_MAP_VAR = "pathMap";
    String GROUP_MAP_VAR = "groupMap";
    String PATH_FILE_NAME = "Router$Path$";
    String GROUP_FILE_NAME = "Router$Group$";
    String PARAMETER_FILE_NAME = "$Parameter";
    String STRING = "java.lang.String";
    String ROUTER_API ="RouterApi";
    String ROUTER_DF_PKG= RouterBean.ROUTER_DF_PKG;

}
