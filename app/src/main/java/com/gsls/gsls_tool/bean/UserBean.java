package com.gsls.gsls_tool.bean;

import com.gsls.gt.GT;

@GT.Hibernate.GT_Entity
public class UserBean {

    @GT.Hibernate.GT_Key
    private int userId;

    private String name;
    private int age;
    private String sex;

    public UserBean() {
        super();
    }

    public UserBean(String name, int age, String sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }
}
