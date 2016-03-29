package com.example.hp.refreshlist.RefreshListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.hp.refreshlist.R;

/**
 * Created by hp on 2016/3/29.
 */
public class RefreshAnimView extends View {

    private Bitmap daishu;
    private int measureWidth;
    private int measureHeight;
    private float mCurrentProgress;
    private int mCuurentAlpha;
    private Paint mPaint;
    private Bitmap scaleDaishu;

    public RefreshAnimView(Context context) {
        super(context);
        init();
    }

    public RefreshAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){

        daishu= BitmapFactory.decodeResource(getResources(), R.mipmap.takeout_img_list_loading_pic1);
        //来个画笔，我们注意到袋鼠都有一个渐变效果的，我们用
        //mPaint.setAlpha来实现这个渐变的效果
        mPaint=new Paint();
        mPaint.setAlpha(0);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
}

    //测量宽度
    private int measureWidth(int widthMeasureSpec){
        int result = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        }else {
            result = daishu.getWidth();
            if (MeasureSpec.AT_MOST == mode) {
                result = Math.min(result, size);
            }
        }
        return result;
    }
    //测量高度
    private int measureHeight(int heightMeasureSpec){
        int result = 0;
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        }else {
            result = daishu.getHeight();
            if (MeasureSpec.AT_MOST == mode) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    //在这里面拿到测量后的宽和高，w就是测量后的宽，h是测量后的高

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureWidth=w;
        measureHeight=h;
        scaleDaishu=Bitmap.createScaledBitmap(daishu,measureWidth,measureHeight,true);
        Log.d("BBB","measureWidth="+measureWidth);
        Log.d("BBB","measureHeight="+measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.scale(mCurrentProgress,mCurrentProgress,measureWidth/2,measureHeight);//画布缩放
        mPaint.setAlpha(mCuurentAlpha);
        Log.d("AAA","mCuurentAlpha"+mCurrentProgress);
        canvas.drawBitmap(scaleDaishu,0,0,mPaint);
    }

    public void setCurrentProgress(float progress){
        mCurrentProgress=progress;
        mCuurentAlpha=(int)(progress*255);

    }
}
