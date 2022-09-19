package com.gsls.gt_toolkit;

public class AppBean {

    public String name;
    public Object appIcon;
    public String function;

    public AppBean(String name, Object appIcon, String function) {
        this.name = name;
        this.appIcon = appIcon;
        this.function = function;
    }

    @Override
    public String toString() {
        return "AppBean{" +
                "name='" + name + '\'' +
                ", appIcon=" + appIcon +
                ", function='" + function + '\'' +
                '}';
    }
}
