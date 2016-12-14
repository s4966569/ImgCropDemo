package com.example.sp.imgcropdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sp on 16-12-8.
 */

public class ImageCropOverView extends View {

    private int mColorBounds = Color.WHITE;
    private int mColorBg = Color.GRAY;
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect mRect;
    private static final double  LENGTH_RATIO =0.8 ;
    private static final int LINE_COUNT = 2;
    private int strokeWidth ;
    private int radios;
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
        strokeWidth = Utils.convertDpToPx(getContext(),2);
        radios = Utils.convertDpToPx(getContext(),10);
        rectPaint.setColor(mColorBounds);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(strokeWidth);

        circlePaint.setColor(Color.parseColor("#99cccccc"));
        circlePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int len = Math.min(width,height);
        int rectWidth = (int) (len * LENGTH_RATIO);

        int startX = (width - rectWidth) /2;
        int startY = (height - rectWidth) /2;
        mRect = new Rect(startX,startY,startX+rectWidth,startY+rectWidth);

        canvas.drawRect(mRect, rectPaint);

        //绘制四个小圆点
        drawCircles(mRect,canvas);
        //绘制矩形内的四条线
        drawLines(mRect,canvas);

        canvas.save();
        //画笔的stroke会占rect以及rect以外的区域各一半(实验猜测，要不边框会有阴影)
        Rect clipRect = new Rect(startX - strokeWidth/2,startY - strokeWidth/2,startX+strokeWidth/2+rectWidth,startY+strokeWidth/2+rectWidth);
        canvas.clipRect(clipRect, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#99000000"));
        canvas.restore();

    }

    private void drawCircles(Rect rect,Canvas canvas){
        canvas.drawCircle(rect.left,rect.top,radios,circlePaint);
        canvas.drawCircle(rect.right,rect.top,radios,circlePaint);
        canvas.drawCircle(rect.left,rect.bottom,radios,circlePaint);
        canvas.drawCircle(rect.right,rect.bottom,radios,circlePaint);
    }

    private void drawLines(Rect rect,Canvas canvas){
        //分别为竖直的两条线跟横着的两条线的坐标点
        float[] pts = new float[]{
                rect.left+rect.width()/3,rect.top,rect.left+rect.width()/3,rect.bottom,
        rect.left+rect.width()*2/3,rect.top,rect.left+rect.width()*2/3,rect.bottom,
        rect.left,rect.top+rect.height()/3,rect.right,rect.top+rect.height()/3,
        rect.left,rect.top+rect.height()*2/3,rect.right,rect.top+rect.height()*2/3};

        canvas.drawLines(pts,rectPaint);

    }

    public Rect getRect() {
        return mRect;
    }
}
