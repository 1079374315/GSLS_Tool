package com.gsls.gsls_tool;

import android.os.Bundle;
import android.view.View;

import com.gsls.gt.GT;

//@GT_DataBinding(setLayout = "activity_main", setBindingType = GT_DataBinding.Activity)
@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends GT.GT_Activity.AnnotationActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GT.logt("单击了按钮");
            }
        });

        /*JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name","123456");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }*/

//        TestEntity testEntity = (TestEntity)JSONObject.toBean(jsonObject, TestEntity.class);

    }


}