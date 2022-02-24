package com.single.code.router_compiler.worker;

import com.single.code.annotation.SRouter;
import com.single.code.annotation.RouterBean;
import com.single.code.router_compiler.tool.RouterConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class SRouterWorker extends AbsProcessorWorker {

    //path 仓库 Map<"person",List<RouterBean>>
    private Map<String, List<RouterBean>> pathMap = new HashMap<>();
    //group 仓库  Map<“person”,"Router$Path$person">
    private Map<String,String> groupMap = new HashMap<>();
    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SRouter.class);
        if(CollectionUtils.isNotEmpty(elements)){
            logger.info("===SRouterWorker parseProcess===>"+elements.size());
            TypeElement pathType = elementUtils.getTypeElement(RouterConfig.PATH_API);
            TypeElement groupType = elementUtils.getTypeElement(RouterConfig.GROUP_API);
            //1、遍历注解，校验注解、缓存信息
            for (Element element : elements){
                SRouter route = element.getAnnotation(SRouter.class);
                RouterBean routerBean = new RouterBean.Builder()
                        .addGroup(route.group())
                        .addPath(route.path())
                        .addElement(element)
                        .build();
                if(routerUtils.checkAnnotation(routerBean)){
                    logger.info("Router checkAnnotation success for "+element);
                    List<RouterBean> routerBeans = pathMap.get(routerBean.getGroup());
                    if(routerUtils.isEmpty(routerBeans)){
                        routerBeans = new ArrayList<>();
                        routerBeans.add(routerBean);
                        pathMap.put(routerBean.getGroup(),routerBeans);
                    }else {
                        routerBeans.add(routerBean);
                    }
                }else {
                    logger.error("Router annotation fail");
                }
            }
            try {
                createPathFile(pathType);
                createGroupFile(groupType,pathType);
            }catch (Exception e){
                e.printStackTrace();
                logger.error("生成Path文件失败："+e.toString());
            }
        }
    }

    private void createGroupFile(TypeElement groupType,TypeElement pathType) throws IOException {
        if(groupMap.isEmpty()){
            return;
        }
        ParameterizedTypeName returnType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))//Class<? extends RouterPath>
                )
        );//Map<String,Class<? extends RouterPath>>
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(RouterConfig.GROUP_METHOD)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addStatement("$T $N = new $T<>()",
                        returnType,
                        RouterConfig.GROUP_MAP_VAR,
                        ClassName.get(HashMap.class));//生成变量groupMap
        for ( Map.Entry<String, String> entry: groupMap.entrySet()) {
            String groupName = entry.getKey();
            String pathClazzName = entry.getValue();
            logger.info("pathClazzName ="+pathClazzName);
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    RouterConfig.GROUP_MAP_VAR,
                    groupName,
                    ClassName.get(aptPackageName,pathClazzName));
            methodBuilder.addStatement("return $N", RouterConfig.GROUP_MAP_VAR);
            //2、创建类
            String groupClazzName = RouterConfig.GROUP_FILE_NAME+groupName;
            logger.info("groupClazzName ="+groupClazzName);
            TypeSpec clazz = TypeSpec.classBuilder(groupClazzName)
                    .addSuperinterface(ClassName.get(groupType))
                    .addMethod(methodBuilder.build())
                    .addModifiers(Modifier.PUBLIC)
                    .build();
            //3、创建包
            JavaFile packagef = JavaFile.builder(aptPackageName,clazz)
                    .build();
            //4、生成文件
            packagef.writeTo(filer);
        }
    }
    /**
     * 创建Path文件
     * @param pathType
     */
    private void createPathFile(TypeElement pathType) throws IOException {
        if(pathMap.isEmpty()){
            return;
        }
        ParameterizedTypeName returnType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class)
        );
        for (Map.Entry<String,List<RouterBean>> entry:pathMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(RouterConfig.PATH_METHOD)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType);
            methodBuilder.addStatement("$T $N = new $T<>()"
                            ,returnType,RouterConfig.PATH_MAP_VAR,ClassName.get(HashMap.class));//生成变量pathMap
            List<RouterBean> pathList = entry.getValue();
            String groupName = entry.getKey();
            for (RouterBean routerBean : pathList) {
                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",//枚举使用$L
                        RouterConfig.PATH_MAP_VAR,
                        routerBean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.TypeEnum.class),
                        routerBean.getTypeEnum(),
                        ClassName.get((TypeElement) routerBean.getElement()),//XXActivity.class
                        routerBean.getPath(),
                        routerBean.getGroup()
                        );
            }
            methodBuilder.addStatement("return $N",RouterConfig.PATH_MAP_VAR);
            //2、创建类
            String pathClazzName = RouterConfig.PATH_FILE_NAME+groupName;
            TypeSpec clazz = TypeSpec.classBuilder(pathClazzName)
                    .addSuperinterface(ClassName.get(pathType))
                    .addMethod(methodBuilder.build())
                    .addModifiers(Modifier.PUBLIC)//定义类声明 public final class "MircoApi_"+path+"_"+user{}
                    .build();
            //3、创建包
            JavaFile packagef = JavaFile.builder(aptPackageName,clazz)
                    .build();
            //4、生成文件
//            if (packagef.toJavaFileObject().delete()) {
//              logger.info("Deleted previously generated file");
//            }
            packagef.writeTo(filer);
            groupMap.put(groupName,pathClazzName);
        }
    }
}
