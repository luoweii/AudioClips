package com.luowei.audioclip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.luowei.audioclip.soundfile.SoundFile;

/**
 * 剪辑布局
 * Created by 骆巍 on 2015/10/26.
 */
public class ClipsFrameLayout extends FrameLayout {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int progress;
    private int progressHeight;
    private int maxProgress;
    private ClipsMarkerTextView cmtvStart;
    private ClipsMarkerTextView cmtvEnd;
    private Bitmap cfBackground;
    private int clipsOverColor;
    private int minSecond;//最小的剪辑时间
    private OnTouchListener startClipsTouchListener;
    private int pointColor;
    private float pointWidth;

    private Paint wavePaint;
    private SoundFile soundFile;
    private float[] smoothedGains;

    public ClipsFrameLayout(Context context) {
        this(context, null);
    }

    public ClipsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);//使该组件有绘图能力
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClipsFrameLayout);
        progressHeight = typedArray.getDimensionPixelSize(R.styleable.ClipsFrameLayout_clip_progressHeight, 0);
        final int startView = typedArray.getResourceId(R.styleable.ClipsFrameLayout_clip_startId, 0);
        final int endView = typedArray.getResourceId(R.styleable.ClipsFrameLayout_clip_endId, 0);
        int bgId = typedArray.getResourceId(R.styleable.ClipsFrameLayout_clip_background, 0);
        clipsOverColor = typedArray.getColor(R.styleable.ClipsFrameLayout_clip_clipsOverColor, Color.parseColor("#87654321"));
        pointWidth = typedArray.getDimension(R.styleable.ClipsFrameLayout_clip_point_width, 1);
        paint.setStrokeWidth(pointWidth);
        pointColor = typedArray.getColor(R.styleable.ClipsFrameLayout_clip_point_color, Color.WHITE);
        minSecond = typedArray.getInteger(R.styleable.ClipsFrameLayout_clip_clipsMinSecond, 10);
        if (minSecond < 1 || minSecond > maxProgress/2) {
            minSecond = 10;
        }
        int waveformColor = typedArray.getColor(R.styleable.ClipsFrameLayout_clip_waveformColor, Color.GREEN);
        typedArray.recycle();

        cfBackground = BitmapFactory.decodeResource(getResources(), bgId);
        wavePaint = new Paint();
        wavePaint.setColor(waveformColor);
        wavePaint.setStrokeWidth(0);
        wavePaint.setAntiAlias(false);

        post(new Runnable() {
            @Override
            public void run() {
                cmtvStart = (ClipsMarkerTextView) findViewById(startView);
                cmtvStart.setPointWidth(pointWidth);
                cmtvStart.setTranslationX(getWidth() / 3f);
                cmtvStart.setSecond(getSecondByPosition(getWidth() / 3f + cmtvStart.getWidth() / 2f));
                cmtvStart.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                float x = event.getRawX() - cmtvStart.getWidth() / 2f;
                                if (cmtvEnd.getTranslationX() - x < getPositionBySecond(minSecond)) {
                                    return false;
                                }
                                cmtvStart.setTranslationX(x);
                                cmtvStart.setSecond(getSecondByPosition(x + cmtvStart.getWidth() / 2f));
//                                invalidate();
                                break;
                        }
                        if (startClipsTouchListener != null)
                            startClipsTouchListener.onTouch(v, event);
                        return true;
                    }
                });
                cmtvEnd = (ClipsMarkerTextView) findViewById(endView);
                cmtvEnd.setPointWidth(pointWidth);
                cmtvEnd.setTranslationX(getWidth() * 2f / 3f);
                cmtvEnd.setSecond(getSecondByPosition(getWidth() * 2f / 3f + cmtvEnd.getWidth() / 2f));
                cmtvEnd.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                float x = event.getRawX() - cmtvStart.getWidth() / 2f;
                                if (x - cmtvStart.getTranslationX() < getPositionBySecond(minSecond)) {
                                    return false;
                                }
                                cmtvEnd.setTranslationX(x);
                                cmtvEnd.setSecond(getSecondByPosition(x + cmtvEnd.getWidth() / 2f));
//                                invalidate();
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        CommonUtil.showToast("onAttachedToWindow");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cfBackground != null) {
            Rect r = new Rect(0, 0, getWidth(), progressHeight);
            canvas.drawBitmap(cfBackground, null, r, null);
        }

        if (soundFile != null) {
            int width = getWidth();
            int height = getHeight();
            float ctr = progressHeight / 2f;
            for (int i = 0; i < width; i+=2) {
                canvas.drawLine(i, ctr-smoothedGains[i]/2, i, ctr+smoothedGains[i]/2, wavePaint);
            }
        }

        float x = (float) (progress * getWidth()) / (float) maxProgress;
        paint.setColor(pointColor);
        canvas.drawLine(x, 0, x, progressHeight, paint);

        if (cmtvStart != null && cmtvEnd != null) {
            paint.setColor(clipsOverColor);
            float left = cmtvStart.getTranslationX() + cmtvStart.getWidth() / 2f;
            float right = cmtvEnd.getTranslationX() + cmtvEnd.getWidth() / 2f;
            canvas.drawRect(left, 0, right, progressHeight, paint);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        cmtvStart.setTranslationX(getWidth() / 3f);
        cmtvStart.setSecond(getSecondByPosition(getWidth() / 3f + cmtvStart.getWidth() / 2f));
        cmtvEnd.setTranslationX(getWidth() * 2f / 3f);
        cmtvEnd.setSecond(getSecondByPosition(getWidth() * 2f / 3f + cmtvEnd.getWidth() / 2f));
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getStartClips() {
        return cmtvStart.getSecond();
    }

    public int getEndClips() {
        return cmtvEnd.getSecond();
    }

    private int getSecondByPosition(float position) {
        return (int) (position * maxProgress / getWidth());
    }

    private float getPositionBySecond(int second) {
        return (float) (second * getWidth()) / (float) maxProgress;
    }

    public void setStartClipsTouchListener(OnTouchListener startClipsTouchListener) {
        this.startClipsTouchListener = startClipsTouchListener;
    }

    public void setSoundFile(SoundFile soundFile) {
        this.soundFile = soundFile;
        computeSmoothedGains();
    }

    private void computeSmoothedGains() {
        int[] frameGains = soundFile.getFrameGains();
        smoothedGains = new float[getWidth()];
        int countPerWidth = frameGains.length / getWidth();
        if (countPerWidth < 1) countPerWidth = 1;
        for (int i = 0; i < smoothedGains.length; i++) {
            float sum = 0;
            for (int j = 0; j < countPerWidth; j++) {
                sum += frameGains[j + i * countPerWidth];
            }
            smoothedGains[i] = sum/countPerWidth;
        }
        float maxGains = 0;
        for (float a : smoothedGains) {
            if (a > maxGains) maxGains = a;
        }
        float factor = (float) (progressHeight/Math.pow(maxGains,3));
        for (int i = 0; i < smoothedGains.length;i++) {
            smoothedGains[i] = (float) (Math.pow(smoothedGains[i],3) * factor);
        }
    }
}
