package com.gsls.gsls_tool;

import com.gsls.gsls_tool.bean.JsonBean;
import com.gsls.gsls_tool.bean.JsonRootBean;
import com.gsls.gt.GT;
import com.gsls.gt_databinding.annotation.GT_HttpCallBuild;


@GT_HttpCallBuild
@GT.HttpCall.URL("https://apis.map.qq.com/")
public interface HttpApi {

    @GT.HttpCall.GET("ws/geocoder/v1/")
    GT.HttpCall.Call<JsonRootBean> getLocation(String location, String key, String get_poi);

    @GT.HttpCall.GET("ws/geocoder/v1")
    GT.HttpCall.Call<JsonBean> getCode(String key);

}
