package com.single.code.router_compiler.worker;

import com.single.code.router_compiler.tool.Logger;
import com.single.code.router_compiler.tool.RouterConfig;
import com.single.code.router_compiler.tool.RouterUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 创建时间：2021/4/20
 * 创建人：singleCode
 * 功能描述：
 **/
public abstract class AbsProcessorWorker {
    protected Types types;//类信息工具类，包含用于操作TypeMirror的工具方法
    protected Elements elementUtils;//操作Element的工具类（类、方法、属性其实都是Element）
    protected Messager messager;//打印日志工具
    protected Filer filer;//文件生成器（类、资源等要生成的文件，就是需要通过Filer来完成）
    protected Logger logger;
    protected boolean isApp = false;
    protected String moduleName;
    protected String aptPackageName;
    protected RouterUtils routerUtils;
    public  void init(ProcessingEnvironment processingEnv){
        elementUtils = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        logger = new Logger(messager);
        filer = processingEnv.getFiler();
        //获取安卓工程传递过来的值
        String value = processingEnv.getOptions().get("isApp");
        moduleName = processingEnv.getOptions().get(RouterConfig.MODULE_NAME);
        aptPackageName= processingEnv.getOptions().get(RouterConfig.APT_PKG_NAME);
        if(StringUtils.isEmpty(aptPackageName)){
            aptPackageName = "com.single.code."+moduleName;
        }
        routerUtils = new RouterUtils(logger,elementUtils,types,moduleName);
        if(!StringUtils.isEmpty(value)){
            isApp = Boolean.parseBoolean(value);
        }
        logger.info("===INIT===>moduleName="+moduleName+" isApp="+isApp+" aptPackageName="+aptPackageName);
    }
    public abstract void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
}
