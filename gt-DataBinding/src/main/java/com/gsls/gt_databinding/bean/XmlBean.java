package com.gsls.gt_databinding.bean;

/**
 * 绑定对象xml布局 类的解析
 */
public class XmlBean {

    private String viewName;
    private String viewPackage;
    private String idName;
    private String layout;

    public XmlBean() {
        super();
    }

    public XmlBean(String viewName, String viewPackage, String idName, String layout) {
        super();
        this.viewName = viewName;
        this.viewPackage = viewPackage;
        this.idName = idName;
        this.layout = layout;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewPackage() {
        return viewPackage;
    }

    public void setViewPackage(String viewPackage) {
        this.viewPackage = viewPackage;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    @Override
    public String toString() {
        return "XmlBean [viewName=" + viewName + ", viewPackage=" + viewPackage + ", idName=" + idName + ", layout="
                + layout + "]";
    }

}
