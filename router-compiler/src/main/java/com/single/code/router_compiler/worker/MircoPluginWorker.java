package com.single.code.router_compiler.worker;

import com.single.code.annotation.MircoPluginApi;
import com.single.code.annotation.SMircoPlugin;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * 创建时间：2021/4/20
 * 创建人：singleCode
 * 功能描述：
 **/

public class MircoPluginWorker extends AbsProcessorWorker{
    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        parseProcess(roundEnv);
    }
    private void parseProcess( RoundEnvironment roundEnv){
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SMircoPlugin.class);
        if(CollectionUtils.isNotEmpty(elements)){
            logger.info("===MircoPluginWorker parseProcess===>"+elements.size());
            createMicroPlugin(elements);
        }
    }
    private void createMicroPlugin(Set<? extends Element> elements){
        //1、创建方法
        //2、创建类
        TypeSpec clazz = TypeSpec.classBuilder("MircoPlugin2")
                .addField(createServiceMapField())
                .addMethod(parseRouter2Init(createInitMethod(),elements))
                .addMethod(createUserMapMethod())
                .addMethod(createFoundServiceMethod())
                .addMethod(createBindServiceMethod())
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)//定义类声明 public final class MyTest{}
                .build();
        //3、创建包
        JavaFile packagef = JavaFile.builder("com.single.code.mircoservice",clazz)
                .build();
        //4、生成文件
        try {
            packagef.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("类生成失败:"+e.toString());
        }
    }
    private MethodSpec createBindServiceMethod(){
        //1、创建方法
        ParameterizedTypeName clazzApiType = ParameterizedTypeName.get(
                ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(MircoPluginApi.class)
        );
        ParameterSpec clazzApi = ParameterSpec.builder(clazzApiType, "clazz").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bindService").
                addModifiers(Modifier.PRIVATE,Modifier.STATIC)//方法声明 public static main
                .returns(void.class)//方法返回值类型
                .addParameter(String.class,"apiName")
                .addParameter(String.class,"user")
                .addParameter(clazzApi);//方法参数;
        ParameterizedTypeName mapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                clazzApiType
        );
        ClassName hasmap = ClassName.get("java.util", "HashMap");
        String userMapStr = "userMap";
        methodBuilder.addStatement("$T $N",mapType,userMapStr);//创建局部变量userMap $N来表示变量 为什么不是$S,因为变量有引用，所以是N
        methodBuilder.beginControlFlow("if(mircoServiceMap.containsKey(apiName))");
        methodBuilder.addStatement("userMap = mircoServiceMap.get(apiName)");
        methodBuilder.beginControlFlow("if(!userMap.containsKey(user))");
        methodBuilder.addStatement("userMap.put(user,clazz)");
        methodBuilder.endControlFlow();
        methodBuilder.nextControlFlow("else");
        methodBuilder.addStatement("userMap = new $T<>()",hasmap);//创建局部变量map
        methodBuilder.addStatement("userMap.put(user,clazz)");
        methodBuilder.addStatement("mircoServiceMap.put(apiName,userMap)");
        methodBuilder.endControlFlow();
        return methodBuilder.build();
    }
    private MethodSpec createUserMapMethod(){
        ParameterizedTypeName clazzApiType = ParameterizedTypeName.get(
                ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(MircoPluginApi.class)
        );
        ParameterizedTypeName userMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                clazzApiType
        );
        ClassName hasmap = ClassName.get("java.util", "HashMap");
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("makeUserMap").
                addModifiers(Modifier.PRIVATE,Modifier.STATIC)//方法声明 public static main
                .returns(userMapType)//方法返回值类型
                .addParameter(String.class,"user")
                .addParameter(clazzApiType,"clazz");
        methodBuilder.addStatement("$T userMap = new $T<>()",userMapType,hasmap);//创建局部变量map
        methodBuilder.addStatement("userMap.put(user,clazz)");
        methodBuilder.addStatement("return userMap");
        return methodBuilder.build();
    }
    private MethodSpec parseRouter2Init(MethodSpec.Builder methodBuilder,Set<? extends Element> elements){
        logger.info("parseRouter2Init elements="+elements.size());
        TypeMirror MircoPluginApiType =  elementUtils.getTypeElement("com.single.code.annotation.MircoPluginApi").asType();
        for (Element element : elements) {
            TypeMirror tm = element.asType();
            SMircoPlugin route = element.getAnnotation(SMircoPlugin.class);
            String path = route.path();
            String user = route.user();
            ClassName className = ClassName.get((TypeElement) element);
            logger.info("parseRouter2Init path="+path+" user="+user+" "+className.toString());
            if(!StringUtils.isEmpty(path) && !StringUtils.isEmpty(user)){
                TypeMirror parent = ((TypeElement) element).getSuperclass();
                if (types.isSubtype(parent, MircoPluginApiType)){
                    methodBuilder.addStatement("bindService($S,$S,$T.class)",path,user,className);
                }else {
                    throw new RuntimeException("The @SRoute annotation user class must be extends MircoPluginApi, look at [" + tm.toString() + "].");
                }
                if (parent instanceof DeclaredType) {
                    Element parentElement = ((DeclaredType) parent).asElement();
                    ClassName parentClassName = ClassName.get((TypeElement) parentElement);
                    logger.info("parseRouter2Init parentClassName="+parentClassName.toString());
                    Class clazz = parentClassName.getClass();

                }
            }else {
                throw new RuntimeException("The @SRoute { path and user } cant be empty, look at [" + tm.toString() + "].");
            }
        }
        return methodBuilder.build();
    }
    private FieldSpec createServiceMapField(){
        ParameterizedTypeName clazzApiType = ParameterizedTypeName.get(
                ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(MircoPluginApi.class)
        );
        ParameterizedTypeName userMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                clazzApiType
        );
        ParameterizedTypeName serviceMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                userMapType
        );
        ClassName hasmap = ClassName.get("java.util", "HashMap");
        FieldSpec serviceMapField = FieldSpec.builder(serviceMapType,"mircoServiceMap")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .initializer("new $T<>()",hasmap).build();
        return serviceMapField;
    }

    private MethodSpec.Builder createInitMethod(){
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("init").
                addModifiers(Modifier.PRIVATE,Modifier.STATIC)//方法声明 public static main
                .returns(void.class);//方法返回值类型
        methodBuilder.addStatement("$T.out.println($S)",System.class,"init mircoServiceMap");

        return methodBuilder;
    }
    private MethodSpec createFoundServiceMethod(){
        ParameterizedTypeName clazzApiType = ParameterizedTypeName.get(
                ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(MircoPluginApi.class)
        );
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("foundService").
                addModifiers(Modifier.PUBLIC,Modifier.STATIC)//方法声明 public static main
                .returns(clazzApiType)//方法返回值类型
                .addParameter(String.class,"apiName")
                .addParameter(String.class,"user");
        methodBuilder.beginControlFlow("if(mircoServiceMap.isEmpty())");
        methodBuilder.addStatement("init()");
        methodBuilder.endControlFlow();
        methodBuilder.addStatement("$T apiClass =null",clazzApiType);//创建局部变量userMap
        methodBuilder.beginControlFlow("if(mircoServiceMap.containsKey(apiName))");
        methodBuilder.addStatement("Map<String, Class<?extends MircoPluginApi>> userMap = mircoServiceMap.get(apiName)");
        methodBuilder.beginControlFlow("if(userMap.containsKey(user))");
        methodBuilder.addStatement(" apiClass = userMap.get(user)");
        methodBuilder.endControlFlow();
        methodBuilder.endControlFlow();
        methodBuilder.addStatement("return apiClass");
        return methodBuilder.build();
    }

}
