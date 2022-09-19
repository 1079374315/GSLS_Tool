package com.gsls.gt

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

open class GTK {

    //================================== 所有属于 GT 类的属性 =======================================
    private val gtAndroid: GT? = null //定义 GT 对象
    private val toast: Toast? = null//弱引用 Toast
    private val context: Context? = null//弱引用 Context
    private val fragmentActivity: FragmentActivity? = null//弱引用 当前动态的 上下文对象
    //================================== 提供访问 GT 属性的接口======================================

}