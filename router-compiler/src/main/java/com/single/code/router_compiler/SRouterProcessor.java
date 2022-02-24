package com.single.code.router_compiler;

import com.google.auto.service.AutoService;
import com.single.code.annotation.Parameter;
import com.single.code.annotation.SMircoPlugin;
import com.single.code.annotation.SRouter;
import com.single.code.router_compiler.tool.RouterConfig;
import com.single.code.router_compiler.worker.AbsProcessorWorker;
import com.single.code.router_compiler.worker.SParameterWorker;
import com.single.code.router_compiler.worker.SRouterWorker;

import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * 创建时间：2021/4/13
 * 创建人：singleCode
 * 功能描述：
 **/
@AutoService(Processor.class)//启动注解处理器服务
//@SupportedAnnotationTypes({"com.single.code.annotation.SRouter","com.single.code.annotation.SMircoPlugin"})//该注解处理器支持的注解类型
//@SupportedOptions({"student","isApp"})//接收安卓工程传递过来的参数
public class SRouterProcessor extends AbstractProcessor {
    private List<AbsProcessorWorker> workers = new CopyOnWriteArrayList<>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
//        workers.add(new MircoPluginWorker());
        workers.add(new SRouterWorker());
        workers.add(new SParameterWorker());
        for (AbsProcessorWorker worker :workers){
            worker.init(processingEnv);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        /*
        * 1、只有代码中使用该处理器支持的注解，才会执行，否则不会执行
        * 2、会执行两次，第二次执行是为了进行检验
        */

        if(CollectionUtils.isNotEmpty(annotations)){
            for (AbsProcessorWorker worker:workers){
                worker.process(annotations,roundEnv);
            }
            return true;
        }
        return false;//false:表示不干活了,只会执行一次  true:会执行两次检验
    }




    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>() {{
            this.add(RouterConfig.MODULE_NAME);
            this.add(RouterConfig.APT_PKG_NAME);
            this.add("isApp");
            this.add("student");
        }};
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {{
            this.add(SRouter.class.getName());
            this.add(SMircoPlugin.class.getName());
            this.add(Parameter.class.getName());
        }};
    }
}
