package com.gsls.gsls_tool;

import com.gsls.gt.GT;
import com.gsls.gt_databinding.annotation.GT_HttpCallBuild;

@GT_HttpCallBuild
@GT.HttpCall.URL("https://apis.map.qq.com/")
public interface HttpApi {

    @GT.HttpCall.POST("ws/geocoder/v1")
    GT.HttpCall.Call<UserBean> getLocation(String location, String key, String get_poi);

    @GT.HttpCall.GET("ws/geocoder/v2")
    GT.HttpCall.Call<UserBean> getCode(String key);


    @GT.HttpCall.GET("ws/geocoder/v3")
    GT.HttpCall.Call<UserBean> getAllAppversions(String type, int userId);

}
