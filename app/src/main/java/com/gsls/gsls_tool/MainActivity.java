package com.gsls.gsls_tool;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.gsls.gt.GT;

@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends GT.GT_Activity.AnnotationActivity {

    String gifImg = "https://s1.chu0.com/src/img/gif/60/" +
            "606e2efad8ea4417a4e101fa1285d609.gif" +
            "?e=1735488000&token=1srnZGLKZ0Aqlz6dk7yF4SkiYf4eP-" +
            "YrEOdM1sob:IA5gbzlKc-NNfpArFhy-5xGKjUg=";

    @SuppressLint("ResourceType")
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        GTImageView gtIv = findViewById(R.id.gtId);
        gtIv.setGifResource(gifImg);

    }


}