package com.example.sp.imgcropdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sp on 16-12-8.
 */

public class ImageCropOverView extends View {

    private int mColorBounds = Color.WHITE;
    private int mColorBg = Color.GRAY;
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect mRect;   //带一半白线边框的裁剪区域（默认画笔的stroke会占裁剪区域跟非裁剪区域各一半）
    private Rect mClipRect;  //带整个白线边框的裁剪区域
    private static final double LENGTH_RATIO = 0.8;
    private int mInitLength;
    private int strokeWidth;
    private int radios;
    private int mLastX, mLastY;
    private int mLeft, mRight, mTop, mBottom; //裁剪区域的四个参数
    private View targetView ; //需要裁剪的目标View; 注意targetView 必须跟此View在同一个viewGroup里面
    //   p1********p2
    //     *      *
    //     *      *
    //     *      *
    //   p3********p4
    private Point p1 = new Point();
    private Point p2 = new Point();
    private Point p3 = new Point();
    private Point p4 = new Point();

    private Point touchPoint;  //当前拖动的圆点
    private int dragMode;
    private static final int DRAG_MOVE = 0x01;
    private static final int DRAG_SCALE = 0x02;


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

    private void init() {
        mRect = new Rect(0, 0, 0, 0);
        mClipRect = new Rect(0, 0, 0, 0);
        strokeWidth = Utils.convertDpToPx(getContext(), 2);
        radios = Utils.convertDpToPx(getContext(), 10);
        rectPaint.setColor(mColorBounds);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(strokeWidth);

        circlePaint.setColor(Color.parseColor("#99cccccc"));
        circlePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = targetView.getWidth();
        int height = targetView.getHeight();

        int len = Math.min(width, height);
        if (mInitLength == 0) {
            mInitLength = (int) (len * LENGTH_RATIO);

            mLeft = (width - mInitLength) / 2+targetView.getLeft();
            mTop = (height - mInitLength) / 2+targetView.getTop();
            mRight = mLeft + mInitLength;
            mBottom = mTop + mInitLength;
        }
        mRect.left = mLeft;
        mRect.top = mTop;
        mRect.right = mRight;
        mRect.bottom = mBottom;

        p1.set(mLeft, mTop);
        p2.set(mRight, mTop);
        p3.set(mLeft, mBottom);
        p4.set(mRight, mBottom);

        //绘制裁剪区域
        canvas.drawRect(mRect, rectPaint);
        //绘制四个小圆点
        drawCircles(mRect, canvas);
        //绘制矩形内的四条线
        drawLines(mRect, canvas);

        canvas.save();
        //画笔的stroke会占rect以及rect以外的区域各一半(实验猜测，要不边框会有重叠阴影)
        mClipRect.left = mLeft - strokeWidth / 2;
        mClipRect.top = mTop - strokeWidth / 2;
        mClipRect.right = mRight + strokeWidth / 2;
        mClipRect.bottom = mBottom + strokeWidth / 2;

        canvas.clipRect(mClipRect, Region.Op.DIFFERENCE);
        //绘制背景
        canvas.drawColor(Color.parseColor("#99000000"));
        canvas.restore();

    }

    private void drawCircles(Rect rect, Canvas canvas) {
        canvas.drawCircle(rect.left, rect.top, radios, circlePaint);
        canvas.drawCircle(rect.right, rect.top, radios, circlePaint);
        canvas.drawCircle(rect.left, rect.bottom, radios, circlePaint);
        canvas.drawCircle(rect.right, rect.bottom, radios, circlePaint);
    }

    private void drawLines(Rect rect, Canvas canvas) {
        //分别为竖直的两条线跟横着的两条线的坐标点
        float[] pts = new float[]{
                rect.left + rect.width() / 3, rect.top, rect.left + rect.width() / 3, rect.bottom,
                rect.left + rect.width() * 2 / 3, rect.top, rect.left + rect.width() * 2 / 3, rect.bottom,
                rect.left, rect.top + rect.height() / 3, rect.right, rect.top + rect.height() / 3,
                rect.left, rect.top + rect.height() * 2 / 3, rect.right, rect.top + rect.height() * 2 / 3};

        canvas.drawLines(pts, rectPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchDot(x, y) == null && !isTouchInRect(x, y, mRect))
                    return super.onTouchEvent(event);
                else if (touchDot(x, y) != null) {
                    dragMode = DRAG_SCALE;
                    touchPoint = touchDot(x, y);
                } else {
                    dragMode = DRAG_MOVE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (dragMode == DRAG_MOVE) {
                    mLeft += deltaX;
                    mRight += deltaX;
                    mTop += deltaY;
                    mBottom += deltaY;
                    Rect rect = new Rect(mLeft - strokeWidth / 2, mTop - strokeWidth / 2, mRight + strokeWidth / 2, mBottom + strokeWidth / 2);
                    if (isOutOfView(rect)) {
                        //不能从临界点坐标开始绘制（比如0,0；因为strokeWidth会占绘制区域跟非绘制区域各一半）
                        if (rect.left < targetView.getLeft()) {
                            mLeft = strokeWidth / 2 + targetView.getLeft();
                            mRight = mRect.width() + mLeft;
                        }
                        if (rect.right > targetView.getRight()) {
                            mRight = targetView.getRight() - strokeWidth / 2;
                            mLeft = mRight - mRect.width();
                        }
                        if (rect.top < targetView.getTop()) {
                            mTop = strokeWidth / 2 +targetView.getTop();
                            mBottom = mRect.height() + mTop;
                        }
                        if (rect.bottom > targetView.getBottom()) {
                            mBottom = targetView.getBottom() - strokeWidth / 2;
                            mTop = mBottom - mRect.height();
                        }
                    }
                } else if (dragMode == DRAG_SCALE) {

                    if (touchPoint == p1) {
                        //拖动p1时，只会改变left,top(没到最小临界值的时候)
                        //左右拖动
                        if (mLeft - strokeWidth / 2 < targetView.getLeft())
                            //往左拖动的时候，不能超过左边界
                            mLeft = strokeWidth / 2 + targetView.getLeft();
                        if (mRight - (mLeft + deltaX) >= strokeWidth){
                            //往右拖动时，没到拖动临界值（最小宽度），让它一直跟着手势拖动
                            mLeft += deltaX;
                        }else if(mRight + deltaX <= targetView.getRight() - strokeWidth /2){
                            //当往右边拉到最小宽度，且最右边不出边界，就让它继续往右移动（其余三个点的处理逻辑类似）
                            mLeft+=deltaX;
                            mRight+=deltaX;
                        }
                        //上下拖动
                        if (mTop - strokeWidth / 2 < targetView.getTop())
                            //往上拖动的时候，不能超过上边界
                            mTop = strokeWidth / 2 + targetView.getTop();
                        if (mBottom - (mTop + deltaY) >= strokeWidth){
                            //往下拖动的时候，没拖动到临界值（最小高度），让它一直跟着手势拖动
                            mTop += deltaY;
                        } else if(mBottom +deltaY <= targetView.getBottom() - strokeWidth /2){
                            //当往下拉到最小高度，且最下边不出边界，就让它继续往下移动（其余三个点的处理逻辑类似）
                            mTop +=deltaY;
                            mBottom += deltaY;
                        }
                    } else if (touchPoint == p2) {
                        //拖动p2，只会改变right,top(没到最小临界值的时候)
                        if (mRight + strokeWidth / 2 > targetView.getRight())
                            mRight = targetView.getRight() - strokeWidth / 2;
                        if (mRight + deltaX - mLeft >= strokeWidth){
                            mRight += deltaX;
                        }else if(mLeft+deltaX >= targetView.getLeft() + strokeWidth /2){
                            mLeft+=deltaX;
                            mRight+=deltaX;
                        }
                        if (mTop - strokeWidth / 2 < targetView.getTop())
                            mTop = strokeWidth / 2 + targetView.getTop();
                        if (mBottom - (mTop + deltaY) >= strokeWidth){
                            mTop += deltaY;
                        }else if(mBottom + deltaY <= targetView.getBottom() - strokeWidth /2){
                            mTop+=deltaY;
                            mBottom+=deltaY;
                        }

                    } else if (touchPoint == p3) {
                        //拖动p3,只会改变left，bottom(没到最小临界值的时候)
                        if (mLeft - strokeWidth / 2 < targetView.getLeft())
                            mLeft = strokeWidth / 2 + targetView.getLeft();
                        if (mRight - (mLeft + deltaX) >= strokeWidth){
                            mLeft += deltaX;
                        }else if(mRight + deltaX <= targetView.getRight() - strokeWidth /2){
                            mLeft+=deltaX;
                            mRight+=deltaX;
                        }
                        if (mBottom + strokeWidth / 2 > targetView.getBottom())
                            mBottom = targetView.getBottom() - strokeWidth / 2;
                        if (mBottom + deltaY - mTop >= strokeWidth){
                            mBottom += deltaY;
                        }else if(mTop + deltaY >= targetView.getTop() + strokeWidth /2) {
                            mTop+=deltaY;
                            mBottom+=deltaY;
                        }
                    } else if (touchPoint == p4) {
                        //拖动p4只会改变right，bottom(没到最小临界值的时候)
                        if (mRight + strokeWidth / 2 > targetView.getRight())
                            mRight = targetView.getRight() - strokeWidth / 2;
                        if (mRight + deltaX - mLeft >= strokeWidth){
                            mRight += deltaX;
                        }else if(mLeft + deltaX >= targetView.getLeft() + strokeWidth /2){
                            mLeft+=deltaX;
                            mRight+=deltaX;
                        }
                        if (mBottom + strokeWidth / 2 > targetView.getBottom())
                            mBottom = targetView.getBottom() - strokeWidth / 2;
                        if (mBottom + deltaY - mTop >= strokeWidth){
                            mBottom += deltaY;
                        }else if(mTop + deltaY >= targetView.getTop() + strokeWidth /2){
                            mTop+=deltaY;
                            mBottom+=deltaY;
                        }
                    }
                }
                if (mLeft == mRect.left && mTop == mRect.top && mRight == mRect.right && mBottom == mRect.bottom) {

                } else {
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;

        return true;
    }

    /**
     * 触摸点是否在裁剪矩形区域内
     *
     * @param x
     * @param y
     * @param rect
     * @return
     */
    private boolean isTouchInRect(int x, int y, Rect rect) {
        if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
            return true;
        }
        return false;
    }

    /**
     * 当前触摸的是哪个圆点
     *
     * @param x 触摸位置的x坐标
     * @param y 触摸位置的y坐标
     * @return 当前触摸的圆点的圆心，触摸位置不在四个圆点上则返回null
     */
    private Point touchDot(int x, int y) {
        Point p = new Point(x, y);
        if (isPointInCircle(p, p1))
            return p1;
        else if (isPointInCircle(p, p2))
            return p2;
        else if (isPointInCircle(p, p3))
            return p3;
        else if (isPointInCircle(p, p4))
            return p4;
        else return null;
    }

    /**
     * 某个点是否在圆内
     *
     * @param p  需要判断的点
     * @param p0 圆心
     * @return
     */
    private boolean isPointInCircle(Point p, Point p0) {
        if ((p0.x - p.x) * (p0.x - p.x) + (p0.y - p.y) * (p0.y - p.y) <= radios * radios * 4)
            return true;
        return false;
    }

    /**
     * 拖动是否超出View范围
     *
     * @return
     */
    private boolean isOutOfView(Rect rect) {
        if (rect.left >= targetView.getLeft() && rect.right <= targetView.getRight() && rect.top >= targetView.getTop() && rect.bottom <= targetView.getBottom()) {
            return false;
        }
        return true;
    }

    private void savePoints(Rect rect) {
        p1.set(rect.left, rect.top);
        p2.set(rect.right, rect.top);
        p3.set(rect.left, rect.bottom);
        p4.set(rect.right, rect.bottom);
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
    }

    /**
     * 获取相对目标View的Rect
     * @return
     */
    public Rect getRect() {
        Rect rect = new Rect(mRect.left - targetView.getLeft(),mRect.top-targetView.getTop(),mRect.right -targetView.getLeft(),mRect.bottom -targetView.getTop());
        return rect;
    }
}
