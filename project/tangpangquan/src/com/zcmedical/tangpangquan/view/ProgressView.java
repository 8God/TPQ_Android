package com.zcmedical.tangpangquan.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zcmedical.common.utils.DensityUtil;

public class ProgressView extends View {

    private float progress;
    private Paint p = new Paint();
    private RectF rectf = new RectF(0, 0, 0, 0);

    public ProgressView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // TODO Auto-generated constructor stub
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(20);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
    }

    public ProgressView(Context context, float progress) {
        super(context);
        this.progress = progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectf.set(left, top, right, bottom);
        canvas.drawArc(rectf, startAngle, sweepAngle, false, p);
    }

    float left;
    float right;
    float top = 20;
    float bottom;
    float startAngle = 180;
    float sweepAngle = 0;

    public void setData(Activity activity, float progress) {
        this.progress = progress;
        int h = DensityUtil.getScreenHeight(activity);
        int w = DensityUtil.getScreenWidth(activity);
        bottom = DensityUtil.dip2px(activity, 212 + 56 - 24) + DensityUtil.getStatusBarHeight();
        //top = DensityUtil.dip2px(activity, 72);
        left = DensityUtil.dip2px(activity, 35);
        right = w - DensityUtil.dip2px(activity, 35);
        sweepAngle = progress * 180.0f / 50.0f;
        //Log.d("@@", "l : " + left + "  right : " + right + "   top : " + top + "   bo : " + bottom + " sw : " + sweepAngle);
        invalidate();
    }
}
