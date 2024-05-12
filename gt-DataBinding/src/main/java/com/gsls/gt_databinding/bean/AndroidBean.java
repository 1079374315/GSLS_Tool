package com.gsls.gt_databinding.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Android 模块 类的解析
 */
public class AndroidBean {

    private String projectName;//项目名
    private String projectPath;//项目路径
    private List<String> javaLibraryNames;//当前项目所有模块名称
    private List<String> javaLibraryPath;//当前项目所有模块路径
    private List<BindingBean> bindingBeanList;//绑定对象实

    public AndroidBean() {
        super();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public List<String> getJavaLibraryNames() {
        if (javaLibraryNames == null) {
            javaLibraryNames = new ArrayList<>();
        }
        return javaLibraryNames;
    }

    public List<String> getJavaLibraryPaths() {
        if (javaLibraryPath == null) {
            javaLibraryPath = new ArrayList<>();
        }
        return javaLibraryPath;
    }

    public void setJavaLibraryNames(List<String> javaLibraryNames) {
        this.javaLibraryNames = javaLibraryNames;
    }

    public void setJavaLibraryPaths(List<String> javaLibraryPath) {
        this.javaLibraryPath = javaLibraryPath;
    }

    public void addJavaLibraryName(String libraryName) {
        if (javaLibraryNames == null) {
            javaLibraryNames = new ArrayList<>();
        }
        if (javaLibraryNames.contains(libraryName)) return;
        javaLibraryNames.add(libraryName);
    }

    public void addJavaLibraryPath(String libraryName) {
        if (javaLibraryPath == null) {
            javaLibraryPath = new ArrayList<>();
        }
        if (javaLibraryPath.contains(libraryName)) return;
        javaLibraryPath.add(libraryName);
    }

    public void addJavaLibraryNames(List<String> list) {
        if (javaLibraryNames == null){
            javaLibraryNames = list;
        }else{
            javaLibraryNames.addAll(list);
        }
    }

    public List<BindingBean> getBindingBeanList() {
        if (bindingBeanList == null) {
            bindingBeanList = new ArrayList<>();
        }
        return bindingBeanList;
    }

    public void setBindingBeanList(List<BindingBean> bindingBeanList) {
        this.bindingBeanList = bindingBeanList;
    }

    public void addBindingBean(BindingBean bindingBean) {
        if (bindingBeanList == null) {
            bindingBeanList = new ArrayList<>();
        }
        bindingBeanList.add(bindingBean);
    }

    @Override
    public String toString() {
        return "AndroidBean{" +
                "projectName='" + projectName + '\'' +
                ", projectPath='" + projectPath + '\'' +
                ", javaLibraryNames=" + javaLibraryNames +
                ", bindingBeanListSize=" + (bindingBeanList != null ? bindingBeanList.size() : "null") +
                '}';
    }
}
