package com.gsls.gsls_tool;

import android.os.Bundle;
import android.view.View;

import com.gsls.gt.GT;
import com.gsls.gt_databinding.annotation.GT_DataBinding;

@GT_DataBinding(setLayout = "activity_main", setBindingType = GT_DataBinding.Activity)
@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends MainActivityBinding {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GT.logt("单击");

            }
        });
    }


}


