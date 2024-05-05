package com.gsls.gt_databinding.bean;

import com.gsls.gt_databinding.route.ClassType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 绑定对象 类的解析
 */
public class BindingBean {

    private String resourcePackName;//资源包名
    private String packName;//包名
    private String className;// 类名字符串

    private Class<Object> objectClass;// 类名 class
    private String packClassPath;//包类相对路径
    private String layoutName;//布局名称
    private String extras;//说明
    private boolean isAsync;
    private String[] interceptors;
    private String layoutPath;//布局路径
    private String classPath;//类路径
    private String layoutAbsolutePath;//布局绝对路径
    private String javaLibraryName;//当前Java 库名
    private String bingingType;//绑定类型
    private List<XmlBean> xmlBeanList;//xml布局类型
    private String classCode;//源代码

    private String classType;//对象类型

    private Object annotateValue;//注解信息

    public BindingBean() {
        super();
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public String[] getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(String[] interceptors) {
        this.interceptors = interceptors;
    }

    public Class<Object> getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class<Object> objectClass) {
        this.objectClass = objectClass;
    }

    public Object getAnnotateValue() {
        return annotateValue;
    }

    public void setAnnotateValue(Object annotateValue) {
        this.annotateValue = annotateValue;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getResourcePackName() {
        return resourcePackName;
    }

    public void setResourcePackName(String resourcePackName) {
        this.resourcePackName = resourcePackName;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public String getLayoutPath() {
        return layoutPath;
    }

    public void setLayoutPath(String layoutPath) {
        this.layoutPath = layoutPath;
    }

    public String getLayoutAbsolutePath() {
        return layoutAbsolutePath;
    }

    public void setLayoutAbsolutePath(String layoutAbsolutePath) {
        this.layoutAbsolutePath = layoutAbsolutePath;
    }

    public String getJavaLibraryName() {
        return javaLibraryName;
    }

    public void setJavaLibraryName(String javaLibraryName) {
        this.javaLibraryName = javaLibraryName;
    }

    public String getBingingType() {
        return bingingType;
    }

    public void setBingingType(String bingingType) {
        this.bingingType = bingingType;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public List<XmlBean> getXmlBeanList() {
        return xmlBeanList;
    }

    public void setXmlBeanList(List<XmlBean> xmlBeanList) {
        this.xmlBeanList = xmlBeanList;
    }

    public void addXmlBean(XmlBean xmlBean) {
        if (xmlBeanList == null) {
            xmlBeanList = new ArrayList<>();
        }
        xmlBeanList.add(xmlBean);
    }

    public String getPackClassPath() {
        return packClassPath;
    }

    public void setPackClassPath(String packClassPath) {
        this.packClassPath = packClassPath;
    }


    @Override
    public String toString() {
        return "BindingBean{" +
                "resourcePackName='" + resourcePackName + '\'' +
                ", packName='" + packName + '\'' +
                ", className='" + className + '\'' +
                ", objectClass=" + objectClass +
                ", packClassPath='" + packClassPath + '\'' +
                ", layoutName='" + layoutName + '\'' +
                ", extras='" + extras + '\'' +
                ", isAsync=" + isAsync +
                ", interceptors=" + Arrays.toString(interceptors) +
                ", layoutPath='" + layoutPath + '\'' +
                ", classPath='" + classPath + '\'' +
                ", layoutAbsolutePath='" + layoutAbsolutePath + '\'' +
                ", javaLibraryName='" + javaLibraryName + '\'' +
                ", bingingType='" + bingingType + '\'' +
                ", xmlBeanList=" + xmlBeanList +
                ", classCode='" + "classCode" + '\'' +
                ", classType=" + classType +
                ", annotateValue=" + annotateValue +
                '}';
    }
}
