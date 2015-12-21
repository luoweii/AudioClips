package com.luowei.audioclip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义剪辑的marker
 * Created by 骆巍 on 2015/10/27.
 */
public class ClipsMarkerTextView extends TextView {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int second;
    private float pointWidth;

    public ClipsMarkerTextView(Context context) {
        this(context, null);
    }

    public ClipsMarkerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getCompoundPaddingTop(), paint);
        super.onDraw(canvas);
    }

    public void setSecond(int second) {
        this.second = second;
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        String str = sdf.format(new Date(second * 1000));
        setText(str);
    }

    public int getSecond() {
        return second;
    }

    public void setPointWidth(float pointWidth) {
        this.pointWidth = pointWidth;
        paint.setStrokeWidth(pointWidth);
        invalidate();
    }
}
