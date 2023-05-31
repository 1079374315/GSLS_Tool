package com.gsls.gsls_tool;

import java.util.List;
import java.util.Map;

public class LoginBean {

    public LoginBean(int loginId, String name, boolean sex, int age, Map<Object, Object> map, List<String> list, UserBean userBean) {
        this.loginId = loginId;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.map = map;
        this.list = list;
        this.userBean = userBean;
    }

    private int loginId;
    private String name;
    private boolean sex;
    private int age;
    private Map<Object,Object> map;
    private List<String> list;
    private UserBean userBean;

}
