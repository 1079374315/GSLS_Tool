package com.gsls.gt_databinding.route;

public interface ClassType {

    //已实现的
    String ACTIVITY = "android.app.Activity";
    String VIEW = "android.view.View";
    String FRAGMENT = "android.app.Fragment";
    String FRAGMENT_X = "androidx.fragment.app.Fragment";
    String DIALOG_FRAGMENT = "android.app.DialogFragment";
    String DIALOG_FRAGMENT_X = "androidx.fragment.app.DialogFragment";

    //GT库特有的
    String PROVIDER = "com.gsls.gt.GT.ARouter.IProvider";
    String INTERCEPTOR = "com.gsls.gt.GT.ARouter.Interceptor";
    String BASE_VIEW = "com.gsls.gt.GT.GT_View.BaseView";
    String FLOATING_WINDOW = "com.gsls.gt.GT.GT_FloatingWindow.BaseFloatingWindow";
    String POPUP_WINDOW = "com.gsls.gt.GT.GT_PopupWindow.BasePopupWindow";
    String NOTIFICATION = "com.gsls.gt.GT.GT_Notification.BaseNotification";
    String WEB_VIEW = "com.gsls.gt.GT.GT_WebView.BaseWebView";
    String ADAPTER = "androidx.recyclerview.widget.RecyclerView.Adapter";
    String VIEW_MODEL = "androidx.lifecycle.ViewModel";

    //待实现的
    String SERVICE = "android.app.Service";
    String CONTENT_PROVIDER = "android.app.ContentProvider";
    String UNKNOWN = "Unknown route type";
}
