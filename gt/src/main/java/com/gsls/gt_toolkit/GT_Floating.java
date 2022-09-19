package com.gsls.gt_toolkit;


import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.gsls.gt.GT;
import com.gsls.gt.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动代码：
 * GT_Floating.setType_screenType(-1);
 * startFloatingWindow(GT_Floating.class);
 * <p>
 * 其中可以把这个悬浮窗作为 GT库 的调试日志工具
 * 在启动前添加这个代码后
 * GT_Floating.listApp.add(new AppBean("王者荣耀","https://img0.baidu.com/it/u=323919098,4072948715&fm=253&fmt=auto&app=120&f=JPEG?w=800&h=800","wzry"));
 * GT_Floating.listApp.add(new AppBean("王者荣丫",null,"wzry2"));
 * <p>
 * 再在注册的订阅者的类中写上这样,就可以监听但单击该 测试1 的方法了
 *
 * @GT.EventBus.Subscribe public void wzry(){
 * GT.logt("触发了!");
 * }
 * <p>
 * 还可以写上 getAppName 或 getAppPack 方法，用于监听点击安卓手机已安装应用，如果返回类型为 true 则直接打开该已安装应用
 * <p>
 */
public class GT_Floating extends GT.GT_FloatingWindow.BaseFloatingWindow implements View.OnClickListener {
    //组件加载
    protected TextView tv_shutdown;//开关机提示
    private View cl_close;//开关机整体页面
    protected CheckBox cb_expansion;//放大缩小按钮
    private View cl_title;//缩小状态栏
    private View ll_bottom;//下面按钮
    private View ll_StatusBar_titleAll;//展开状态栏
    private ScrollView sv_StatusBar_titleData;//状态栏最顶端容器
    private View ll_statusBar_set;//状态栏内装功能的容器
    private View view_bg_floating_title;//展开状态栏后显示的背景黑色
    private TextView tv_statusBar_message;//状态栏内的消息提示
    private SeekBar sb_diaphaneity;//状态栏内的透明度调节拖动条
    private SeekBar sb_width;//宽度拖动条
    private SeekBar sb_height;//高度拖动条
    private GT.ViewUtils.FlowLayout flowLayout;//装APP容器
    private View sv;//APP图标滑动组件
    private SearchView sv_find;//搜索组件
    private ViewGroup fl_main; //界面容器
    private ImageView iv_fillTop;//全屏最顶部

    //创建内置APP工具
    private UtilsGTApp utilsGTApp = new UtilsGTApp();
    //创建工具
    private Utils utils = new Utils();
    //创建动画库
    public final GT.GT_Animation animation = new GT.GT_Animation();

    //内置 GT库 APP
    private final String[] GTApp = {
            "LOG", "SQL", "F栈",
    };

    //外置设置的app key = app名称  value = 单击后发布事件的方法
    public final static List<AppBean> listApp = new ArrayList<>();

    //用于存储本次加载的APP
    private final Map<String, View> viewMap = new HashMap<>();
    //记录原始的宽高,用于重置初始化
    private int originalWidth = 0;
    private int originalHeight = 0;

    @GT.EventBus.Subscribe(threadMode = GT.EventBus.ThreadMode.MAIN)
    public void logAllData(String msg) {
        utilsGTApp.addLog(msg);
    }

    @Override
    protected int loadLayout() {
        return R.layout.floating_main;//加载布局
    }

    @Override
    protected void initView(View view) {
        setDrag(true);//设置可拖动
        GT.EventBus.getDefault().register(this);//绑定订阅者

        //组件初始化
        tv_shutdown = view.findViewById(R.id.tv_shutdown);
        cl_close = view.findViewById(R.id.cl_close);
        cb_expansion = view.findViewById(R.id.cb_expansion);
        cl_title = view.findViewById(R.id.cl_title);
        ll_bottom = view.findViewById(R.id.ll_bottom);
        ll_StatusBar_titleAll = view.findViewById(R.id.ll_StatusBar_titleAll);
        sv_StatusBar_titleData = view.findViewById(R.id.sv_StatusBar_titleData);
        ll_statusBar_set = view.findViewById(R.id.ll_statusBar_set);
        view_bg_floating_title = view.findViewById(R.id.view_bg_floating_title);
        tv_statusBar_message = view.findViewById(R.id.tv_statusBar_message);
        sb_diaphaneity = view.findViewById(R.id.sb_diaphaneity);
        sb_width = view.findViewById(R.id.sb_width);
        sb_height = view.findViewById(R.id.sb_height);
        flowLayout = view.findViewById(R.id.flowLayout);
        sv = view.findViewById(R.id.sv);
        sv_find = view.findViewById(R.id.sv_find);
        fl_main = view.findViewById(R.id.fl_main);
        iv_fillTop = view.findViewById(R.id.iv_fillTop);

        //组件单击事件注册
        int[] viewIds = {
                R.id.tv_hide, R.id.cb_expansion, R.id.tv_close, R.id.cl_title, //上面
                R.id.tv_statusBar_message, R.id.iv_cut, R.id.iv_reset, R.id.iv_fill, R.id.iv_fillTop, //状态栏内的
                R.id.tv_back, R.id.tv_home, R.id.tv_task,       //下面
        };

        //遍历注册单击事件
        for (int viewId : viewIds) {
            onClickView(view.findViewById(viewId));
        }

        utils.loadApp();//加载安装系统内部APP
        //记录原始宽高
        originalWidth = getLayoutParams().width;
        originalHeight = getLayoutParams().height;

    }

    @SuppressLint({"NewApi", "UseCompatLoadingForDrawables"})
    @Override
    public void loadData(Context context, Intent intent, View view) {
        super.loadData(context, intent, view);
        registerReceiver(utils.mBroadcastReceiver, new IntentFilter(Utils.ACTION));//单击事件 注册广播
        utils.setTitleListener(sv_find); //设置状态栏事件
        animation.translateY_T(0, -getHeight(), 1, 0, false, ll_StatusBar_titleAll);//状态栏默认收起来
        utils.setStatusBarShrinkListener(tv_statusBar_message, true);//设置状态栏内部上滑收缩事件
        utils.setStatusBarShrinkListener(sv_StatusBar_titleData, false);//设置状态栏内部上滑收缩事件
        utils.setStatusBarDiaphaneityDrag(sb_diaphaneity);//设置透明度拖动事件

        //创建屏幕拖动条参数
        sb_width.setMax(getWidth());//设置宽度拖动最大值
        sb_width.setProgress(getLayoutParams().width);//设置宽度拖动当前值
        sb_height.setMax(getHeight());//设置高度拖动最大值
        sb_height.setProgress(getLayoutParams().height);//设置高度拖动当前值
        utils.setStatusBarWidthDrag(sb_width);//设置状态栏宽度拖动事件
        utils.setStatusBarHeightDrag(sb_height);//设置状态栏高度拖动事件

        cl_close.setOnTouchListener(new FloatingOnTouchListener_Close());//开关机界面可拖动界面
        utils.setSearchEvent(sv_find);//设置搜索框事件
        flowLayout.removeAllViews();//清空所有组件
        fl_main.removeAllViews();//清空所有组件

        //初始化GT内部App
        for (int i = 0; i < GTApp.length; i++) {
            View item_app_ico = LayoutInflater.from(this).inflate(R.layout.item_app_ico, null);
            TextView tv_appName = item_app_ico.findViewById(R.id.tv_appName);
            ImageView iv_icon = item_app_ico.findViewById(R.id.iv_icon);
            iv_icon.setImageDrawable(getDrawable(R.mipmap.gt_logo));
            tv_appName.setText(GTApp[i]);
            flowLayout.addView(item_app_ico);//添加APP
            item_app_ico.setOnClickListener(this);
            //将APP存储到容器中
            viewMap.put(tv_appName.getText().toString(), item_app_ico);
        }

        //初始化外部App
        for (AppBean appBean : listApp) {
            View item_app_ico = LayoutInflater.from(this).inflate(R.layout.item_app_ico, null);
            TextView tv_appName = item_app_ico.findViewById(R.id.tv_appName);
            ImageView iv_icon = item_app_ico.findViewById(R.id.iv_icon);
//            GT.Glide.with(this).load(appBean.appIcon != null ? appBean.appIcon : R.mipmap.gt_logo).into(iv_icon);
            tv_appName.setText(appBean.name);
            flowLayout.addView(item_app_ico);//添加APP
            item_app_ico.setOnClickListener(view1 -> GT.EventBus.getDefault().post(null, appBean.function));
            //将APP存储到容器中
            viewMap.put(tv_appName.getText().toString(), item_app_ico);
        }
    }

    //内置APP功能
    public void onClick(View v) {
        TextView tv_appName = v.findViewById(R.id.tv_appName);
        String appName = tv_appName.getText().toString();
//        GT.log("单击了:" + appName);
        switch (appName) {
            case "LOG":
                View view = utilsGTApp.initLog();
                utils.setDoubleClickFullScreen(view);//设置双击全屏
                fl_main.addView(view);
                break;
            case "SQL":
                view = utilsGTApp.initSQL();
                utils.setDoubleClickFullScreen(view);//设置双击全屏
                fl_main.addView(view);
                break;
            case "F栈":
                view = utilsGTApp.initFStack();
                utils.setDoubleClickFullScreen(view);//设置双击全屏
                fl_main.addView(view);
                break;
            default:

                break;
        }
    }

    //小手机事件
    public void onClickView(View view) {

        if (view == null) return;

        view.setOnClickListener(v -> {
            int id = v.getId();//上面
            //充满全屏
            if (id == R.id.cl_title) {//状态栏
                if (utils.operationType == 2) return;//屏蔽掉长按事件的冲突
                utils.isUnfold = true;
                utils.setStatusBarBgHide(true);
                animation.translateY_T(-getHeight(), 0, 500, 0, false, ll_StatusBar_titleAll);
//                GT.log("单击 状态栏");
            } else if (id == R.id.tv_hide) {//隐藏按钮
                utils.sendCustomViewNotification();//发送通知
                hide();//隐藏GT小白
            } else if (id == R.id.cb_expansion) {//切换屏幕大小
                utils.changeSize();
            } else if (id == R.id.tv_close) {//关机
                utils.shutdown(false, 100);//关机且设置持续时间

                //状态栏内
            } else if (id == R.id.tv_statusBar_message) {//状态栏内的消息提示
                animation.translateY_T(0, -getHeight(), 500, 0, false, ll_StatusBar_titleAll);
                utils.setStatusBarBgHide(false);
                utils.isUnfold = false;
                tv_statusBar_message.setText("暂无通知");
//                GT.log("单击 暂无通知");
            } else if (id == R.id.iv_cut) {//切换竖屏横屏
//                GT.log("切换");
                int tmp_w = getLayoutParams().width;
                int tmp_h = getLayoutParams().height;
                getLayoutParams().width = tmp_h;
                getLayoutParams().height = tmp_w;
                updateView();
            } else if (id == R.id.iv_reset) {//重置窗体属性
                sb_diaphaneity.setProgress(0);
                sb_width.setProgress(originalWidth);
                sb_height.setProgress(originalHeight);
                view.setAlpha(1);
                getLayoutParams().width = originalWidth;
                getLayoutParams().height = originalHeight;
                updateView();
            } else if (id == R.id.iv_fill || id == R.id.iv_fillTop) {//执行双击全屏操作
                utils.isFullScreen = !utils.isFullScreen;
                utils.setIsFullScreen(utils.isFullScreen);

                //下面三个按钮
            } else if (id == R.id.tv_back) {
                GT.toast(context, "返回上级");
                if (utils.isUnfold) {
//                    GT.log("进入返回上级");
                    utils.isUnfold = false;
                    utils.setStatusBarBgHide(false);
                    animation.translateY_T(0, -getHeight(), 500, 0, false, ll_StatusBar_titleAll);
                    return;
                }

                if (utilsGTApp.hierarchyNumber > 0) {
                    utilsGTApp.hierarchyNumber--;
                }

                //退出栈顶界面
                onBackPressed();
            } else if (id == R.id.tv_home) {
                GT.toast(context, "返回桌面");
                utils.backAllFrame();//退出所有内置APP
            } else if (id == R.id.tv_task) {
                GT.toast(context, "浏览任务");
                for (int i = 0; i < fl_main.getChildCount(); i++) {
                    View childAt = fl_main.getChildAt(i);
//                    GT.log("TAG:" + childAt);
                }
            }

        });


    }

    //设置开关机界面的窗体拖动事件
    public class FloatingOnTouchListener_Close implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    getLayoutParams().x = getLayoutParams().x + movedX;
                    getLayoutParams().y = getLayoutParams().y + movedY;
                    updateView();
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    class Utils {

        //记录旧的宽高
        protected int oldWidth;
        protected int oldHeight;
        //发送通知栏
        public static final String ACTION = "召唤";    //动作1
        public static final int NOTIFICATION_ID = 0x1079;    //通知ID
        //状态栏下滑展开
        private float x, oldX;  //记录位置X
        private float y, oldY;  //记录位置Y
        private final int slidingRate = 50;//所有手势滑动的速率
        private int operationType = -1;//默认(无事件)：-1、滑动：0、下拉状态栏：1、长按事件：2
        private float pullY = 0;//下拉Y位置
        private float alphaBG = 0.00f;//下拉时具体显示渐变色
        private boolean isUnfold = false;//状态栏是否展开
        private float topMax = 0;//拉到底部后的最大值
        private float bgTransparencyValue = 0.8f;//状态栏背景透明度
        //状态栏中下拉与上提的返回类型
        private boolean returnType = false;//如果按下位置与抬起位置是一致的，那就判定为单击返回 false,否则返回 true 滑动事件
        private boolean isFullScreen = false;//是否全屏
        //实现双击功能的参数
        private long DOUBLE_TIME = 300;
        private long lastClickTime = 0;
        //下面三个按钮是否需要触发
        public boolean isBottom = false;//默认是需要去触发的，如果设置喂true,那就说明是触摸消耗了事件

        public Utils() {
        }

        //提示吐司
        public void toast(Object msg) {
            GT.Thread.runAndroid(new Runnable() {
                @Override
                public void run() {
                    GT.toast(context, String.valueOf(msg));
                }
            });
        }

        //切换屏幕大小
        private void changeSize() {
            if (cb_expansion.isChecked()) {
                //记录原始大小
                oldWidth = getLayoutParams().width;
                oldHeight = getLayoutParams().height;
                //设置界面最大
                getLayoutParams().width = getWidth();
                getLayoutParams().height = getHeight();
            } else {
                //还原原始大小
                getLayoutParams().width = oldWidth;
                getLayoutParams().height = oldHeight;
            }
            updateView();//更新界面
        }

        //开关机
        private void shutdown(boolean startOrClose, int time) {
            GT.Thread.runJava(new Runnable() {
                @Override
                public void run() {
                    GT.Thread.runAndroid(new Runnable() {
                        @Override
                        public void run() {
                            cl_close.setVisibility(View.VISIBLE);//显示开关机组件
                            if (startOrClose) {
                                tv_shutdown.setText("开机");
                            } else {
                                tv_shutdown.setText("关机");

                            }
                        }
                    });
                    GT.Thread.sleep(time);
                    GT.Thread.runAndroid(new Runnable() {
                        @Override
                        public void run() {
                            if (startOrClose) {
                                cl_close.setVisibility(View.GONE);//隐藏开关机组件
                            } else {
                                finish();
                            }

                        }
                    });

                }
            });
        }

        //发送通知栏
        private NotificationManagerCompat notificationManagerCompat;

        public void sendCustomViewNotification() {

            //加载自定义布局
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.item_notification);//普通 normal_notification 用到的视图

            //直接更新 通知栏上的 UI 组件方法 ，但并不能进行监听
//        remoteViews.setTextViewText(R.id.tv_data, "点击召唤小白");//更新 textView 的内容
//        remoteViews.setImageViewResource(R.id.iv_head, R.mipmap.gt_logo);

            //这下面则是发送通知的
            String channelId = createNotificationChannel(getApplicationContext());//创建Notification Channel
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);//创建Notification并与Channel关联

            builder.setSmallIcon(R.mipmap.gt_logo);
            builder.setTicker("GT-小白");
            builder.setOngoing(true);
            builder.setAutoCancel(true);//跳转后消失
            builder.setContent(remoteViews);//设置普通notification视图 将自定义布局设置为 通知栏的布局
            builder.setCustomBigContentView(remoteViews);//设置显示bigView的notification视图
            builder.setPriority(NotificationCompat.PRIORITY_MAX);//设置最大优先级


            //单击后跳转回 主页面
//            Intent intent = new Intent(context, MainActivity.class);
//            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//            builder.setContentIntent(pi);//设置通知栏 点击跳转

            //设置单击后发布的广播
            Intent intent_music_left = new Intent("召唤");
            PendingIntent pendingIntent_music_left = PendingIntent.getBroadcast(context, 0, intent_music_left, PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.tv_data, pendingIntent_music_left);
            remoteViews.setOnClickPendingIntent(R.id.iv_head, pendingIntent_music_left);

            //创建一个启动详细页面的 Intent 对象
//            Intent[] intent = new Intent[]{new Intent(GT_Floating.this, MainActivity.class)};
//            PendingIntent pi = PendingIntent.getActivities(context,0,intent,0);
//            builder.setContentIntent(pi);//设置通知栏 点击跳转

            //发布通知
            notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

        }

        private String createNotificationChannel(Context context) {
            // O (API 26)及以上版本的通知需要NotificationChannels。
            if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 初始化NotificationChannel。
                NotificationChannel notificationChannel = new NotificationChannel("com.gsls.king", "GT-应用", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("GT 小白等待您的召唤");
                // 向系统添加 NotificationChannel。试图创建现有通知
                // 通道的初始值不执行任何操作，因此可以安全地执行
                // 启动顺序
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(notificationChannel);
                return "com.gsls.king";
            } else {
                return null; // 为pre-O(26)设备返回 null
            }
        }

        // 声明一个广播用于接受单击状态栏
        public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION)) { //弹出广播
                    show();//显示GT小白
                    notificationManagerCompat.cancel(NOTIFICATION_ID);//清除当前 id 为 NOTIFICATION_ID 的通知
                }
            }
        };


        //设置状态栏下滑展开
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("ClickableViewAccessibility")
        public void setTitleListener(View cl_view) {

            cl_view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    GT.log("开始锁定事件");
                    if (operationType == 0) return false;//如果是正在拖动，那就拦截长按锁定功能
                    if (isDrag()) {
                        utils.toast("已锁定");
                        setDrag(false);
                    } else {
                        utils.toast("已解锁");
                        setDrag(true);
                    }
                    operationType = 2;//赋值为长按事件
                    return false;
                }
            });

            //设置状态栏下拉、上滑、左滑、右滑事件
            cl_view.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下
                        //如果展开状态没有显示那就显示
                        if (ll_StatusBar_titleAll.getVisibility() != View.VISIBLE) {
                            ll_StatusBar_titleAll.setVisibility(View.VISIBLE);//显示状态栏
                        }
                        if (view_bg_floating_title.getVisibility() != View.VISIBLE) {
                            view_bg_floating_title.setVisibility(View.VISIBLE);//显示状态栏渐变色
                        }
                        oldX = (int) event.getRawX();
                        oldY = (int) event.getRawY();
                        //初始化
                        alphaBG = 0f;
                        operationType = -1;
                        topMax = -1;
//                        GT.log("按下 X:" + oldX + " Y:" + oldY);
                        break;
                    case MotionEvent.ACTION_UP://抬起
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();

                        if (oldX == x && oldY == y) {
                            returnType = false;
                            cl_title.performClick();//多加一个点击事件
                        } else {
                            returnType = true;
                        }
//                        GT.log("抬起1:" + returnType + " X:" + x + " Y:" + y);

                        //如果之前做的是下拉动作
                        if (operationType == 1) {
//                            GT.log("ll_StatusBar_titleAll.getY():" + ll_StatusBar_titleAll.getY());
                            if (ll_StatusBar_titleAll.getY() == topMax) {//下拉到顶了
                                isUnfold = true;
                            } else if (event.getY() >= (getLayoutParams().height / 4)) {//下拉到展开状态，进行展开操作
                                animation.translateY_T(pullY, 0, 500, 0, false, ll_StatusBar_titleAll);
                                isUnfold = true;
                            } else {//未下拉到展开状态，进行收缩回去
                                animation.translateY_T(pullY, -getHeight() - pullY, 500, 0, false, ll_StatusBar_titleAll);
                                isUnfold = false;
                                setStatusBarBgHide(false);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = (int) (nowX - x);
                        int movedY = (int) (nowY - y);
                        x = nowX;
                        y = nowY;
//                            GT.log("滑动X:" + movedX + " Y:" + movedY);
                        if ((x - oldX >= slidingRate || x - oldX <= Math.negateExact(slidingRate)) && (operationType == -1 || operationType == 0) && isDrag()) {
                            getLayoutParams().x = getLayoutParams().x + movedX;
                            getLayoutParams().y = getLayoutParams().y + movedY;
                            updateView();
                            operationType = 0;//赋值为滑动
                        } else {
                            if (y - oldY >= slidingRate && (operationType == -1 || operationType == 1)) {
                                //下拉状态栏逻辑
                                if (event.getY() <= (getLayoutParams().height / 2) - (slidingRate / 2)) {//设置下拉限度
                                    pullY = event.getY() - (getLayoutParams().height / 2);
                                    animation.translateY_T(pullY, pullY, 10, 0, false, ll_StatusBar_titleAll);
                                    //下拉渐变色
                                    alphaBG += movedY * 0.005;
                                    if (alphaBG > bgTransparencyValue) {//状态栏背景色显示限制
                                        alphaBG = bgTransparencyValue;
                                    }
                                    view_bg_floating_title.setAlpha(alphaBG);
                                } else {//拉到顶了
                                    topMax = ll_StatusBar_titleAll.getY();
                                    animation.translateY_T(pullY, 0, 0, 0, false, ll_StatusBar_titleAll);
                                    view_bg_floating_title.setAlpha(bgTransparencyValue);
                                }
                                operationType = 1;
                            } else if (y - oldY <= Math.negateExact(slidingRate) && (operationType == -1 || operationType == 0) && isDrag()) {
                                getLayoutParams().x = getLayoutParams().x + movedX;
                                getLayoutParams().y = getLayoutParams().y + movedY;
                                updateView();
                                operationType = 0;//赋值为滑动
                            }
                        }
                        break;
                    default:
                        break;
                }
//                GT.log("抬起1返回:" + returnType);
                return returnType;
            });
        }

        //设置点击无法穿透
        public View setNotClickPenetrate(View view) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            return view;
        }

        //隐藏状态栏背景
        public void setStatusBarBgHide(boolean tf) {

            view_bg_floating_title.setVisibility(View.VISIBLE);

            GT.Thread.getInstance(0).execute(new Runnable() {
                @Override
                public void run() {

                    if (!tf) {
                        float value = bgTransparencyValue;
                        while (true) {
                            value -= 0.03;
                            if (value <= 0) break;
                            float finalValue = value;
                            GT.Thread.runAndroid(() -> view_bg_floating_title.setAlpha(finalValue));
                            GT.Thread.sleep(10);
                        }
                        GT.Thread.runAndroid(() -> view_bg_floating_title.setVisibility(View.GONE));
                    } else {
                        float value = 0f;
                        while (true) {
                            value += 0.03;
                            if (value >= bgTransparencyValue) break;
                            float finalValue = value;
                            GT.Thread.runAndroid(() -> view_bg_floating_title.setAlpha(finalValue));
                            GT.Thread.sleep(10);
                        }
                    }


                }
            });

        }

        //设置状态栏内部的收缩事件
        @SuppressLint("ClickableViewAccessibility")
        public void setStatusBarShrinkListener(View view, boolean isClick) {
            //设置状态栏上滑事件
            view.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下
                        oldX = (int) event.getRawX();
                        oldY = (int) event.getRawY();
//                        GT.log("按下 X:" + oldX + " Y:" + oldY);
                        break;
                    case MotionEvent.ACTION_UP://抬起
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        if (oldX == x && oldY == y) {
                            returnType = false;
                            if (isClick)
                                tv_statusBar_message.performClick();//多加一个点击事件
                        } else {
                            returnType = true;
                        }
//                        GT.log("抬起2:" + returnType + " X:" + x + " Y:" + y);
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        int nowY = (int) event.getRawY();
                        y = nowY;
                        if (oldY - nowY >= slidingRate) {
                            //直接划掉
                            isUnfold = false;
                            animation.translateY_T(0, -getHeight(), 500, 0, false, ll_StatusBar_titleAll);
                            setStatusBarBgHide(false);
                            tv_statusBar_message.setText("暂无通知");
                        }
                        break;
                    default:
                        break;
                }
//                GT.log("抬起2返回:" + returnType);
                return returnType;
            });
        }

        //设置状态栏中的 透明度拖动条
        public void setStatusBarDiaphaneityDrag(SeekBar seekBar) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    GT.log("当前进度:" + progress);
                    tv_statusBar_message.setText("透明度:" + progress + "%");
                    float v = (float) ((seekBar.getMax() - progress) * 0.01);
                    getView().setAlpha(v);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                    GT.log("开始进度:" + seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//                    GT.log("进度:" + seekBar.getProgress());
                }
            });
        }

        //设置状态栏中宽度拖动条事件
        public void setStatusBarWidthDrag(SeekBar seekBar) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tv_statusBar_message.setText("宽度:" + progress + "px");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                    GT.log("开始进度:" + seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//                    GT.log("进度:" + seekBar.getProgress());
                    getLayoutParams().width = seekBar.getProgress();
                    updateView();
                }
            });
        }

        //设置状态栏中高度拖动条事件
        public void setStatusBarHeightDrag(SeekBar seekBar) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tv_statusBar_message.setText("高度:" + progress + "px");

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                    GT.log("开始进度:" + seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//                    GT.log("进度:" + seekBar.getProgress());
                    getLayoutParams().height = seekBar.getProgress();
                    updateView();
                }
            });
        }

        //设置全图
        public void setIsFullScreen(boolean isFullScreen) {
            if (isFullScreen) {
                cl_title.setVisibility(View.GONE);
                ll_bottom.setVisibility(View.GONE);
                iv_fillTop.setVisibility(View.VISIBLE);
            } else {
                cl_title.setVisibility(View.VISIBLE);
                ll_bottom.setVisibility(View.VISIBLE);
                iv_fillTop.setVisibility(View.GONE);
            }
        }

        //设置双击全屏
        @SuppressLint("ClickableViewAccessibility")
        public View setDoubleClickFullScreen(View flowLayout) {
            flowLayout.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下
                        long currentTimeMillis = System.currentTimeMillis();
                        if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {//如果双击的时间在300毫秒之内就执行双击操作
                            //执行双击全屏操作
                            if (isFullScreen) {
                                isFullScreen = false;
                            } else {
                                isFullScreen = true;
                            }
                            setIsFullScreen(isFullScreen);
                        }
                        lastClickTime = currentTimeMillis;
                        break;
                    default:
                        break;
                }
                return true;
            });
            return flowLayout;
        }

        //加载APP
        public void loadApp() {
            cl_close.setVisibility(View.VISIBLE);//显示开关机组件
            tv_shutdown.setText("开机");

            GT.Observable.getDefault().execute(new GT.Observable.RunJava<Object>() {
                @Override
                public void run() {
                    cl_close.setVisibility(View.VISIBLE);//显示开关机组件
                    tv_shutdown.setText("开机");
                    String manufacturer = DeviceUtils.getManufacturer().toLowerCase();
                    List<AppUtils.AppInfo> appsInfo = AppUtils.getAppsInfo();//获取手机所有APP信息
                    for (int i = 0; i < appsInfo.size(); i++) {
                        AppUtils.AppInfo appInfo = appsInfo.get(i);
                        String name = appInfo.getName();
                        String packageName = appInfo.getPackageName().toLowerCase();//获取当前遍历的包名
                        if (packageName.contains("google") || packageName.contains("android") || packageName.contains(manufacturer)) {
                            continue;//过滤掉系统自带应用
                        }
//                        GT.log("packageName:" + packageName);
                        View item_app_ico = LayoutInflater.from(context).inflate(R.layout.item_app_ico, null);
                        TextView tv_appName = item_app_ico.findViewById(R.id.tv_appName);
                        ImageView iv_icon = item_app_ico.findViewById(R.id.iv_icon);
                        tv_appName.setText(name);//设置APP名称
                        iv_icon.setImageDrawable(appInfo.getIcon());
                        GT.Thread.runAndroid(() -> {
                            flowLayout.addView(item_app_ico);//添加APP
                        });

                        item_app_ico.setOnClickListener(v -> {
                            String appName = tv_appName.getText().toString();
                            String appPack = packageName.replaceAll("\\.", "_");
                            Object a = GT.EventBus.getDefault().post(appName, "getAppName");
                            Object b = GT.EventBus.getDefault().post(appPack, "getAppPack");
                            String getAppName = String.valueOf(a);
                            String getAppPack = String.valueOf(b);
                            if ("true".equals(getAppName) || "true".equals(getAppPack)) {
                                startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
                            }
                        });

                        //将APP存储到容器中
                        viewMap.put(tv_appName.getText().toString(), item_app_ico);
                    }

                }
            }).execute(new GT.Observable.RunAndroid<Object>() {
                @Override
                public void run() {
                    cl_close.setVisibility(View.GONE);//隐藏开关机组件
                }
            });

        }

        private Timer timer = new Timer();
        private final long DELAY = 1000; // in ms

        //设置搜索事件
        public void setSearchEvent(SearchView sv_find) {

            //搜索框展开时后面叉叉按钮的点击事件
            sv_find.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    getView().findViewById(R.id.tv_hide).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.cb_expansion).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.tv_close).setVisibility(View.VISIBLE);
                    setGetFocus(false);//设置取消焦点
                    return false;
                }
            });
            //搜索图标按钮(打开搜索框的按钮)的点击事件
            sv_find.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    GT.log("搜索图标按钮(打开搜索框的按钮)的点击事件");
                    getView().findViewById(R.id.tv_hide).setVisibility(View.GONE);
                    getView().findViewById(R.id.cb_expansion).setVisibility(View.GONE);
                    getView().findViewById(R.id.tv_close).setVisibility(View.GONE);
                    setGetFocus(true);//设置获取焦点

                }
            });
            //搜索框文字变化监听
            sv_find.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
//                    GT.log("TextSubmit : " + s);//点击了搜索框的搜索
                    search(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            search(s);
                        }
                    }, DELAY);


                    return false;
                }
            });

        }

        //搜索方法
        public void search(String keyValue) {
            GT.Thread.runAndroid(new Runnable() {
                @Override
                public void run() {
                    if (fl_main.getChildCount() == 0) {
                        //桌面查询事件
                        flowLayout.removeAllViews();//清空所有View
                        GT.Thread.runJava(new Runnable() {
                            @Override
                            public void run() {

                                for (String appName : viewMap.keySet()) {
                                    GT.Thread.runAndroid(() -> {
                                        if (keyValue.length() == 0) {
                                            flowLayout.addView(viewMap.get(appName));
                                        } else if (appName.toLowerCase().contains(keyValue.toLowerCase())) {
                                            flowLayout.addView(viewMap.get(appName));
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        View childAt = fl_main.getChildAt(fl_main.getChildCount() - 1);
                        int id = childAt.getId();//                                GT.log("默认的过滤");
                        if (id == R.layout.frame_log) {
                            GT.Thread.runJava(new Runnable() {
                                @Override
                                public void run() {

                                    List<String> logBeans = utilsGTApp.getLogBeans();
                                    List<String> logBeans2 = new ArrayList<>();
                                    for (String logBean : logBeans) {

                                        if (keyValue.length() != 0) {
                                            if (logBean.toLowerCase().contains(keyValue.toLowerCase())) {
                                                logBeans2.add(logBean);
                                            }
                                        } else {
                                            logBeans2.add(logBean);
                                        }
                                    }

                                    GT.Thread.runAndroid(new Runnable() {
                                        @Override
                                        public void run() {
                                            //更新数据
                                            utilsGTApp.getLogAdapter().setBeanList(logBeans2);
                                        }
                                    });

                                }
                            });
                        } else if (id == R.layout.frame_sql) {//                                GT.log("搜索进入 SQL页面:" + keyValue);
                            List<String> sqlValues = utilsGTApp.getSQLValues();
                            sqlValues.clear();

                            switch (utilsGTApp.hierarchyNumber) {
                                case 1:
//                                        GT.log("数据库过滤:" + keyValue);
                                    List<String> sqlAllNames = utilsGTApp.hibernate.getSqlAllName();//获取数据库所有名称
//                                        GT.log("sqlAllNames:" + sqlAllNames);
                                    if (sqlAllNames == null || sqlAllNames.size() == 0) {
//                                            GT.log("进入 if");
                                        sqlValues.add("暂无数据，\n请通过搜索框搜索！");
                                    } else {
//                                            GT.log("进入 else:" + keyValue.length());
//                                            GT.log("sqlValues:" + sqlValues);
                                        if (keyValue.length() == 0) {
                                            sqlValues.addAll(sqlAllNames);
                                        } else {
                                            for (String sqlName : sqlAllNames) {
                                                if (sqlName.split("\\.")[0].toLowerCase().contains(keyValue.toLowerCase())) {
//                                                        GT.log("单个添加:" + sqlName);
                                                    sqlValues.add(sqlName);
                                                }
                                            }
                                        }
                                    }
                                    utilsGTApp.getSqlAdapter().setBeanList(sqlValues);
                                    break;
                                case 2:
//                                        GT.log("表过滤:" + keyValue);
                                    List<String> sqlAllTableName = utilsGTApp.hibernate.getSqlAllTableName();
                                    if (sqlAllTableName == null || sqlAllTableName.size() == 0) {
                                        sqlValues.add("暂无数据，\n请通过搜索框搜索！");
                                    } else {
                                        if (keyValue.length() == 0) {
                                            sqlValues.addAll(sqlAllTableName);
                                        } else {
                                            for (String sqlTableName : sqlAllTableName) {
                                                if (sqlTableName.toLowerCase().contains(keyValue.toLowerCase())) {
                                                    sqlValues.add(sqlTableName);
                                                }
                                            }
                                        }
                                    }
                                    utilsGTApp.getTableAdapter().setBeanList(sqlValues);
                                    break;

                            }

                            //刷新数据
//                                GT.log("刷新适配器", sqlValues);
                        } else if (id == R.layout.frame_sql_table) {//                                GT.log("表字段过滤");
                        } else if (id == R.layout.frame_fragment_stack) {//                                GT.log("搜索进入 FStack页面:" + keyValue);
                            LinearLayout ll_fragmentStack = utilsGTApp.getLl_fragmentStack();
                            if (ll_fragmentStack != null) {
                                for (int i = 0; i < ll_fragmentStack.getChildCount(); i++) {
                                    View childAtView = ll_fragmentStack.getChildAt(i);
                                    TextView tv_showStack = childAtView.findViewById(R.id.tv_showStack);
                                    String showStackData = tv_showStack.getText().toString();
//                                        GT.log("showStackData:" + showStackData);
                                }
                            }
                        }

                    }
                }
            });
        }

        //退出栈顶界面
        public void backStackTopFrame() {
            if (fl_main.getChildCount() != 0) {
                fl_main.removeViewAt(fl_main.getChildCount() - 1);//退出栈顶的页面
            }
        }

        //退出指定的 Frame
        public void backAssignFrame(int backFrameID) {
            int childCount = fl_main.getChildCount();
            if (backFrameID == -1) {
                if (childCount != 0) {
                    fl_main.removeViewAt(0);
                }
            } else {
                for (int i = 0; i < childCount; i++) {
                    View childAt = fl_main.getChildAt(i);
                    if (childAt.getId() == backFrameID) {
                        fl_main.removeView(childAt);//退出指定的界面
                        break;
                    }

                }
            }
        }

        //退出栈顶界面
        public void backAllFrame() {
            int childCount = fl_main.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = fl_main.getChildAt(i);
                fl_main.removeView(childAt);//退出指定的界面

            }
        }

        //获取最顶端的Frame
        public View getTopFrame() {
            int childCount = fl_main.getChildCount();
            if (childCount != 0) {
                return fl_main.getChildAt(0);
            }
            return null;
        }


    }//工具方法结尾

    public class UtilsGTApp implements SQLAdapter.ClickSqlTable {

        public void addLog(String msg) {
            logBeans.add(msg);
            logAdapter.setBeanList(logBeans);//刷新数据
            if (cb_dynamicRefresh.isChecked()) {
                linearLayoutManager_log.scrollToPosition(logBeans.size() - 1);//每次滑动最底部
            }
        }

        public void emptyLog() {
            logBeans.clear();
            GT.logt("清空日志");
            logAdapter.setBeanList(logBeans);
        }

        private LogAdapter logAdapter;
        private List<String> logBeans = new ArrayList<>();
        private CheckBox cb_dynamicRefresh;
        private LinearLayoutManager linearLayoutManager_log;

        public LogAdapter getLogAdapter() {
            return logAdapter;
        }

        public void setLogAdapter(LogAdapter logAdapter) {
            this.logAdapter = logAdapter;
        }

        public List<String> getLogBeans() {
            return logBeans;
        }

        public void setLogBeans(List<String> logBeans) {
            this.logBeans = logBeans;
        }

        @SuppressLint({"ResourceType", "MissingInflatedId"})
        public View initLog() {
            View frame_log = LayoutInflater.from(context).inflate(R.layout.frame_log, null);
            frame_log.setId(R.layout.frame_log);//设置ID
            RecyclerView recyclerView = frame_log.findViewById(R.id.recyclerView);
            cb_dynamicRefresh = frame_log.findViewById(R.id.cb_dynamicRefresh);
            logAdapter = new LogAdapter(context);
            linearLayoutManager_log = logAdapter.setLinearLayoutManager_V(recyclerView);

            frame_log.findViewById(R.id.btn_empty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("GT_i", "单击");
                    emptyLog();
                }
            });
            return frame_log;
        }


        private GT.Hibernate hibernate = new GT.Hibernate();
        private List<String> sqlValues = new ArrayList<>();
        private SQLAdapter sqlAdapter;
        private SQLAdapter tableAdapter;
        private SQLiteDatabase sqLiteDatabase;
        public int hierarchyNumber = 0;

        public GT.Hibernate getHibernate() {
            return hibernate;
        }

        public void setHibernate(GT.Hibernate hibernate) {
            this.hibernate = hibernate;
        }


        public SQLAdapter getTableAdapter() {
            return tableAdapter;
        }

        public void setTableAdapter(SQLAdapter tableAdapter) {
            this.tableAdapter = tableAdapter;
        }

        public SQLAdapter getSqlAdapter() {
            return sqlAdapter;
        }

        public void setSqlAdapter(SQLAdapter sqlAdapter) {
            this.sqlAdapter = sqlAdapter;
        }

        public List<String> getSQLValues() {
            return sqlValues;
        }

        public void setSQLValues(List<String> sqlValues) {
            this.sqlValues = sqlValues;
        }

        @SuppressLint("ResourceType")
        public View initSQL() {
            //初始化UI
            View frame_sqlAll = LayoutInflater.from(context).inflate(R.layout.frame_sql, null);
            frame_sqlAll.setId(R.layout.frame_sql);
            RecyclerView recyclerView = frame_sqlAll.findViewById(R.id.recyclerView);
            sqlAdapter = new SQLAdapter(context, this);
            sqlAdapter.setLinearLayoutManager_V(recyclerView);

            if (sqlValues != null) {
                sqlValues.clear();
            }

            List<String> sqlAllNames = hibernate.getSqlAllName();//获取数据库所有名称
            if (sqlAllNames == null || sqlAllNames.size() == 0) {
                sqlValues.add("暂无数据，\n请通过搜索框搜索！");
            } else {
                sqlValues.addAll(sqlAllNames);
            }

            //刷新数据
            sqlAdapter.setBeanList(sqlValues);
            hierarchyNumber = 1;
            return frame_sqlAll;
        }

        @SuppressLint("ResourceType")
        @Override
        public void clickTable(String name) {

            if (name.contains(".db")) {
                //单击数据库
                sqLiteDatabase = context.openOrCreateDatabase(name, context.MODE_PRIVATE, null);
                hibernate.setSqLiteDatabase(sqLiteDatabase);
//                GT.log("单击的数据库名称:" + name);


                View frame_sql = LayoutInflater.from(context).inflate(R.layout.frame_sql, null);
                frame_sql.setId(R.layout.frame_sql);
                RecyclerView recyclerView = frame_sql.findViewById(R.id.recyclerView);
                tableAdapter = new SQLAdapter(context, this);
                tableAdapter.setLinearLayoutManager_V(recyclerView);
                if (sqlValues != null) {
                    sqlValues.clear();
                }

                List<String> sqlAllTableName = hibernate.getSqlAllTableName();
                if (sqlAllTableName == null || sqlAllTableName.size() == 0) {
                    sqlValues.add("暂无数据，\n请通过搜索框搜索！");
                } else {
                    sqlValues.addAll(sqlAllTableName);
                }

                //刷新数据
                tableAdapter.setBeanList(sqlValues);
                utils.setDoubleClickFullScreen(frame_sql);//设置双击全屏
                fl_main.addView(frame_sql);
                hierarchyNumber = 2;
            } else {
                //单击表
                View view = initTable(name);
                utils.setDoubleClickFullScreen(view);//设置双击全屏
                fl_main.addView(view);
            }


        }


        private TableDataAdapter tableDataAdapter;
        private List<String> tableVaues;

        //初始化 表名称
        @SuppressLint("ResourceType")
        public View initTable(String tableName) {
            //初始化UI
            View frame_sqlTitle = LayoutInflater.from(context).inflate(R.layout.frame_sql_table, null);
            frame_sqlTitle.setId(R.layout.frame_sql_table);
            LinearLayout ll_title = frame_sqlTitle.findViewById(R.id.ll_title);


            RecyclerView recyclerView = frame_sqlTitle.findViewById(R.id.recyclerView);
            tableDataAdapter = new TableDataAdapter(context);
            tableDataAdapter.setLinearLayoutManager_V(recyclerView);


            GT.Thread.getInstance(0).execute(() -> {
//                    GT.log("tableName:" + tableName);
                Cursor cursor = hibernate.query(tableName);
                if (cursor == null) return;
//                    GT.log("tableAllValues:" + cursor.getColumnCount());
                String[] tableAllValues = cursor.getColumnNames();
                if (tableAllValues == null) return;

                if (tableDataAdapter.bytesList != null) {
                    tableDataAdapter.bytesList.clear();
                }

                //初始化标题
                for (int i = 0; i < tableAllValues.length; i++) {
                    TextView item_sql_tv = (TextView) LayoutInflater.from(context).inflate(R.layout.item_sql_tv, null);
                    item_sql_tv.setText(tableAllValues[i]);
                    GT.Thread.runAndroid(new Runnable() {
                        @Override
                        public void run() {
                            tableDataAdapter.bytesList.add(item_sql_tv.getText().toString().getBytes().length);
                            ll_title.addView(item_sql_tv);
                        }
                    });
                }

                if (tableVaues == null) {
                    tableVaues = new ArrayList<>();
                } else {
                    tableVaues.clear();
                }
                //初始化内容
                GT.Thread.runJava(() -> {
                    String[] columnNames = cursor.getColumnNames();
                    StringBuffer val = new StringBuffer();
                    for (int j = 0; j < cursor.getCount(); j++) {
                        val.delete(0, val.length());//清空
                        for (int i = 0; i < columnNames.length; i++) {
                            String string = cursor.getString(cursor.getColumnIndex(columnNames[i]));
//                                    val += string + "-GT-";
                            val.append(string).append("-GT-");
//                                    val += string + "    ,    ";
                        }
//                                GT.log("val:" + val);
                        tableVaues.add(val.toString());
                        cursor.moveToNext();//移动到下一位
                    }
//                            GT.log("总共有：" + tableVaues.size());
                    GT.Thread.runAndroid(new Runnable() {
                        @Override
                        public void run() {
                            tableDataAdapter.setBeanList(tableVaues);
                        }
                    });

                });


                hierarchyNumber = 3;
            });

            return frame_sqlTitle;
        }


        private LinearLayout ll_fragmentStack;

        public LinearLayout getLl_fragmentStack() {
            return ll_fragmentStack;
        }

        public void setLl_fragmentStack(LinearLayout ll_fragmentStack) {
            this.ll_fragmentStack = ll_fragmentStack;
        }

        @SuppressLint("ResourceType")
        public View initFStack() {
            ll_fragmentStack = null;
            fragmentMsg = "";
            View frame_f_stack = LayoutInflater.from(context).inflate(R.layout.frame_fragment_stack, null);
            frame_f_stack.setId(R.layout.frame_fragment_stack);
            ll_fragmentStack = frame_f_stack.findViewById(R.id.ll_fragmentStack);
            updateFStack();//更新栈数据
            return frame_f_stack;
        }

        private String fragmentMsg = "";

        //更新Fragment栈信息
        private void updateFStack() {
            GT.Thread.getInstance(0).execute(() -> {
                while (ll_fragmentStack != null) {
                    List<String> stackFragmentNames = GT.GT_Fragment.getGt_fragment().getStackFragmentNames();
                    //判断是否需要更新UI
                    if (fragmentMsg.equals(stackFragmentNames.toString())) {
                        GT.Thread.sleep(300);
                        continue;
                    }
                    fragmentMsg = stackFragmentNames.toString();

                    GT.Thread.runAndroid(() -> {
                        ll_fragmentStack.removeAllViews();//情况所有组件
                        Collections.reverse(stackFragmentNames);
                        if (stackFragmentNames.size() != 0) {
                            for (String stackFragmentName : stackFragmentNames) {
                                View item_fragment_stack = LayoutInflater.from(context).inflate(R.layout.item_fragment_stack, null);
                                TextView tv_showStack = item_fragment_stack.findViewById(R.id.tv_showStack);
                                tv_showStack.setText(stackFragmentName);
                                ll_fragmentStack.addView(item_fragment_stack);
                            }
                        } else {
                            View item_fragment_stack = LayoutInflater.from(context).inflate(R.layout.item_fragment_stack, null);
                            TextView tv_showStack = item_fragment_stack.findViewById(R.id.tv_showStack);
                            tv_showStack.setText("栈中无Fragment信息");
                            ll_fragmentStack.addView(item_fragment_stack);
                        }

                    });

                }
            });
        }


    }

    @Override
    protected boolean onBackPressed() {
        utils.backStackTopFrame();
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            listApp.clear();
            GT.EventBus.getDefault().unregister(this);//解绑订阅者
            unregisterReceiver(utils.mBroadcastReceiver);//解除广播
        } catch (RuntimeException e) {
//            GT.log("异常:" + e);
        }
//        System.exit(0);
    }

}
