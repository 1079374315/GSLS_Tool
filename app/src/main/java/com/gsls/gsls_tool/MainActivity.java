package com.gsls.gsls_tool;

import android.os.Bundle;

import com.gsls.gsls_tool.bean.UserBean;
import com.gsls.gt.GT;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends GT.GT_Activity.AnnotationActivity {

    @GT.Hibernate.Build(setSqlVersion = 1)
    private static GT.Hibernate hibernate;


    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        GT.logt("表字段:" + hibernate.getTableAllValue(UserBean.class));

//        UserBean userBean = new UserBean();
//        userBean.setSex("男");
//        userBean.setAge(22);
//        userBean.setName("小明");
//        hibernate.save(userBean);

        GT.logt("表所有数据:" + hibernate.queryAll(UserBean.class));


       /* GT.Thread.getInstance(0).execute(new Runnable() {
            @Override
            public void run() {
                GT.logt("初始化initView");
                DaoAPI daoAPI = GT.Hibernate.create(DaoAPI.class, hibernate);
                GT.logt("初始化 数据前");
                List<UserBean> list = new ArrayList<>();
                for (int i = 0; i < 100; i++) {//TODO 50万 OK,需要支持到 无限大(最小1亿)
                    UserBean userBean = new UserBean(GT.GT_Random.getName(GT.GT_Random.getInt(2, 4)), GT.GT_Random.getInt(0, 100), GT.GT_Random.getInt() % 2 == 0 ? "男" : "女");
                    list.add(userBean);
                }
                GT.logt("初始化 数据后,开始保存");
                daoAPI.insertAll(list).newCall(new GT.Hibernate.Callback<List<UserBean>>() {
                    @Override
                    public void onSuccess(List<UserBean> userBeans, GT.Hibernate.Call<List<UserBean>> call) {
                        super.onSuccess(userBeans, call);
                        GT.logt("保存成功:" + userBeans.size() + "个数据");
                        GT.logt("总数据量:" + hibernate.count(UserBean.class));
                    }
                    @Override
                    public void onError(GT.Hibernate.Call<List<UserBean>> call, Exception e) {
                        super.onError(call, e);
                        GT.logt("保存失败:" + call);
                    }
                });

                GT.logt("到达代码尾部");

            }
        });*/
    }


}