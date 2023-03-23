package com.gsls.gsls_tool;

import com.gsls.gsls_tool.bean.UserBean;
import com.gsls.gt.GT;

import java.util.List;

public interface DaoAPI {

    //插入操作
    @GT.Hibernate.GT_Insert()
    void insert(UserBean userBean);

    @GT.Hibernate.GT_Insert
    void insertAll(UserBean... userBeans);

    @GT.Hibernate.GT_Insert
    void insertAll(List<UserBean> list);

    //删除操作
    @GT.Hibernate.GT_Delete
    void delete(String name, int age);


    void delete(UserBean user, int index);

    @GT.Hibernate.GT_Delete("DELETE from user where uid > 0")
    void deleteAll();

    //查询操作
    @GT.Hibernate.GT_Query(where = "sex = ? and age > ?")
    GT.Hibernate.Call<List<UserBean>> queryAll(String sex, int age);

    @GT.Hibernate.GT_Code("SELECT * from UserBean")
    GT.Hibernate.Call<UserBean> query();


    //修改操作
    @GT.Hibernate.GT_Update
    void update(UserBean user);

}
