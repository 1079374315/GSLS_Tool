package com.gsls.gt_databinding.route;

import java.util.Arrays;

public class GT_RouteMeta {
    private ClassType type;             //路由目标类型
    private Class<?> destination;       //路由目标的class
    private String path;                //路由路径
    private String group;               //路由组
    private String packClassPath;       //路由目标 类路径
    private String[] interceptors;       //拦截器

    public GT_RouteMeta() {
    }

    public static GT_RouteMeta build(ClassType type, Class<?> destination, String path, String group, String packClassPath, String[] interceptors) {
        return new GT_RouteMeta(type, destination, path, group, packClassPath, interceptors);
    }

    public GT_RouteMeta(ClassType type, Class<?> destination, String path, String group, String packClassPath, String[] interceptors) {
        this.type = type;
        this.destination = destination;
        this.path = path;
        this.group = group;
        this.packClassPath = packClassPath;
        this.interceptors = interceptors;
    }

    public String getPackClassPath() {
        return packClassPath;
    }

    public void setPackClassPath(String packClassPath) {
        this.packClassPath = packClassPath;
    }


    public ClassType getType() {
        return type;
    }

    public GT_RouteMeta setType(ClassType type) {
        this.type = type;
        return this;
    }

    public String[] getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(String[] interceptors) {
        this.interceptors = interceptors;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public GT_RouteMeta setDestination(Class<?> destination) {
        this.destination = destination;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GT_RouteMeta setPath(String path) {
        this.path = path;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public GT_RouteMeta setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public String toString() {
        return "GT_RouteMeta{" +
                "type=" + type +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                ", packClassPath='" + packClassPath + '\'' +
                ", interceptors='" + Arrays.toString(interceptors) + '\'' +
                '}';
    }
}
