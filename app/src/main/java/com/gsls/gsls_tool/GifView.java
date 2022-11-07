package com.gsls.gsls_tool;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.gsls.gt.GT;

import java.io.InputStream;

/**
 * @Author PlayfulKing
 * @Date 2022/6/22 22:20
 * @CSDN https://blog.csdn.net/qq_39799899
 * @GitHub https://github.com/1079374315/GT
 * @Description:
 */
public class GifView extends androidx.appcompat.widget.AppCompatImageView {

    private Resources resources;
    private Movie mMovie;
    private long mMovieStart;
    private float ratioWidth;
    private float ratioHeight;

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        GT.logt("GifView 初始化");
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        resources = context.getResources();
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GifView);
//        int resourceId = typedArray.getResourceId(R.styleable.GifView_src, -1);
//        setGifResource(resourceId);
//        typedArray.recycle();
        requestLayout();
    }

    public void setGifResource(int resourceId) {

        if (resourceId == -1) {
            return;
        }
        InputStream is = resources.openRawResource(resourceId);
        mMovie = Movie.decodeStream(is);
        requestLayout();
        GT.logt("setGifResource：" + mMovie);//2号
    }

    public void setGifStream(InputStream is) {
        mMovie = Movie.decodeStream(is);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        GT.logt("onMeasure：" + mMovie);
        if (mMovie != null) {
            int w = mMovie.width() - 10;
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        GT.logt("onDraw：" + mMovie);
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
        }
    }
	
	
	
}