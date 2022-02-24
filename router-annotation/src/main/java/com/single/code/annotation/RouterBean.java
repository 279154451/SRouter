package com.single.code.annotation;

import javax.lang.model.element.Element;

/**
 * 创建时间：2021/4/23
 * 创建人：singleCode
 * 功能描述：
 **/
public class RouterBean {
    public enum TypeEnum{
        ACTIVITY
    }
    public static class Builder{
        private String path;
        private String group;
        private Class<?> clazz;
        private Element element;
        private TypeEnum typeEnum;
        public Builder addPath(String path){
            this.path = path;
            return this;
        }
        public Builder addGroup(String group){
            this.group = group;
            return this;
        }
        public Builder addClazz(Class<?> clazz){
            this.clazz = clazz;
            return this;
        }
        public Builder addElement(Element element){
            this.element = element;
            return this;
        }
        public RouterBean build(){
            return new RouterBean(path,group,clazz,element,typeEnum);
        }
    }
    private String path;
    private String group;
    private Class<?> clazz;
    private Element element;
    private TypeEnum typeEnum;

    public static RouterBean create(TypeEnum typeEnum,Class<?> clazz,String path,String group){
        RouterBean routerBean = new RouterBean(path,group,clazz,typeEnum);
        return routerBean;
    }
    public RouterBean(){

    }
    private RouterBean(String path, String group, Class<?> clazz,TypeEnum typeEnum) {
        this.path = path;
        this.group = group;
        this.clazz = clazz;
        this.typeEnum = typeEnum;
    }
    private RouterBean(String path, String group, Class<?> clazz, Element element, TypeEnum typeEnum) {
        this.path = path;
        this.group = group;
        this.clazz = clazz;
        this.element = element;
        this.typeEnum = typeEnum;
    }

    public String getPath() {
        return path;
    }

    public String getGroup() {
        return group;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Element getElement() {
        return element;
    }

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }
}
