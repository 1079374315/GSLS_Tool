package com.gsls.gsls_tool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gsls.gt.GT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GT.logt("迁移成功！");
    }
}