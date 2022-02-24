package com.single.code.router_compiler.tool;

import com.single.code.annotation.RouterBean;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class RouterUtils {
    private Logger logger;
    private String moduleName;
    private Types types;
    protected Elements elementUtils;
    public RouterUtils(Logger logger, Elements elementUtil, Types types,String moduleName) {
        this.logger = logger;
        this.moduleName = moduleName;
        this.types = types;
        this.elementUtils  =elementUtil;
    }

    public  boolean checkAnnotation( RouterBean bean){
        if(StringUtils.isEmpty(moduleName)){
            logger.warning("使用SRouter注解模块需要配置moduleName");
            return false;
        }
        TypeMirror elementType = bean.getElement().asType();
        TypeElement activity = elementUtils.getTypeElement(RouterConfig.ACTIVITY_PACKAGE);
        TypeMirror activityType = activity.asType();
        if(!types.isSubtype(elementType,activityType)){
            logger.warning("SRouter 注解只能在Activity上使用");
            return false;
        }else {
            bean.setTypeEnum(RouterBean.TypeEnum.ACTIVITY);
        }
        String path = bean.getPath();
        String group = bean.getGroup();
        if(StringUtils.isEmpty(path)||!path.startsWith("/")){
            logger.warning("@SRoute path 必须要/开头, look at [" + path+ "].");
            return false;
        }
        if(path.lastIndexOf("/")==0){
            logger.warning("@SRoute path 未按规范配置 ，如：/app/MainActivity");
            return false;
        }
        String finalGroup = path.substring(1,path.indexOf("/",1));
        if(!StringUtils.isEmpty(group)){
            if(!StringUtils.equals(group,finalGroup)){
                logger.warning("SRouter path 前面的组名和group配置不匹配");
                return false;
            }else if(!StringUtils.equals(group,moduleName)){
                logger.warning("SRouter 注解中group必须和模块名一致");
                return false;
            }
        }else {
            bean.setGroup(finalGroup);
        }

        return true;
    }

    public boolean isEmpty(List<?> list){
        if(list == null || list.isEmpty()){
            return true;
        }
        return false;
    }

}
