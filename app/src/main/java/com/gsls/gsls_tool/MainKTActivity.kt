package com.gsls.gsls_tool

import android.os.Bundle
import com.gsls.gt.GT.Annotations.GT_AnnotationActivity
import com.gsls.gt.GT.GT_Activity
import com.gsls.gtk.logt
import com.gsls.gtk.toStrings


//@GT_R_Build
@GT_AnnotationActivity(R.layout.activity_main)
class MainKTActivity : GT_Activity.AnnotationActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val map = HashMap<Any, Any>()
        map[1] = "小明"
        map["name"] = "小红"
        map[true] = "小猪"

        val list = ArrayList<String>()
        list.add("集合1")
        list.add("集合2")
        list.add("集合3")

        val userBean = UserBean(1, "小明", 24, true)

        "------------------------------------------------ 分割线 -------------------------------------".logt()
        val loginBean = LoginBean(1, "小红", false, 18, map, list, userBean)
        loginBean.toStrings().logt()


    }


}
