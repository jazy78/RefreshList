package com.example.hp.refreshlist.Dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import com.example.hp.refreshlist.R;

/**
 * Created by hp on 2016/3/29.
 */
public class LoadingDialogView extends View {

    private Bitmap endBitmap;


    public LoadingDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        endBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.takeout_img_loading_pic1);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }


    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        } else {
            result = endBitmap.getWidth();
            if (MeasureSpec.AT_MOST == mode) {
                result = Math.min(size, result);
            }
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {

        int result = 0;
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.EXACTLY == mode) {
            result = size;
        } else {
            result = endBitmap.getHeight();
            if (MeasureSpec.AT_MOST == mode) {
                result = Math.min(size, result);
            }
        }
        return result;
    }
}