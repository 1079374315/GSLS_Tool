package com.gsls.gsls_tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.gsls.gt.GT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author PlayfulKing
 * @Date 2022/6/29 20:49
 * @CSDN https://blog.csdn.net/qq_39799899
 * @GitHub https://github.com/1079374315/GT
 * @Description:
 */
public class GTImageView extends AppCompatImageView {

    //圆形
    private float width, height;
    private int defaultRadius = 0;
    private int radius;
    private int leftTopRadius;
    private int rightTopRadius;
    private int rightBottomRadius;
    private int leftBottomRadius;
    private Context context;

    //动态图
    private Resources resources;
    private Movie mMovie;
    private long mMovieStart;
    private float ratioWidth;
    private float ratioHeight;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GTImageView(Context context) {
        this(context, null);
        GT.logt("初始化 1号");
        init(context, null);
    }

    public GTImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        GT.logt("初始化 2号");
        init(context, attrs);
    }

    public GTImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        GT.logt("初始化 3号");
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        resources = context.getResources();

        //设置图片其他参数
        TypedArray typedArray = context.obtainStyledAttributes(attrs, androidx.appcompat.R.styleable.AppCompatImageView);
        int resourceId = typedArray.getResourceId(androidx.appcompat.R.styleable.AppCompatImageView_android_src, -1);
        setGifResource(resourceId);//设置动态图

        //设置图片 四边圆角
        TypedArray array = context.obtainStyledAttributes(attrs, com.gsls.gt.R.styleable.GTImageView);
        load(array);
        array.recycle();

        //
        requestLayout();
    }

    /**
     * 加载图片
     *
     * @param array
     */
    private void load(TypedArray array) {
        radius = array.getDimensionPixelOffset(com.gsls.gt.R.styleable.GTImageView_radius, defaultRadius);
        leftTopRadius = array.getDimensionPixelOffset(com.gsls.gt.R.styleable.GTImageView_radius_top_left, defaultRadius);
        rightTopRadius = array.getDimensionPixelOffset(com.gsls.gt.R.styleable.GTImageView_radius_top_right, defaultRadius);
        rightBottomRadius = array.getDimensionPixelOffset(com.gsls.gt.R.styleable.GTImageView_radius_bottom_right, defaultRadius);
        leftBottomRadius = array.getDimensionPixelOffset(com.gsls.gt.R.styleable.GTImageView_radius_bottom_left, defaultRadius);

        //如果四个角的值没有设置，那么就使用通用的radius的值。
        if (defaultRadius == leftTopRadius) {
            leftTopRadius = radius;
        }
        if (defaultRadius == rightTopRadius) {
            rightTopRadius = radius;
        }
        if (defaultRadius == rightBottomRadius) {
            rightBottomRadius = radius;
        }
        if (defaultRadius == leftBottomRadius) {
            leftBottomRadius = radius;
        }
    }

    public void setGifResource(Context... contexts) {
        setGifResource(url, contexts);
    }

    /**
     * 设置 GIF 动态图
     *
     * @param resource
     * @param contexts
     */
    public void setGifResource(Object resource, Context... contexts) {
        if (contexts.length > 0) {
            context = contexts[0];
        }

        //下面的逻辑只有 Integer、InputStream 逻辑可以走通
        GT.Observable.getDefault().execute(new GT.Observable.RunJavaR<InputStream>() {
            @Override
            public InputStream run() {
                if (resource == null) return null;
                if (resource instanceof Integer) {
                    int resourceId = (int) resource;
                    GT.logt("进入 Integer:" + resourceId);
                    if (resourceId == -1) return null;
                    return resources.openRawResource(resourceId);
                } else if (resource instanceof InputStream) {
                    InputStream inputStream1 = (InputStream) resource;
                    GT.logt("进入 InputStream:" + inputStream1);
                    return inputStream1;
                } else if (resource instanceof String) {
                   /* String gifStr = String.valueOf(resource);
                    GT.logt("进入 String:" + gifStr);
                    java.io.InputStream inputStream = GT.ImageViewTools.getImageInputStream(gifStr);
                    GT.logt("新生 inputStream:" + inputStream);
                    mMovie = Movie.decodeStream(inputStream);
                    if (mMovie != null) {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        requestLayout();
                    }*/

                   GT.Thread.runAndroid(new Runnable() {
                       @Override
                       public void run() {
                           com.bumptech.glide.Glide.with(context).asGif().load(String.valueOf(resource)).into(GTImageView.this);
                       }
                   });

                    return null;
                    /*String imgResource = (String) resource;
                    if (imgResource.contains("http")) {
                        Bitmap bitmap = GT.ImageViewTools.getImageInputStream(imgResource);
                        return GT.ImageViewTools.Bitmap2InputStream(bitmap);
                    } else {
                        if (GT.FileUtils.fileExist(imgResource)) {//如果是SD地址那就先，看是否存在，存在
                            File file = new File(imgResource);
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                return fileInputStream;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }*/
                } else if (resource instanceof Drawable) {
                    Drawable drawable = (Drawable) resource;
                    return GT.ImageViewTools.Drawable2InputStream(drawable);
                } else if (resource instanceof Bitmap) {
                    Bitmap bitmap = (Bitmap) resource;
                    return GT.ImageViewTools.Bitmap2InputStream(bitmap);
                } else if (resource instanceof Uri) {
                    Uri uri = (Uri) resource;
                    try {
                        return context.getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                return null;
            }
        }).execute(runAndroidV);
    }

    /**
     * 设置 Assets 资源
     *
     * @param resource
     */
    public void setGifAssets(String resource) {
        GT.Observable.getDefault().execute(new GT.Observable.RunJavaR<InputStream>() {
            @Override
            public InputStream run() {
                try {
                    return context.getAssets().open(resource);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute(runAndroidV);

    }

    //加载处理好的 GIF 资源
    private GT.Observable.RunAndroidV<InputStream> runAndroidV = new GT.Observable.RunAndroidV<InputStream>() {
        @Override
        public void run(InputStream inputStream) {
//            GT.logt("inputStream:" + inputStream);
            if (inputStream == null) return;
            mMovie = Movie.decodeStream(inputStream);
            if (mMovie != null) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                requestLayout();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            int w = mMovie.width();
            int h = mMovie.height();
            if (w <= 0) {
                w = 1;
            }
            if (h <= 0) {
                h = 1;
            }
            int pLeft = getPaddingLeft();
            int pRight = getPaddingRight();
            int pTop = getPaddingTop();
            int pBottom = getPaddingBottom();
            int widthSize;
            int heightSize;
            w += pLeft + pRight;
            h += pTop + pBottom;
            w = Math.max(w, getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());
            widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
            ratioWidth = (float) widthSize / w;
            ratioHeight = (float) heightSize / h;
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        if (mMovieStart == 0) { //第一次进入
            mMovieStart = now;
        }
        if (mMovie != null) {
            int dur = mMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            int relTime = (int) ((now - mMovieStart) % dur);
            mMovie.setTime(relTime);
            mMovie.draw(canvas, 0, 0);
            float scale = Math.min(ratioWidth, ratioHeight);
            canvas.scale(scale, scale);
            mMovie.draw(canvas, 0, 0);
            invalidate();
        } else {
            initDraw(canvas);
        }
    }

    @SuppressLint("WrongCall")
    private void initDraw(Canvas canvas) {
        //这里做下判断，只有图片的宽高大于设置的圆角距离的时候才进行裁剪
        int maxLeft = Math.max(leftTopRadius, leftBottomRadius);
        int maxRight = Math.max(rightTopRadius, rightBottomRadius);
        int minWidth = maxLeft + maxRight;
        int maxTop = Math.max(leftTopRadius, rightTopRadius);
        int maxBottom = Math.max(leftBottomRadius, rightBottomRadius);
        int minHeight = maxTop + maxBottom;
        if (width >= minWidth && height > minHeight) {
            @SuppressLint("DrawAllocation")
            Path path = new Path();
            //四个角：右上，右下，左下，左上
            path.moveTo(leftTopRadius, 0);
            path.lineTo(width - rightTopRadius, 0);
            path.quadTo(width, 0, width, rightTopRadius);

            path.lineTo(width, height - rightBottomRadius);
            path.quadTo(width, height, width - rightBottomRadius, height);

            path.lineTo(leftBottomRadius, height);
            path.quadTo(0, height, 0, height - leftBottomRadius);

            path.lineTo(0, leftTopRadius);
            path.quadTo(0, 0, leftTopRadius, 0);

            canvas.clipPath(path);
            super.onDraw(canvas);
        }
    }

}