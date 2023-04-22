package com.gsls.gsls_tool;

import android.content.ContentValues;

import com.gsls.gsls_tool.bean.UserBean;
import com.gsls.gt.GT;

import java.util.List;

//TODO 这里整个都可以当做教学素材 api，千万别删除
public interface DaoAPI {

    //插入操作
    @GT.Hibernate.GT_Insert
    GT.Hibernate.Call<UserBean> insert(UserBean userBean);

    @GT.Hibernate.GT_Insert
    GT.Hibernate.Call<UserBean[]> inserts(UserBean... userBeans);

    @GT.Hibernate.GT_Insert
    GT.Hibernate.Call<List<UserBean>> insertAll(List<UserBean> list);

    //删除操作
    @GT.Hibernate.GT_Delete(where = "name = ? and age = ?")
    GT.Hibernate.Call<UserBean> delete(String name, int age);

    @GT.Hibernate.GT_Delete(where = "userId = ?")
    GT.Hibernate.Call<UserBean> delete(int userId);

    @GT.Hibernate.GT_Delete
    GT.Hibernate.Call<UserBean> delete(UserBean userBean);

    @GT.Hibernate.GT_Delete
    GT.Hibernate.Call<UserBean> deleteAll();


    //查询操作
    @GT.Hibernate.GT_Query(where = "userId = ? ")
    GT.Hibernate.Call<UserBean> query(int userId);

    @GT.Hibernate.GT_Query(where = "sex = ? and age = ?")
    GT.Hibernate.Call<UserBean> query(String sex, int age);

    @GT.Hibernate.GT_Query
    GT.Hibernate.Call<UserBean> query(GT.Hibernate.WhereBean whereBean);

    @GT.Hibernate.GT_Query
    GT.Hibernate.Call<List<UserBean>> queryAll();

    @GT.Hibernate.GT_Query(where = "sex = ? and age > ?")
    GT.Hibernate.Call<List<UserBean>> queryAll(String sex, int age);

    //修改操作
    @GT.Hibernate.GT_Update(where = "age = ?")
    GT.Hibernate.Call<UserBean> update(int age, ContentValues contentValues);

    @GT.Hibernate.GT_Update
    GT.Hibernate.Call<UserBean> update(UserBean userBean);


    //自定义 SQL 代码操作,?问号 的顺序必须对应 形参里的顺序
    @GT.Hibernate.GT_Code("SELECT name, age FROM UserBean WHERE name LIKE '%?%'")
    GT.Hibernate.Call<List<UserBean>> query(String name);

    @GT.Hibernate.GT_Code("DELETE FROM UserBean WHERE name = '?'")
    GT.Hibernate.Call<UserBean> delete(String name);

    @GT.Hibernate.GT_Code("UPDATE UserBean SET name = '?' ,age = ?, sex = '?' WHERE userId = ?")
    GT.Hibernate.Call<UserBean> update(String name, int age, String sex, int userId);

    @GT.Hibernate.GT_Code("INSERT INTO UserBean VALUES (?, ?, '?', '?')")
    GT.Hibernate.Call<List<UserBean>> save(int userId, int age, String name, String sex);

}
