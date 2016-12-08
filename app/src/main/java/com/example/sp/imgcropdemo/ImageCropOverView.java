package com.example.sp.imgcropdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sp on 16-12-8.
 */

public class ImageCropOverView extends View {

    private int mColorBounds = Color.WHITE;
    private int mColorBg = Color.GRAY;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public ImageCropOverView(Context context) {
        super(context);
        init();
    }

    public ImageCropOverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageCropOverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint.setColor(mColorBounds);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(Utils.convertDpToPx(getContext(),2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect(0,0,width,height);
        canvas.drawRect(rect,mPaint);
    }
}
