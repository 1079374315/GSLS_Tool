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
    private List<BindingBean> bindingBeanList;//绑定对象实

    public AndroidBean() {
        super();
    }

    public AndroidBean(String projectName, String projectPath, List<String> javaLibraryNames, List<BindingBean> bindingBeanList) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.javaLibraryNames = javaLibraryNames;
        this.bindingBeanList = bindingBeanList;
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
        return javaLibraryNames;
    }

    public void setJavaLibraryNames(List<String> javaLibraryNames) {
        this.javaLibraryNames = javaLibraryNames;
    }

    public void addJavaLibraryName(String libraryName) {
        if (javaLibraryNames == null) {
            javaLibraryNames = new ArrayList<>();
        }
        javaLibraryNames.add(libraryName);
    }

    public List<BindingBean> getBindingBeanList() {
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
