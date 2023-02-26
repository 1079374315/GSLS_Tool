package com.gsls.gsls_tool;

import com.gsls.gt.GT;
import com.gsls.gt_databinding.annotation.GT_Dao;

import java.util.List;

@GT_Dao(value = UserBean.class)//表类型从这里拿到
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
    void delete(UserBean user);


    void delete(UserBean user, int index);

    @GT.Hibernate.GT_Delete("DELETE from user where uid > 0")
    void deleteAll();

    //查询操作
    @GT.Hibernate.GT_Query("SELECT * from UserBean")
    GT.Hibernate.Call<List<UserBean>> queryAll();


    //修改操作
    @GT.Hibernate.GT_Update
    void update(UserBean user);

}
