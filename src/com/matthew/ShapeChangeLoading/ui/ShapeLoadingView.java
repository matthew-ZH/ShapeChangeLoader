package com.matthew.ShapeChangeLoading.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by mattlyzheng on 2015/8/23.
 */
public class ShapeLoadingView extends View {
    private Point[] squarePoints;
    private Point[] squareControlPoints;
    private Point[] triPoints;
    private Point[] triControlPoints;
    private static final float MAGIC_NUMBER = 0.551784F;
    private static final int NUMBER_CHANGE = 50;
    private float squareControlPointOffset;
    private float maxControlPointOffset;
    private float squareStep;
    private float triControlPointOffset;
    private float triStep;
    private int squareWidth;
    private float startOffset;
    private boolean isReverse;   // 绘制方向的状态控制
    private boolean withinCircle; // 绘制阶段的状态控制
    private boolean isInit = true;
    private ObjectAnimator rotationAnimation;
    private Paint mPaint;
    private Path mPath;
    public ShapeLoadingView(Context context) {
        this(context, null);
    }

    public ShapeLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareResource();
    }

    private void prepareResource() {
        withinCircle = true;
        isReverse = false;
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPath = new Path();
        mPaint.setAntiAlias(true);
        squarePoints = new Point[4];
        for (int i = 0; i < squarePoints.length; i++) {
            squarePoints[i] = new Point();
        }
        squareControlPoints = new Point[8];
        for (int i = 0; i < squareControlPoints.length; i++) {
            squareControlPoints[i] = new Point();
        }
        triPoints = new Point[3];
        for (int i = 0; i < triPoints.length; i++) {
            triPoints[i] = new Point();
        }
        triControlPoints = new Point[2];
        for (int i = 0; i < triControlPoints.length; i++) {
            triControlPoints[i] = new Point();
        }
        squareWidth = dp2px(50);
        maxControlPointOffset = (float) (MAGIC_NUMBER / Math.sqrt(2) * squareWidth /Math.sqrt(2));
        squareControlPointOffset = maxControlPointOffset;
        squareStep = maxControlPointOffset / NUMBER_CHANGE;
        triStep = squareWidth / NUMBER_CHANGE;
        triControlPointOffset = squareWidth;
        startOffset = squareWidth / 2;
        initSquare();
        updateSquareControlPoint();
        initTriangle();
        rotationAnimation = ObjectAnimator.ofFloat(this, ROTATION, 0, 360).setDuration(5000);
        rotationAnimation.setInterpolator(new LinearInterpolator());
        rotationAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimation.setRepeatMode(ValueAnimator.RESTART);
    }

    private void initSquare(){
        squarePoints[0].x = startOffset;
        squarePoints[0].y = startOffset;

        squarePoints[1].x = startOffset + squareWidth;
        squarePoints[1].y = startOffset;

        squarePoints[2].x = startOffset + squareWidth;
        squarePoints[2].y = startOffset + squareWidth;

        squarePoints[3].x = startOffset;
        squarePoints[3].y = startOffset + squareWidth;
    }

    private void initTriangle(){
        triPoints[0].x = squarePoints[0].x + squareWidth / 2;
        triPoints[0].y = squarePoints[0].y;

        triPoints[1].x = squarePoints[2].x;
        triPoints[1].y = squarePoints[2].y;

        triPoints[2].x = squarePoints[3].x;
        triPoints[2].y = squarePoints[3].y;
    }

    private void updateSquareControlPoint(){
        squareControlPoints[0].x = squarePoints[0].x + squareControlPointOffset;
        squareControlPoints[0].y = squarePoints[0].y - squareControlPointOffset;
        squareControlPoints[1].x = squarePoints[1].x - squareControlPointOffset;
        squareControlPoints[1].y = squarePoints[1].y - squareControlPointOffset;

        squareControlPoints[2].x = squarePoints[1].x + squareControlPointOffset;
        squareControlPoints[2].y = squarePoints[1].y + squareControlPointOffset;
        squareControlPoints[3].x = squarePoints[2].x + squareControlPointOffset;
        squareControlPoints[3].y = squarePoints[2].y - squareControlPointOffset;

        squareControlPoints[4].x = squarePoints[2].x - squareControlPointOffset;
        squareControlPoints[4].y = squarePoints[2].y + squareControlPointOffset;
        squareControlPoints[5].x = squarePoints[3].x + squareControlPointOffset;
        squareControlPoints[5].y = squarePoints[3].y + squareControlPointOffset;

        squareControlPoints[6].x = squarePoints[3].x - squareControlPointOffset;
        squareControlPoints[6].y = squarePoints[3].y - squareControlPointOffset;
        squareControlPoints[7].x = squarePoints[0].x - squareControlPointOffset;
        squareControlPoints[7].y = squarePoints[0].y + squareControlPointOffset;
    }

    private void updateTriControlPoints(){
        triControlPoints[0].x = triPoints[2].x;
        triControlPoints[0].y = triPoints[2].y - triControlPointOffset;

        triControlPoints[1].x = triPoints[1].x;
        triControlPoints[1].y = triPoints[1].y - triControlPointOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInit) {
            rotationAnimation.start();
            isInit = false;
        }
        if (withinCircle) {
            updateSquareAnimation();
            if (isReverse) {
                squareControlPointOffset += squareStep;
            } else {
                squareControlPointOffset -= squareStep;
            }

            //临界点变换状态
            if (squareControlPointOffset < 0.0001) {
                withinCircle = false;
            } else if (maxControlPointOffset - squareControlPointOffset < 0.001) {
                isReverse = false;
            }
        } else {
            updateTriAnimation();
            if (isReverse) {
                triControlPointOffset += triStep;
            } else {
                triControlPointOffset -= triStep;
            }

            //临界点变换状态
            if (triControlPointOffset < 0.0001) {
                isReverse = true;
            } else if (squareWidth - triControlPointOffset < 0.001) {
                withinCircle = true;
            }
        }
        canvas.drawPath(mPath, mPaint);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(2 * squareWidth, 2 * squareWidth);
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private class Point{
        float x;
        float y;
    }

    private void updateSquareAnimation(){
        updateSquareControlPoint();
        mPath.reset();
        mPath.moveTo(squarePoints[0].x, squarePoints[0].y);
        mPath.cubicTo(squareControlPoints[0].x, squareControlPoints[0].y, squareControlPoints[1].x, squareControlPoints[1].y, squarePoints[1].x, squarePoints[1].y);
        mPath.cubicTo(squareControlPoints[2].x, squareControlPoints[2].y, squareControlPoints[3].x, squareControlPoints[3].y, squarePoints[2].x, squarePoints[2].y);
        mPath.cubicTo(squareControlPoints[4].x, squareControlPoints[4].y, squareControlPoints[5].x, squareControlPoints[5].y, squarePoints[3].x, squarePoints[3].y);
        mPath.cubicTo(squareControlPoints[6].x, squareControlPoints[6].y, squareControlPoints[7].x, squareControlPoints[7].y, squarePoints[0].x, squarePoints[0].y);
    }

    private void updateTriAnimation(){
        updateTriControlPoints();
        mPath.reset();
        mPath.moveTo(triPoints[0].x, triPoints[0].y);
        mPath.quadTo(triControlPoints[1].x, triControlPoints[1].y, triPoints[1].x, triPoints[1].y);
        mPath.lineTo(triPoints[2].x, triPoints[2].y);
        mPath.quadTo(triControlPoints[0].x, triControlPoints[0].y, triPoints[0].x, triPoints[0].y);
    }

}
