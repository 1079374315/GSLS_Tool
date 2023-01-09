package com.gsls.gsls_tool;

import android.os.Bundle;

import com.gsls.gt.GT;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends GT.GT_Activity.AnnotationActivity {

//    @GT.Hibernate.Build(setSqlVersion = 1)
//    private GT.Hibernate hibernate;

    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

//        HttpApi httpApi = GT.HttpCall.create(HttpApi.class);

        /*DaoAPI daoAPI = GT.Hibernate.create(DaoAPI.class, hibernate);
        daoAPI.queryAll().newCall(new GT.Hibernate.Callback<List<UserBean>>() {
            @Override
            public void onSuccess(List<UserBean> list, GT.HttpCall.Call<List<UserBean>> call) {
                GT.logt("查询成功:" + list.size());
            }

            @Override
            public void onError(GT.HttpCall.Call<List<UserBean>> call, String e) {
                GT.logt("查询失败:" + e);
            }
        });*/

//        daoAPI.update(null);

        /*List<UserBean> list = new ArrayList<>();
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