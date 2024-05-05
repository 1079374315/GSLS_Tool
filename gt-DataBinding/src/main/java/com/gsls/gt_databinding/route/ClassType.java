package com.gsls.gt_databinding.route;

public enum ClassType {

    //已实现的
    ACTIVITY(10, "android.app.Activity"),//activity
    VIEW(11, "android.view.View"),//自定义View
    FRAGMENT(12, "android.app.Fragment"),//fragment
    FRAGMENT_X(13, "androidx.fragment.app.Fragment"),//fragment x
    DIALOG_FRAGMENT(14, "android.app.DialogFragment"),//DialogFragment
    DIALOG_FRAGMENT_X(15, "androidx.fragment.app.DialogFragment"),//DialogFragment x

    //GT库特有的
    PROVIDER(21, "com.gsls.gt.GT.ARouter.IProvider"),//接口
    INTERCEPTOR(22, "com.gsls.gt.GT.ARouter.IInterceptor"),//拦截器
    BASE_VIEW(23, "com.gsls.gt.GT.GT_View.BaseView"),//自定义GT_View
    FLOATING_WINDOW(24, "com.gsls.gt.GT.GT_FloatingWindow.BaseFloatingWindow"),//悬浮窗
    POPUP_WINDOW(25, "com.gsls.gt.GT.GT_PopupWindow.BasePopupWindow"),//PopupWindow
    NOTIFICATION(26, "com.gsls.gt.GT.GT_Notification.BaseNotification"),//通知栏
    WEB_VIEW(27, "com.gsls.gt.GT.GT_WebView.BaseWebView"),//Web 网页
    ADAPTER(28, "androidx.recyclerview.widget.RecyclerView.Adapter"),//适配器
    VIEW_MODEL(29, "androidx.lifecycle.ViewModel"),//ViewModel



    //待实现的
    SERVICE(31, "android.app.Service"),
    CONTENT_PROVIDER(32, "android.app.ContentProvider"),
    UNKNOWN(-1, "Unknown route type");

    int id;
    String className;

    public int getId() {
        return id;
    }

    public ClassType setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ClassType setClassName(String className) {
        this.className = className;
        return this;
    }

    ClassType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public static ClassType parse(String name) {
        for (ClassType routeType : ClassType.values()) {
            if (routeType.getClassName().equals(name)) {
                return routeType;
            }
        }

        return UNKNOWN;
    }
}
