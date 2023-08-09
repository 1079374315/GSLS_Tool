package com.gsls.gtk

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Editable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.AnimatorRes
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.gsls.gt.GT
import com.gsls.gt.GT.GT_SharedPreferences
import com.gsls.gt.GT.Glide
import com.gsls.gt.GT.LOG
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

/**
 * GT库扩展方法
 */

//********************************** 日志 扩展 ***********************************
//普通日志
fun Any.log(tag: Any = "") = GT.log(this, tag.toString())
fun Any.logt(tag: Any = "") = GT.logt(this, LOG.lineInfoIndex + 2, tag.toString())
fun Any.logs(tag: Any = "") = GT.logs(this, LOG.lineInfoIndex + 2, tag.toString())
fun Any.logAll(title: String, tag: Any = "") = GT.logAll(title, this.toString(), tag.toString())

//错误日志
fun Any.err(title: String = "") = GT.err(title, this)
fun Any.errt(tag: Any = "") = GT.errt(this, LOG.lineInfoIndex + 2, tag.toString())
fun Any.errs(tag: Any = "") = GT.errs(this, LOG.lineInfoIndex + 2, tag.toString())
fun Any.errAll(title: String, tag: Any = "") = GT.errAll(title, this.toString(), tag.toString())

//时间日志
fun startTime() = GT.GT_Date.startTime()
fun endTime(startTimes: Long = 0, isLog: Boolean = false): Long {
    val endTime = GT.GT_Date.endTime(startTimes)
    if (isLog) "time:$endTime(毫秒) : ${GT.GT_Date.secondsToTime((endTime / 1000).toInt())}".log()
    return endTime
}


//********************************** 吐丝 扩展 ***********************************
fun String.toast(context: Context, time: Int = 1500) = GT.toast_time(context, this, time)

//********************************** R资源 扩展 ***********************************

//string
fun stringGet(context: Context, @StringRes id: Int): String = GT.Res.string(context, id)
fun Context.string(@StringRes id: Int): String = GT.Res.string(this, id)
fun Activity.string(@StringRes id: Int): String = GT.Res.string(this, id)
fun Fragment.string(@StringRes id: Int): String = GT.Res.string(requireContext(), id)
fun DialogFragment.string(@StringRes id: Int): String = GT.Res.string(requireContext(), id)
fun GT.GT_FloatingWindow.BaseFloatingWindow.string(@StringRes id: Int): String =
    GT.Res.string(context, id)

fun GT.GT_PopupWindow.BasePopupWindow.string(@StringRes id: Int): String =
    GT.Res.string(context, id)

fun GT.GT_View.BaseView.string(@StringRes id: Int): String = GT.Res.string(context, id)
fun GT.GT_WebView.BaseWebView.string(@StringRes id: Int): String = GT.Res.string(context, id)
fun GT.GT_Notification.BaseNotification.string(@StringRes id: Int): String =
    GT.Res.string(context, id)

//color
fun colorGet(context: Context, @ColorRes id: Int) = GT.Res.color(context, id)
fun Context.color(@ColorRes id: Int) = GT.Res.color(this, id)
fun Activity.color(@ColorRes id: Int) = GT.Res.color(this, id)
fun Fragment.color(@ColorRes id: Int) = GT.Res.color(requireContext(), id)
fun DialogFragment.color(@ColorRes id: Int) = GT.Res.color(requireContext(), id)
fun GT.GT_FloatingWindow.BaseFloatingWindow.color(@ColorRes id: Int) = GT.Res.color(context, id)
fun GT.GT_PopupWindow.BasePopupWindow.color(@ColorRes id: Int) = GT.Res.color(context, id)
fun GT.GT_View.BaseView.color(@ColorRes id: Int) = GT.Res.color(context, id)
fun GT.GT_WebView.BaseWebView.color(@ColorRes id: Int) = GT.Res.color(context, id)
fun GT.GT_Notification.BaseNotification.color(@ColorRes id: Int) = GT.Res.color(context, id)

//drawable
fun drawableGet(context: Context, @DrawableRes id: Int): Drawable = GT.Res.drawable(context, id)
fun Context.drawable(@DrawableRes id: Int): Drawable = GT.Res.drawable(this, id)
fun Activity.drawable(@DrawableRes id: Int): Drawable = GT.Res.drawable(this, id)
fun Fragment.drawable(@DrawableRes id: Int): Drawable = GT.Res.drawable(requireContext(), id)
fun DialogFragment.drawable(@DrawableRes id: Int): Drawable = GT.Res.drawable(requireContext(), id)
fun GT.GT_FloatingWindow.BaseFloatingWindow.drawable(@DrawableRes id: Int): Drawable =
    GT.Res.drawable(context, id)

fun GT.GT_PopupWindow.BasePopupWindow.drawable(@DrawableRes id: Int): Drawable =
    GT.Res.drawable(context, id)

fun GT.GT_View.BaseView.drawable(@DrawableRes id: Int): Drawable = GT.Res.drawable(context, id)
fun GT.GT_WebView.BaseWebView.drawable(@DrawableRes id: Int): Drawable =
    GT.Res.drawable(context, id)

fun GT.GT_Notification.BaseNotification.drawable(@DrawableRes id: Int): Drawable =
    GT.Res.drawable(context, id)

//bitmap
fun bitmapGet(context: Context, @DrawableRes id: Int): Bitmap = GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(context, id))
fun Context.bitmap(@DrawableRes id: Int): Bitmap = GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(this, id))
fun Activity.bitmap(@DrawableRes id: Int): Bitmap = GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(this, id))
fun Fragment.bitmap(@DrawableRes id: Int): Bitmap = GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(requireContext(), id))
fun DialogFragment.bitmap(@DrawableRes id: Int): Bitmap = GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(requireContext(), id))
fun GT.GT_FloatingWindow.BaseFloatingWindow.bitmap(@DrawableRes id: Int): Bitmap =
    GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(context, id))

fun GT.GT_PopupWindow.BasePopupWindow.bitmap(@DrawableRes id: Int): Bitmap =
    GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(context, id))

fun GT.GT_View.BaseView.bitmap(@DrawableRes id: Int): Bitmap = GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(context, id))
fun GT.GT_WebView.BaseWebView.bitmap(@DrawableRes id: Int): Bitmap =
    GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(context, id))

fun GT.GT_Notification.BaseNotification.bitmap(@DrawableRes id: Int): Bitmap =
    GT.ImageViewTools.drawable2Bitmap(GT.Res.drawable(context, id))


//dimen
fun dimenGet(context: Context, @DimenRes id: Int) = GT.Res.dimen(context, id)
fun Context.dimen(@DimenRes id: Int) = GT.Res.dimen(this, id)
fun Activity.dimen(@DimenRes id: Int) = GT.Res.dimen(this, id)
fun Fragment.dimen(@DimenRes id: Int) = GT.Res.dimen(requireContext(), id)
fun DialogFragment.dimen(@DimenRes id: Int) = GT.Res.dimen(requireContext(), id)
fun GT.GT_FloatingWindow.BaseFloatingWindow.dimen(@DimenRes id: Int) = GT.Res.dimen(context, id)
fun GT.GT_PopupWindow.BasePopupWindow.dimen(@DimenRes id: Int) = GT.Res.dimen(context, id)
fun GT.GT_View.BaseView.dimen(@DimenRes id: Int) = GT.Res.dimen(context, id)
fun GT.GT_WebView.BaseWebView.dimen(@DimenRes id: Int) = GT.Res.dimen(context, id)
fun GT.GT_Notification.BaseNotification.dimen(@DimenRes id: Int) = GT.Res.dimen(context, id)

//animation
fun animationGet(context: Context, @AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(context, id)

fun Context.animation(@AnimatorRes id: Int): XmlResourceParser = GT.Res.animation(this, id)
fun Activity.animation(@AnimatorRes id: Int): XmlResourceParser = GT.Res.animation(this, id)
fun Fragment.animation(@AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(requireContext(), id)

fun DialogFragment.animation(@AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(requireContext(), id)

fun GT.GT_FloatingWindow.BaseFloatingWindow.animation(@AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(context, id)

fun GT.GT_PopupWindow.BasePopupWindow.animation(@AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(context, id)

fun GT.GT_View.BaseView.animation(@AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(context, id)

fun GT.GT_WebView.BaseWebView.animation(@AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(context, id)

fun GT.GT_Notification.BaseNotification.animation(@AnimatorRes id: Int): XmlResourceParser =
    GT.Res.animation(context, id)

//inflate
fun <T> inflateGet(context: Context, @LayoutRes id: Int): T = GT.Res.inflate(context, id)
fun inflateGet(context: Context, @LayoutRes id: Int): View = GT.Res.inflate(context, id)
fun <T> Context.inflate(@LayoutRes id: Int): T = GT.Res.inflate(this, id)
fun Context.inflate(@LayoutRes id: Int): View = GT.Res.inflate(this, id)
fun <T> Activity.inflate(@LayoutRes id: Int): T = GT.Res.inflate(this, id)
fun Activity.inflate(@LayoutRes id: Int): View = GT.Res.inflate(this, id)
fun <T> Fragment.inflate(@LayoutRes id: Int): T = GT.Res.inflate(requireContext(), id)
fun Fragment.inflate(@LayoutRes id: Int): View = GT.Res.inflate(requireContext(), id)
fun <T> DialogFragment.inflate(@LayoutRes id: Int): T = GT.Res.inflate(requireContext(), id)
fun DialogFragment.inflate(@LayoutRes id: Int): View = GT.Res.inflate(requireContext(), id)
fun <T> GT.GT_FloatingWindow.BaseFloatingWindow.inflate(@LayoutRes id: Int): T =
    GT.Res.inflate(context, id)

fun GT.GT_FloatingWindow.BaseFloatingWindow.inflate(@LayoutRes id: Int): View =
    GT.Res.inflate(context, id)

fun <T> GT.GT_PopupWindow.BasePopupWindow.inflate(@LayoutRes id: Int): T =
    GT.Res.inflate(context, id)

fun GT.GT_PopupWindow.BasePopupWindow.inflate(@LayoutRes id: Int): View =
    GT.Res.inflate(context, id)

fun <T> GT.GT_View.BaseView.inflate(@LayoutRes id: Int): T = GT.Res.inflate(context, id)
fun GT.GT_View.BaseView.inflate(@LayoutRes id: Int): View = GT.Res.inflate(context, id)
fun <T> GT.GT_WebView.BaseWebView.inflate(@LayoutRes id: Int): T = GT.Res.inflate(context, id)
fun GT.GT_WebView.BaseWebView.inflate(@LayoutRes id: Int): View = GT.Res.inflate(context, id)
fun <T> GT.GT_Notification.BaseNotification.inflate(@LayoutRes id: Int): T =
    GT.Res.inflate(context, id)

fun GT.GT_Notification.BaseNotification.inflate(@LayoutRes id: Int): View =
    GT.Res.inflate(context, id)

//view
fun <T> viewGet(context: Context, @IdRes id: Int): T = GT.Res.view(context, id)
fun viewGet(context: Context, @IdRes id: Int): View = GT.Res.view(context, id)
fun <T> Context.view(@IdRes id: Int): T = GT.Res.view(this, id)
fun Context.view(@IdRes id: Int): View = GT.Res.view(this, id)
fun <T> Activity.view(@IdRes id: Int): T = GT.Res.view(this, id)
fun Activity.view(@IdRes id: Int): View = GT.Res.view(this, id)
fun <T> Fragment.view(@IdRes id: Int): T = GT.Res.view(requireContext(), id)
fun Fragment.view(@IdRes id: Int): View = GT.Res.view(requireContext(), id)
fun <T> DialogFragment.view(@IdRes id: Int): T = GT.Res.view(requireContext(), id)
fun DialogFragment.view(@IdRes id: Int): View = GT.Res.view(requireContext(), id)
fun <T> GT.GT_FloatingWindow.BaseFloatingWindow.view(@IdRes id: Int): T = GT.Res.view(view, id)
fun GT.GT_FloatingWindow.BaseFloatingWindow.view(@IdRes id: Int): View = GT.Res.view(view, id)
fun <T> GT.GT_PopupWindow.BasePopupWindow.view(@IdRes id: Int): T = GT.Res.view(view, id)
fun GT.GT_PopupWindow.BasePopupWindow.view(@IdRes id: Int): View = GT.Res.view(view, id)
fun <T> GT.GT_View.BaseView.view(@IdRes id: Int): T = GT.Res.view(view, id)
fun GT.GT_View.BaseView.view(@IdRes id: Int): View = GT.Res.view(view, id)
fun <T> GT.GT_WebView.BaseWebView.view(@IdRes id: Int): T = GT.Res.view(context, id)
fun GT.GT_WebView.BaseWebView.view(@IdRes id: Int): View = GT.Res.view(context, id)

//stringArray
fun stringArrayGet(context: Context, @ArrayRes id: Int): Array<String> =
    GT.Res.stringArray(context, id)

fun Context.stringArray(@ArrayRes id: Int): Array<String> = GT.Res.stringArray(this, id)
fun Activity.stringArray(@ArrayRes id: Int): Array<String> = GT.Res.stringArray(this, id)
fun Fragment.stringArray(@ArrayRes id: Int): Array<String> =
    GT.Res.stringArray(requireContext(), id)

fun DialogFragment.stringArray(@ArrayRes id: Int): Array<String> =
    GT.Res.stringArray(requireContext(), id)

fun GT.GT_FloatingWindow.BaseFloatingWindow.stringArray(@ArrayRes id: Int): Array<String> =
    GT.Res.stringArray(context, id)

fun GT.GT_PopupWindow.BasePopupWindow.stringArray(@ArrayRes id: Int): Array<String> =
    GT.Res.stringArray(context, id)

fun GT.GT_View.BaseView.stringArray(@ArrayRes id: Int): Array<String> =
    GT.Res.stringArray(context, id)

fun GT.GT_WebView.BaseWebView.stringArray(@ArrayRes id: Int): Array<String> =
    GT.Res.stringArray(context, id)

//intArray
fun intArrayGet(context: Context, @ArrayRes id: Int): IntArray = GT.Res.intArray(context, id)
fun Context.intArray(@ArrayRes id: Int): IntArray = GT.Res.intArray(this, id)
fun Activity.intArray(@ArrayRes id: Int): IntArray = GT.Res.intArray(this, id)
fun Fragment.intArray(@ArrayRes id: Int): IntArray = GT.Res.intArray(requireContext(), id)
fun DialogFragment.intArray(@ArrayRes id: Int): IntArray = GT.Res.intArray(requireContext(), id)
fun GT.GT_FloatingWindow.BaseFloatingWindow.intArray(@ArrayRes id: Int): IntArray =
    GT.Res.intArray(context, id)

fun GT.GT_PopupWindow.BasePopupWindow.intArray(@ArrayRes id: Int): IntArray =
    GT.Res.intArray(context, id)

fun GT.GT_View.BaseView.intArray(@ArrayRes id: Int): IntArray = GT.Res.intArray(context, id)
fun GT.GT_WebView.BaseWebView.intArray(@ArrayRes id: Int): IntArray = GT.Res.intArray(context, id)

//assetLocales
fun assetLocalesGet(context: Context): Array<String> = GT.Res.assetLocales(context)
fun Context.assetLocales(): Array<String> = GT.Res.assetLocales(this)
fun Activity.assetLocales(): Array<String> = GT.Res.assetLocales(this)
fun Fragment.assetLocales(): Array<String> = GT.Res.assetLocales(requireContext())
fun DialogFragment.assetLocales(): Array<String> = GT.Res.assetLocales(requireContext())
fun GT.GT_FloatingWindow.BaseFloatingWindow.assetLocales(): Array<String> =
    GT.Res.assetLocales(context)

fun GT.GT_PopupWindow.BasePopupWindow.assetLocales(): Array<String> = GT.Res.assetLocales(context)
fun GT.GT_View.BaseView.assetLocales(): Array<String> = GT.Res.assetLocales(context)
fun GT.GT_WebView.BaseWebView.assetLocales(): Array<String> = GT.Res.assetLocales(context)


//assetLocales
fun assetsGet(context: Context, assetsName: String, onListener: GT.OnListener<String>) =
    GT.ApplicationUtils.getAssetsValue(context, assetsName, onListener)

fun Context.assets(assetsName: String, onListener: GT.OnListener<String>) =
    GT.ApplicationUtils.getAssetsValue(this, assetsName, onListener)

fun Activity.assets(assetsName: String, onListener: GT.OnListener<String>) =
    GT.ApplicationUtils.getAssetsValue(this, assetsName, onListener)

fun Fragment.assets(assetsName: String, onListener: GT.OnListener<String>) =
    GT.ApplicationUtils.getAssetsValue(requireContext(), assetsName, onListener)

fun DialogFragment.assets(assetsName: String, onListener: GT.OnListener<String>) =
    GT.ApplicationUtils.getAssetsValue(requireContext(), assetsName, onListener)

fun GT.GT_FloatingWindow.BaseFloatingWindow.assets(
    assetsName: String,
    onListener: GT.OnListener<String>
) =
    GT.ApplicationUtils.getAssetsValue(context, assetsName, onListener)

fun GT.GT_PopupWindow.BasePopupWindow.assets(
    assetsName: String,
    onListener: GT.OnListener<String>
) =
    GT.ApplicationUtils.getAssetsValue(context, assetsName, onListener)

fun GT.GT_View.BaseView.assets(assetsName: String, onListener: GT.OnListener<String>) =
    GT.ApplicationUtils.getAssetsValue(context, assetsName, onListener)

fun GT.GT_WebView.BaseWebView.assets(assetsName: String, onListener: GT.OnListener<String>) =
    GT.ApplicationUtils.getAssetsValue(context, assetsName, onListener)


//********************************** GT_SharedPreferences 扩展 ***********************************
private var sp: GT_SharedPreferences? = null

/**
 * 用户级别的SP
 */
fun Context.initSP(userID: String = packageName, commit: Boolean = true): GT_SharedPreferences? {
    if (sp == null) {
        sp = GT.GT_Cache.getSP(userID)
        if (sp == null) {
            sp = GT_SharedPreferences(this, userID, commit)
            GT.GT_Cache.putSP(userID, sp)
        }
    }
    return sp
}

fun String.saveSP(obj: Any): GT_SharedPreferences? {
    if (sp != null) sp!!.save(this, obj)
    return sp
}

fun String.deleteSP(): GT_SharedPreferences? {
    if (sp != null) sp!!.delete(this)
    return sp
}

fun <T> String.querySP(dataType: Class<T>? = null): T? {
    if (sp != null) {
        return if (dataType == null) {
            sp!!.query(this) as T
        } else {
            sp!!.query(this, dataType) as T
        }
    }
    return null
}

fun String.updataSP(key: Any): GT_SharedPreferences? {
    if (sp != null) sp!!.updata(key.toString(), this)
    return sp
}


fun clearSP(): GT_SharedPreferences? {
    if (sp != null) sp!!.clear()
    return sp
}


//********************************** JSON 扩展 ***********************************

fun <T> String.fromJson(clazz: Class<T>): T = GT.JSON.fromJson2(this, clazz)

fun Any.toJson(): String = GT.JSON.toJson2(this)

//********************************** Glide 扩展 ***********************************

fun ImageView.loadImage(
    urlImg: Any?,                //加载的图片
    placeholder: Any? = null,       //设置占位图
    error: Any? = null,             //设置错误图
    ambiguity: Float = 0f,          //设置高斯模糊度
    topLeft: Int = 0,               //设置左上圆角
    topRight: Int = 0,              //设置右上圆角
    bottomLeft: Int = 0,            //设置左下圆角
    bottomRight: Int = 0,           //设置右上圆角
    isGif: Boolean = false,         //是否动态图
    gifSpeed: Int = 16,             //动态图速度(默认是16数值，增大则变快，缩小则变慢)
    isAnimations: Boolean = true,   //是否动画
    isCaches: Boolean = true        //是否缓存
) {
    Glide.with(this)//构建初始化
        .placeholder(placeholder)//设置占位图
        .error(error)//设置错误图
        .blurTransformation(ambiguity)//设置 高斯模糊度
        .roundedCorners(topLeft, topRight, bottomLeft, bottomRight)//设置圆角
        .asGif(isGif, gifSpeed)//设置圆角
        .load(urlImg, isCaches)//设置加载的网络图,是否缓存
        .into(this, isAnimations)//设置图片控件 5
}

//加载动态图 [gif]：可直接指定为 gif
fun ImageView.loadGifImage(
    url: Any,
    placeholder: Any? = null,       //设置占位图
    error: Any? = null,             //设置错误图
    ambiguity: Float = 0f,          //设置高斯模糊度
    topLeft: Int = 0,               //设置左上圆角
    topRight: Int = 0,              //设置右上圆角
    bottomLeft: Int = 0,            //设置左下圆角
    bottomRight: Int = 0,           //设置右上圆角
    gifSpeed: Int = 16,             //动态图速度(默认是16数值，增大则变快，缩小则变慢)
    isAnimations: Boolean = true,   //是否动画
    isCaches: Boolean = true        //是否缓存
) {
    if (url is String) {
        val isGif = url.contains("gif")
        var urlImg = url
        if (isGif && urlImg.contains("[")) urlImg = urlImg.substring(0, urlImg.indexOf("["))
        loadImage(urlImg, placeholder, error, ambiguity, topLeft, topRight, bottomLeft, bottomRight, isGif, gifSpeed, isAnimations, isCaches)
    } else {
        loadImage(url, placeholder, error, ambiguity, topLeft, topRight, bottomLeft, bottomRight, false, gifSpeed, isAnimations, isCaches)
    }
}


//********************************** GT_Animation 扩展 ***********************************

private val gtAnimation: GT.GT_Animation = GT.GT_Animation()

/**
 * @param x           初始 X 位置
 * @param toX         最终 X 位置
 * @param y           初始 Y 位置
 * @param toY         最终 Y 位置
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限循环
 * @param toAndFro    是否来回播放
 * @param view        给 View 添加动画
 * @return
 * @移动动画（假）
 */
fun View.startAnimationTranslate_F(
    x: Float,               //初始 X 位置
    toX: Float,             //最终 X 位置
    y: Float,               //初始 Y 位置
    toY: Float,             //最终 Y 位置
    time: Long,             //动画持续时间
    isSaveClose: Boolean,   //是否保持动画结束时的最终状态
    runCount: Int,          //播放动画的次数 -1 表示无限循环
    toAndFro: Boolean       //是否来回播放
): GT.GT_Animation {
    gtAnimation.translate_F(x, toX, y, toY, time, isSaveClose, runCount, toAndFro, this)
    return gtAnimation
}


/**
 * @param x           初始 X 位置
 * @param toX         最终 X 位置
 * @param y           初始 Y 位置
 * @param toY         最终 Y 位置
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示为无限次数
 * @param toAndFro    是否来回播放
 * @return
 * @移动item动画（假）
 */
fun startAnimationTranslat_Item_F(
    x: Float,               //初始 X 位置
    toX: Float,             //最终 X 位置
    y: Float,               //初始 Y 位置
    toY: Float,             //最终 Y 位置
    time: Long,             //动画持续时间
    isSaveClose: Boolean,   //是否保持动画结束时的最终状态
    runCount: Int,          //播放动画的次数 -1 表示为无限次数
    toAndFro: Boolean       //是否来回播放
): Animation = gtAnimation.translat_Item_F(x, toX, y, toY, time, isSaveClose, runCount, toAndFro)


/**
 * @param x        初始 X 位置
 * @param toX      最终 X 位置
 * @param time     执行动画时间
 * @param runCount 执行动画次数
 * @param toAndFro 是否来回播放
 * @param view     给View加入动画
 * @return
 * @左右动画（真）
 */
fun View.startAnimationTranslateX_T(
    x: Float,           //初始 X 位置
    toX: Float,         //最终 X 位置
    time: Long,         //执行动画时间
    runCount: Int,      //执行动画次数
    toAndFro: Boolean   //是否来回播放
): GT.GT_Animation {
    gtAnimation.translateX_T(x, toX, time, runCount, toAndFro, this)
    return gtAnimation
}


/**
 * @param x        初始 X 位置
 * @param toX      最终 X 位置
 * @param time     执行动画时间
 * @param runCount 执行动画次数
 * @param toAndFro 是否来回播放
 * @param view     给View加入动画
 * @return
 * @左右item动画（真）
 */
fun View.getAnimationTranslateX_Item_T(
    x: Float,
    toX: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.translateX_Item_T(x, toX, time, runCount, toAndFro, this)


/**
 * @param y        初始 Y 位置
 * @param toY      最终 Y 位置
 * @param time     执行动画时间
 * @param runCount 执行动画次数
 * @param toAndFro 是否来回播放
 * @param view     给View加入动画
 * @return
 * @上下动画（真）
 */
fun View.startAnimationTranslateY_T(
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.translateY_T(y, toY, time, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param y        初始 Y 位置
 * @param toY      最终 Y 位置
 * @param time     执行动画时间
 * @param runCount 执行动画次数
 * @param toAndFro 是否来回播放
 * @param view     给View加入动画
 * @return
 * @上下item动画（真）
 */
fun View.getAnimationTranslateY_Item_T(
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.translateY_Item_T(y, toY, time, runCount, toAndFro, this)

/**
 * @param x        初始 X 坐标
 * @param toX      最终 X 坐标
 * @param y        初始 Y 坐标
 * @param toY      最终 Y 坐标
 * @param time     消耗时间
 * @param runCount 执行次数
 * @param toAndFro 是否来回播放
 * @param view     动画的View
 * @return
 * @平移动画（真）
 */
fun View.startAnimationTranslate_T(
    x: Float,
    toX: Float,
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.translate_T(x, toX, y, toY, time, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param x        初始 X 坐标
 * @param toX      最终 X 坐标
 * @param y        初始 Y 坐标
 * @param toY      最终 Y 坐标
 * @param time     消耗时间
 * @param runCount 执行次数
 * @param toAndFro 是否来回播放
 * @param view     动画的View
 * @return
 * @平移item动画（真）
 */
fun View.getAnimationTranslate_Item_T(
    x: Float,
    toX: Float,
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): AnimatorSet = gtAnimation.translate_Item_T(x, toX, y, toY, time, runCount, toAndFro, this)


/**
 * @param x           原始 X 尺寸
 * @param toX         结束 X 尺寸
 * @param y           原始 Y 尺寸
 * @param toY         结束 Y 尺寸
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @param view        给 View 添加动画
 * @return
 * @缩放动画（假）
 */
fun View.startAnimationScale_F(
    x: Float,
    toX: Float,
    y: Float,
    toY: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.scale_F(x, toX, y, toY, time, isSaveClose, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param x           原始 X 尺寸
 * @param toX         结束 X 尺寸
 * @param y           原始 Y 尺寸
 * @param toY         结束 Y 尺寸
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @return
 * @缩放item动画（假）
 */
fun View.getAnimationScales_F(
    x: Float,
    toX: Float,
    y: Float,
    toY: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): Animation = gtAnimation.scales_F(x, toX, y, toY, time, isSaveClose, runCount, toAndFro)

/**
 * @param x           原始 X 尺寸
 * @param toX         结束 X 尺寸
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @param view        给 View 添加动画
 * @return
 * @缩放动画（真）
 */
fun View.startAnimationScaleX_T(
    x: Float,
    toX: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.scaleX_T(x, toX, time, isSaveClose, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param x           原始 X 尺寸
 * @param toX         结束 X 尺寸
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @param view        给 View 添加动画
 * @return
 * @缩放item动画（真）
 */
fun View.startAnimationScaleX_item_T(
    x: Float,
    toX: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.scaleX_item_T(x, toX, time, isSaveClose, runCount, toAndFro, this)

/**
 * @param y           原始 Y 尺寸
 * @param toY         结束 Y 尺寸
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @param view        给 View 添加动画
 * @return
 * @缩放动画（真）
 */
fun View.startAnimationScaleY_T(
    y: Float,
    toY: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.scaleY_T(y, toY, time, isSaveClose, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param y           原始 Y 尺寸
 * @param toY         结束 Y 尺寸
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @param view        给 View 添加动画
 * @return
 * @缩放item动画（真）
 */
fun View.getAnimatorScaleY_item_T(
    y: Float,
    toY: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.scaleY_item_T(y, toY, time, isSaveClose, runCount, toAndFro, this)

/**
 * @param x        初始 X 坐标
 * @param toX      最终 X 坐标
 * @param y        初始 Y 坐标
 * @param toY      最终 Y 坐标
 * @param time     消耗时间
 * @param runCount 执行次数
 * @param toAndFro 是否来回播放
 * @param view     动画的View
 * @return
 * @缩放动画（真）
 */
fun View.startAnimationScale_T(
    x: Float,
    toX: Float,
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.scale_T(x, toX, y, toY, time, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param x        初始 X 坐标
 * @param toX      最终 X 坐标
 * @param y        初始 Y 坐标
 * @param toY      最终 Y 坐标
 * @param time     消耗时间
 * @param runCount 执行次数
 * @param toAndFro 是否来回播放
 * @param view     动画的View
 * @return
 * @缩放item动画（真）
 */
fun View.startAnimatorScale_item_T(
    x: Float,
    toX: Float,
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): AnimatorSet = gtAnimation.scale_item_T(x, toX, y, toY, time, runCount, toAndFro, this)


/**
 * @param degrees     View初始角度
 * @param toDegrees   View旋转角度
 * @param time        动画执行时间
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @param view        执行View动画
 * @return
 * @平面旋转动画（假）
 */
fun View.startAnimationRotate_F(
    degrees: Float,
    toDegrees: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.rotate_F(degrees, toDegrees, time, isSaveClose, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param degrees     View初始角度
 * @param toDegrees   View旋转角度
 * @param time        动画执行时间
 * @param time        动画持续时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @return
 * @平面旋转item动画（假）
 */
fun View.getAnimationRotates_F(
    degrees: Float,
    toDegrees: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): Animation = gtAnimation.rotates_F(degrees, toDegrees, time, isSaveClose, runCount, toAndFro)

/**
 * @param x        初始化 X 坐标
 * @param toX      最终的 X 坐标
 * @param time     动画耗时
 * @param runCount 动画播放次数
 * @param toAndFro 是否来回播放
 * @param view     要使用动画的View
 * @return
 * @旋转动画X（真）
 */
fun View.startAnimationRotatesX_T(
    x: Float,
    toX: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean,
): GT.GT_Animation {
    gtAnimation.rotatesX_T(x, toX, time, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param x        初始化 X 坐标
 * @param toX      最终的 X 坐标
 * @param time     动画耗时
 * @param runCount 动画播放次数
 * @param toAndFro 是否来回播放
 * @param view     要使用动画的View
 * @return
 * @旋转item动画X（真）
 */
fun View.startAnimationRotatesX_Item_T(
    x: Float,
    toX: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.rotatesX_Item_T(x, toX, time, runCount, toAndFro, this)

/**
 * @param y        初始化 Y 坐标
 * @param toY      最终的 Y 坐标
 * @param time     动画耗时
 * @param runCount 动画播放次数
 * @param toAndFro 是否来回播放
 * @param view     要使用动画的View
 * @return
 * @旋转动画Y（真）
 */
fun View.startAnimationRotatesY_T(
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.rotatesY_T(y, toY, time, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param y        初始化 Y 坐标
 * @param toY      最终的 Y 坐标
 * @param time     动画耗时
 * @param runCount 动画播放次数
 * @param toAndFro 是否来回播放
 * @param view     要使用动画的View
 * @return
 * @旋转item动画Y（真）
 */
fun View.getAnimatorRotatesY_Item_T(
    y: Float,
    toY: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.rotatesY_Item_T(y, toY, time, runCount, toAndFro, this)

/**
 * @param z        初始化 Z 坐标
 * @param toZ      最终的 Z 坐标
 * @param time     动画耗时
 * @param runCount 动画播放次数
 * @param toAndFro 是否来回播放
 * @param view     要使用动画的View
 * @return
 * @旋转动画Z（真）
 */
fun View.startAnimationRotatesZ_T(
    z: Float,
    toZ: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean,
): GT.GT_Animation {
    gtAnimation.rotatesZ_T(z, toZ, time, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param z        初始化 Z 坐标
 * @param toZ      最终的 Z 坐标
 * @param time     动画耗时
 * @param runCount 动画播放次数
 * @param toAndFro 是否来回播放
 * @param view     要使用动画的View
 * @return
 * @旋转item动画Z（真）
 */
fun View.getAnimationRotatesZ_item_T(
    z: Float,
    toZ: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.rotatesZ_item_T(z, toZ, time, runCount, toAndFro, this)

/**
 * @param alpha       初始透明度
 * @param toAlpha     最终透明度
 * @param time        动画执行时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @param view        执行View动画
 * @return
 * @透明动画（假）
 */
fun View.startAnimationAlpha_F(
    alpha: Float,
    toAlpha: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.alpha_F(alpha, toAlpha, time, isSaveClose, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param alpha       初始透明度
 * @param toAlpha     最终透明度
 * @param time        动画执行时间
 * @param isSaveClose 是否保持动画结束时的最终状态
 * @param runCount    播放动画的次数 -1 表示无限次数
 * @param toAndFro    是否来回播放
 * @return
 * @透明item动画（假）
 */
fun getAnimationAlphas_F(
    alpha: Float,
    toAlpha: Float,
    time: Long,
    isSaveClose: Boolean,
    runCount: Int,
    toAndFro: Boolean
): Animation = gtAnimation.alphas_F(alpha, toAlpha, time, isSaveClose, runCount, toAndFro)

/**
 * @param alpha    初始透明度
 * @param toAlpha  最终透明度
 * @param time     动画执行时间
 * @param runCount 播放动画的次数 -1 表示无限次数
 * @param toAndFro 是否来回播放
 * @return
 * @透明动画（真）
 */
fun View.startAnimationAlpha_T(
    alpha: Float,
    toAlpha: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): GT.GT_Animation {
    gtAnimation.alpha_T(alpha, toAlpha, time, runCount, toAndFro, this)
    return gtAnimation
}

/**
 * @param alpha    初始透明度
 * @param toAlpha  最终透明度
 * @param time     动画执行时间
 * @param runCount 播放动画的次数 -1 表示无限次数
 * @param toAndFro 是否来回播放
 * @return
 * @透明item动画（真）
 */
fun View.getAnimationAlpha_item_T(
    alpha: Float,
    toAlpha: Float,
    time: Long,
    runCount: Int,
    toAndFro: Boolean
): ObjectAnimator = gtAnimation.alpha_item_T(alpha, toAlpha, time, runCount, toAndFro, this)

/**
 * @param annotationList
 * @param view
 * @return
 * @添加组合动画（假）
 */
fun View.startAnimationSet_F(annotationList: List<Animation>): GT.GT_Animation? {
    gtAnimation.animationSet_F(annotationList, this)
    return gtAnimation
}

/**
 * @param annotationList
 * @return
 * @添加组合item动画（假）
 */
fun View.getAnimationSets_F(annotationList: List<Animation>): AnimationSet =
    gtAnimation.animationSets_F(annotationList)


//********************************** ViewUtils 扩展 ***********************************

fun EditText.delayTrigger(
    onTriggerValue: GT.ViewUtils.DelayTrigger.OnTriggerValue,
    time: Int = 250
) = GT.ViewUtils.DelayTrigger().EditText(this, time, onTriggerValue)


//********************************** 协程 扩展 ***********************************

private val mapJob: MutableMap<Any, Job> = HashMap()
private val mapMainScope: MutableMap<Any, CoroutineScope> = HashMap()

/**
 * 获取 mainScope
 */
private fun getMainScope(name: Any): CoroutineScope {
    if (mapMainScope[name] == null) mapMainScope[name] = MainScope()
    return mapMainScope[name]!!
}

//创建协程
fun <T> Any.runJava(block: suspend CoroutineScope.() -> T) =
    this.creationJob(Dispatchers.Default, block)

fun <T> Any.runJavaIO(block: suspend CoroutineScope.() -> T) =
    this.creationJob(Dispatchers.IO, block)

fun <T> Any.runAndroid(block: suspend CoroutineScope.() -> T) =
    this.creationJob(Dispatchers.Main, block)

private fun <T> Any.creationJob(
    type: CoroutineDispatcher,
    block: suspend CoroutineScope.() -> T
): Job {
    val name = LOG.getClassName(5) ?: this
    val mainScope = getMainScope(name)
    mapJob[name] = mainScope.launch(type) { block() }
    return mapJob[name]!!
}

fun Any.cancel() {
    val name = LOG.getClassName(4) ?: this
    val job = mapJob[name] ?: return
    job.cancel()
    mapJob.remove(name)
}

fun Any.cancelAll() {
    val name = LOG.getClassName(4) ?: this
    val mainScope = getMainScope(name)
    mainScope.cancel()
    mapMainScope.remove(name)
    mapJob.clear()
}

fun cancelAlls() {
    for (key in mapJob.keys) {
        val job = mapJob[key] ?: continue
        job.cancel()
    }
    for (key in mapMainScope.keys) {
        val mainScope = mapMainScope[key] ?: continue
        mainScope.cancel()
    }
    mapMainScope.clear()
    mapJob.clear()
}


//********************************** 系统方法 扩展 ***********************************

//挂起
fun sleep(millis: Long) = GT.Thread.sleep(millis)

//清除所有空格
fun String.notBlanks(): String = GT.ApplicationUtils.notBlanks(this)

//设置为 EditText 可设置的参数
fun String.editText(): Editable = Editable.Factory.getInstance().newEditable(this)

//设置 EditText 值
fun EditText.setValue(value: String) {
    text = Editable.Factory.getInstance().newEditable(value)
}

//设置 EditText 值 并 将光标设置到最后
fun EditText.setValueEnd(value: String) {
    text = Editable.Factory.getInstance().newEditable(value)
    setSelectionEnd()
}

//设置光标设置到最后
fun EditText.setSelectionEnd() = this.setSelection(text.length)

//延迟触发
fun EditText.delayTrigger(sleep: Int = 300, onTriggerValue: GT.ViewUtils.DelayTrigger.OnTriggerValue) {
    GT.ViewUtils.DelayTrigger().EditText(this, sleep, onTriggerValue)
}


//打印实体类 属性
fun Any.toStrings(): String {
    val stringBuilder = StringBuilder()
    val javaClass = this.javaClass
    val className = javaClass.simpleName
    stringBuilder.append("$className{")
    for (field in javaClass.declaredFields) {
        field.isAccessible = true
        val type = field.type
        val name = field.name
        val value = field.get(this)
        when (type) {
            String::class.java -> stringBuilder.append("$name='$value', ")
            Boolean::class.java -> stringBuilder.append("$name=$value, ")
            Short::class.java -> stringBuilder.append("$name=$value, ")
            Int::class.java -> stringBuilder.append("$name=$value, ")
            Long::class.java -> stringBuilder.append("$name=$value, ")
            Float::class.java -> stringBuilder.append("$name=$value, ")
            Double::class.java -> stringBuilder.append("$name=$value, ")
            ArrayList::class.java -> stringBuilder.append("$name=$value, ")
            Map::class.java -> stringBuilder.append("$name=$value, ")
            else -> stringBuilder.append("$name=$value, ")
        }
    }
    var toString = stringBuilder.toString()
    toString = toString.substring(0, toString.length - 2).plus("}")
    return toString
}

//延时加载
class Later<T>(private val block: () -> T) {
    var value: Any? = null
    operator fun getValue(any: Any?, prop: KProperty<*>): T {
        if (value == null) {
            value = block()
        }
        return value as T
    }
}

fun <T> load(block: () -> T) = Later(block)
