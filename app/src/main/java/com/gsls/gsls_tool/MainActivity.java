package com.gsls.gsls_tool;

import android.os.Bundle;
import android.widget.ImageView;

import com.gsls.gt.GT;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends GT.GT_Activity.AnnotationActivity {

    @GT.Annotations.GT_View(R.id.iv)
    private ImageView iv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

    }


}


