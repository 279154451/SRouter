package com.single.code.router_compiler.worker;

import com.single.code.annotation.Parameter;
import com.single.code.router_compiler.tool.RouterConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class SParameterWorker extends AbsProcessorWorker {
    private Map<TypeElement, List<Element>> parameterMap = new HashMap<>();

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Parameter.class);
        if (CollectionUtils.isNotEmpty(elements)) {
            for (Element element : elements) {
                /**
                 * 这里的element对应的是属性,如：
                 * @Parameter
                 * String userId;
                 */
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();//获取属性所在的类,如：MainActivity
                if (parameterMap.containsKey(typeElement)) {
                    parameterMap.get(typeElement).add(element);
                } else {
                    List<Element> fields = new ArrayList<>();//属性列表缓存
                    fields.add(element);
                    parameterMap.put(typeElement, fields);
                }
            }
            try {
                createParameterFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createParameterFile() throws IOException {
        if (!parameterMap.isEmpty()) {
            TypeElement activity = elementUtils.getTypeElement(RouterConfig.ACTIVITY_PACKAGE);
            TypeElement parameterType = elementUtils.getTypeElement(RouterConfig.PARAMETER_API);
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, RouterConfig.PARAMETER_TARGET_VAR).build();
            for (Map.Entry<TypeElement, List<Element>> entry : parameterMap.entrySet()) {
                TypeElement key = entry.getKey();
                List<Element> fields = entry.getValue();
                if (types.isSubtype(key.asType(), activity.asType())) {
                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(RouterConfig.PARAMETER_METHOD)
                            .returns(void.class)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(parameterSpec);
                    ClassName clazz = ClassName.get(key);
                    methodBuilder.addStatement("$T t= ($T)"+RouterConfig.PARAMETER_TARGET_VAR,clazz,clazz);
                    for (Element field : fields) {
                        TypeMirror fieldType = field.asType();
                        int ordinal = fieldType.getKind().ordinal();//获取属性的序列号
                        String fieldName = field.getSimpleName().toString();//获取属性名称
                        String annotationName = field.getAnnotation(Parameter.class).name();
                        // 判断注解的值为空的情况下的处理（注解中有name值就用注解值）
                        annotationName = StringUtils.isEmpty(annotationName) ? fieldName : annotationName;
                        String finalValue = "t."+fieldName;
                        String getMethodContent = finalValue+"="+"t.getIntent().";
                        if(ordinal == TypeKind.INT.ordinal()){
                            getMethodContent += "getIntExtra($S,"+finalValue+")";
                        }else if(ordinal == TypeKind.BOOLEAN.ordinal()){
                            getMethodContent += "getBooleanExtra($S,"+finalValue+")";
                        }else{
                            if(fieldType.toString().equalsIgnoreCase(RouterConfig.STRING)){
                                //String 类型
                                getMethodContent += "getStringExtra($S)";
                            }
                        }
                        if(getMethodContent.endsWith(")")){
                            methodBuilder.addStatement(getMethodContent,annotationName);
                        }else {
                            logger.warning("only support String、int、boolean Parameter Type");
                        }
                    }
                    String finalParameterClazz = key.getSimpleName()+RouterConfig.PARAMETER_FILE_NAME;
                    TypeSpec finalParameterClazzType = TypeSpec.classBuilder(finalParameterClazz)
                            .addSuperinterface(ClassName.get(parameterType))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())
                            .build();
//                    JavaFile.builder(aptPackageName,finalParameterClazzType).build().writeTo(filer);
                    JavaFile.builder(clazz.packageName(),finalParameterClazzType).build().writeTo(filer);
                } else {
                    logger.error("only can use in Activity");
                }
            }
        }
    }

}
