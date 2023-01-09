package com.gsls.gt_toolkit;

import com.gsls.gt.GT;

public class AppBean {

    public String name;
    public Object appIcon;
    public String function;
    public String longFunction;
    public String permissions;
    public GT.OnListener<Object> onListener;

    public AppBean(String name, Object appIcon, String function, String longFunction, String permissions, GT.OnListener<Object>... onListeners) {
        this.name = name;
        this.appIcon = appIcon;
        this.function = function;
        this.longFunction = longFunction;
        this.permissions = permissions;
        if (onListeners.length > 0) {
            this.onListener = onListeners[0];
        }
    }

    @Override
    public String toString() {
        return "AppBean{" +
                "name='" + name + '\'' +
                ", appIcon=" + appIcon +
                ", function='" + function + '\'' +
                ", longFunction='" + longFunction + '\'' +
                ", permissions='" + permissions + '\'' +
                '}';
    }
}
