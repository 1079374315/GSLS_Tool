package com.gsls.gsls_tool;

import android.os.Bundle;

import com.gsls.gsls_tool.bean.UserBean;
import com.gsls.gt.GT;

import java.util.List;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends GT.GT_Activity.AnnotationActivity {

    @GT.Hibernate.Build(setSqlVersion = 1)
    private GT.Hibernate hibernate;

    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        /**
         *
         * TODO 思路：从尾到头，从流程尾部直接获取所有数据库数据，看缺少什么，却少么就在头部添加什么
         */

        HttpApi httpApi = GT.HttpCall.create(HttpApi.class);
        //接口1 post 请求
        /*httpApi.getLocation("22.5948,114.3069163", "J6HBZ-N3K33-D2B3V-YH7I4-37AVE-NJFMT", "1")
                .newCall(new GT.HttpCall.Callback<JsonRootBean>() {
                    @Override
                    public void onSuccess(JsonRootBean jsonRootBean, GT.HttpCall.Call<JsonRootBean> call) {
                        super.onSuccess(jsonRootBean, call);
                        GT.logt("请求成功:" + call.getData());
                    }

                    @Override
                    public void onError(GT.HttpCall.Call<JsonRootBean> call, String e) {
                        super.onError(call, e);
                        GT.logt("请求失败:" + call.getData());
                    }
                });*/



        GT.Hibernate.WhereBean whereBean = new GT.Hibernate.WhereBean();
        whereBean.setSqlWhere("sex = ?");
        whereBean.setSqlValue("女");


        DaoAPI daoAPI = GT.Hibernate.create(DaoAPI.class, hibernate);
        daoAPI.queryAll("女", 42).newCall(new GT.Hibernate.Callback<List<UserBean>>() {
            @Override
            public void onSuccess(List<UserBean> list, GT.Hibernate.Call<List<UserBean>> call) {
                super.onSuccess(list, call);
                GT.logt("查询1: " + (list == null ? null : "数据库总共有" + list.size() + "条数据"));
                if (list == null) return;
                for (UserBean userBean : list) {
                    GT.logt("查询成功:" + userBean,"");
                }
            }

            @Override
            public void onSuccess(String data, GT.Hibernate.Call call) {
                super.onSuccess(data, call);
                GT.logt("查询2:" + data);

            }
        });

//        daoAPI.update(null);

       /* List<UserBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserBean userBean = new UserBean(GT.GT_Random.getName(GT.GT_Random.getInt(2, 4)), GT.GT_Random.getInt(0, 100), GT.GT_Random.getInt() % 2 == 0 ? "男" : "女");
            list.add(userBean);
        }
        hibernate.saveAll(list);*/

//        UserBean userBean2 = new UserBean(GT.GT_Random.getName(GT.GT_Random.getInt(2, 4)), GT.GT_Random.getInt(0, 100), GT.GT_Random.getInt() % 2 == 0 ? "男" : "女");
//        hibernate.save(userBean2);


//        for (UserBean userBean : hibernate.queryAll(UserBean.class))  GT.logt(userBean);

    }


}