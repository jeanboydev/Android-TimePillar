package com.jeanboy.component.timepillar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jeanboy on 2020/6/13 15:14.
 */
public class TimePillarView extends View {

    private int rulerSpace; // 中间线间距
    private int rulerMargin; // 中间线开始与结束边距
    private int rulerWidth; // 尺子宽度
    private int rulerStartX; // 尺子开始 x 坐标
    private int rulerStartY; // 尺子开始 y 坐标
    private int rulerStep; // 尺子刻度间距
    private int maxHour = 24; // 最大时刻
    private int stepRange = 6; // 大刻度间距
    private int shortScale; // 段刻度
    private int longScale; // 长刻度
    private int pillarHeight; // 柱子高度

    private Paint rulerPaint; // 尺子画笔
    private Path rulerPath; // 尺子路径
    private Paint pillarPaint; // 柱子画笔
    private Paint bgPaint; // 背景色画笔
    private Paint textPaint; // 文字画笔

    private String headUpText;
    private String headDownText;
    private int headUpTextColor; // 头部文本上面颜色
    private int headDownTextColor; // 头部文本下面颜色
    private int headTextSize; // 头部文本大小
    private int headTextMargin; // 头部与文本的距离

    private int rulerLineColor; // 尺子线颜色
    private int rulerTextColor; // 尺子文本颜色
    private int rulerTextSize; // 尺子文本大小

    private int pillarUpStartColor;
    private int pillarUpEndColor;
    private int pillarUpTextColor;
    private int pillarDownStartColor;
    private int pillarDownEndColor;
    private int pillarDownTextColor;
    private int pillarTextSize; // 柱子文本大小
    private int pillarTextMargin; // 柱子与文本的距离

    private int bgUpStartColor;
    private int bgUpEndColor;
    private int bgDownStartColor;
    private int bgDownEndColor;

    public TimePillarView(Context context) {
        this(context, null);
    }

    public TimePillarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePillarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setupView(context, attrs);
    }

    private void setupView(Context context, AttributeSet attrs) {
        rulerSpace = dp2px(12);
        rulerMargin = dp2px(14);
        shortScale = dp2px(2);
        longScale = dp2px(6);


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimePillarView);
        try {
            headUpText = typedArray.getString(R.styleable.TimePillarView_headUpText);
            headDownText = typedArray.getString(R.styleable.TimePillarView_headDownText);
            headUpTextColor = typedArray.getColor(R.styleable.TimePillarView_headUpTextColor, Color.parseColor("#B8E986"));
            headDownTextColor = typedArray.getColor(R.styleable.TimePillarView_headDownTextColor, Color.parseColor("#FF8B99"));
            headTextSize = (int) typedArray.getDimension(R.styleable.TimePillarView_headTextSize, sp2px(12));
            headTextMargin = typedArray.getDimensionPixelSize(R.styleable.TimePillarView_headTextMargin, dp2px(4));

            rulerLineColor = typedArray.getColor(R.styleable.TimePillarView_rulerLineColor, Color.parseColor("#80ffffff"));
            rulerTextColor = typedArray.getColor(R.styleable.TimePillarView_rulerTextColor, Color.parseColor("#80ffffff"));
            rulerTextSize = (int) typedArray.getDimension(R.styleable.TimePillarView_rulerTextSize, sp2px(8));

            pillarUpStartColor = typedArray.getColor(R.styleable.TimePillarView_pillarUpStartColor, Color.parseColor("#26FF60"));
            pillarUpEndColor = typedArray.getColor(R.styleable.TimePillarView_pillarUpEndColor, Color.parseColor("#00AB8D"));
            pillarUpTextColor = typedArray.getColor(R.styleable.TimePillarView_pillarUpTextColor, Color.parseColor("#B8E986"));
            pillarDownStartColor = typedArray.getColor(R.styleable.TimePillarView_pillarDownStartColor, Color.parseColor("#AB0074"));
            pillarDownEndColor = typedArray.getColor(R.styleable.TimePillarView_pillarDownEndColor, Color.parseColor("#FF0000"));
            pillarDownTextColor = typedArray.getColor(R.styleable.TimePillarView_pillarDownTextColor, Color.parseColor("#FF5B6F"));
            pillarTextSize = (int) typedArray.getDimension(R.styleable.TimePillarView_pillarTextSize, dp2px(10));
            pillarTextMargin = typedArray.getDimensionPixelSize(R.styleable.TimePillarView_pillarTextMargin, sp2px(8));

            bgUpStartColor = typedArray.getColor(R.styleable.TimePillarView_bgUpStartColor, Color.parseColor("#00000000"));
            bgUpEndColor = typedArray.getColor(R.styleable.TimePillarView_bgUpEndColor, Color.parseColor("#3300AB8D"));
            bgDownStartColor = typedArray.getColor(R.styleable.TimePillarView_bgDownStartColor, Color.parseColor("#33AB0000"));
            bgDownEndColor = typedArray.getColor(R.styleable.TimePillarView_bgDownEndColor, Color.parseColor("#00000000"));
        } finally {
            typedArray.recycle();
        }


        // 刻度尺画笔
        rulerPaint = new Paint();
        rulerPaint.setAntiAlias(true); // 打开抗锯齿
        rulerPaint.setStyle(Paint.Style.STROKE);
        rulerPaint.setStrokeWidth(dp2px(1));

        rulerPath = new Path();

        // 柱子画笔
        pillarPaint = new Paint();
        pillarPaint.setAntiAlias(true);
        pillarPaint.setStyle(Paint.Style.FILL);

        // 背景色画笔
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);

        // 文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        textPaint.setTextSize(headTextSize);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        int fontTotalHeight = (int) (fontMetrics.bottom - fontMetrics.top);
//        // 不显示标题
//        int fontTotalHeight = 0;
//        int headTextMargin = 0;

        rulerWidth = mWidth - fontTotalHeight - headTextMargin;
        rulerStep = (rulerWidth - rulerMargin * 2) / maxHour;
        rulerStartX = getPaddingLeft() + fontTotalHeight + headTextMargin;
        rulerStartY = getPaddingTop() + mHeight / 2 - rulerSpace / 2;

        pillarHeight = (mHeight - rulerSpace) / 2 - pillarTextSize - pillarTextMargin;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT); // 绘制背景颜色

        drawHead(canvas);
        drawBackground(canvas);
        drawPillar(canvas);
        drawRuler(canvas);
    }

    /**
     * 绘制头部
     */
    private void drawHead(Canvas canvas) {
        int upHeadX = rulerStartX - headTextSize / 2 - headTextMargin;
        int upHeadY = getPaddingTop() + (rulerStartY - getPaddingTop()) / 2;
        drawText(canvas, headUpText, upHeadX, upHeadY, headTextSize, headUpTextColor, -90);
        int downHeadX = rulerStartX - headTextSize / 2 - headTextMargin;
        int downHeadY = rulerStartY + rulerSpace + (rulerStartY - getPaddingTop()) / 2;
        drawText(canvas, headDownText, downHeadX, downHeadY, headTextSize, headDownTextColor, -90);
    }

    /**
     * 绘制背景色
     */
    private void drawBackground(Canvas canvas) {
        int bgHeight = pillarHeight + pillarTextMargin + pillarTextSize;
        int rulerWidthHalf = rulerWidth / 2;
        Rect upRect = new Rect(rulerStartX, -bgHeight, rulerStartX + rulerWidth, rulerStartY);
        LinearGradient upGradient = new LinearGradient(rulerWidthHalf, rulerStartY - bgHeight, rulerWidthHalf,
                rulerStartY, bgUpStartColor, bgUpEndColor, Shader.TileMode.CLAMP);
        bgPaint.setShader(upGradient);
        canvas.drawRect(upRect, bgPaint);

        Rect downRect = new Rect(rulerStartX, rulerStartY + rulerSpace, rulerStartX + rulerWidth, rulerStartY + rulerSpace + bgHeight);
        LinearGradient downGradient = new LinearGradient(rulerWidthHalf, rulerStartY + rulerSpace,
                rulerWidthHalf, rulerStartY + rulerSpace + bgHeight, bgDownStartColor, bgDownEndColor, Shader.TileMode.CLAMP);
        bgPaint.setShader(downGradient);
        canvas.drawRect(downRect, bgPaint);
    }

    /**
     * 绘制柱子
     */
    private void drawPillar(Canvas canvas) {
        for (DataModel dataModel : dataList) {
            Rect rect = getPillarRect(dataModel, pillarHeight);
            int startColor = dataModel.isDown ? pillarDownStartColor : pillarUpStartColor;
            int endColor = dataModel.isDown ? pillarDownEndColor : pillarUpEndColor;
            int[] gradientPoint = getPillarGradientPoint(rect);

            String beginText = getHourText(dataModel.begin);
            String endText = getHourText(dataModel.end);
            boolean isDown = dataModel.isDown;
            int textColor = isDown ? pillarDownTextColor : pillarUpTextColor;
            int textX = isDown ? gradientPoint[2] : gradientPoint[0];
            int textY = isDown ? gradientPoint[3] + pillarTextMargin : gradientPoint[1] - pillarTextMargin;
            drawText(canvas, beginText + "-" + endText, textX, textY, pillarTextSize, textColor);

            LinearGradient linearGradient = new LinearGradient(gradientPoint[0], gradientPoint[1],
                    gradientPoint[2], gradientPoint[3], startColor, endColor, Shader.TileMode.CLAMP);
            pillarPaint.setShader(linearGradient);
            canvas.drawRect(rect, pillarPaint);
        }
    }

    /**
     * 绘制刻度尺
     */
    private void drawRuler(Canvas canvas) {
        // 第一条线
        rulerPath.moveTo(rulerStartX, rulerStartY);
        rulerPath.lineTo(rulerStartX + rulerWidth, rulerStartY);

        // 第二条线
        rulerPath.moveTo(rulerStartX, rulerStartY + rulerSpace);
        rulerPath.lineTo(rulerStartX + rulerWidth, rulerStartY + rulerSpace);

        // 划刻度
        rulerPath.moveTo(rulerStartX + rulerMargin, rulerStartY);
        int lastX = rulerStartX + rulerMargin;
        int lastY = rulerStartY;
        int rulerSpaceHalf = rulerSpace / 2;
        for (int i = 0; i <= maxHour; i++) {
            if (i % stepRange == 0) { // long
                rulerPath.lineTo(lastX, lastY - longScale);
                rulerPath.moveTo(lastX, lastY + rulerSpace);
                rulerPath.lineTo(lastX, lastY + rulerSpace + longScale);
                drawText(canvas, getHourText(i), lastX, lastY + rulerSpaceHalf, rulerTextSize, rulerTextColor);
            } else { // short
                rulerPath.lineTo(lastX, lastY - shortScale);
                rulerPath.moveTo(lastX, lastY + rulerSpace);
                rulerPath.lineTo(lastX, lastY + rulerSpace + shortScale);
            }
            lastX = lastX + rulerStep;
            rulerPath.moveTo(lastX, lastY);
        }
        rulerPaint.setColor(rulerLineColor);
        canvas.drawPath(rulerPath, rulerPaint);
    }

    private Rect getPillarRect(DataModel dataModel, int pillarHeight) {
        long begin = dataModel.begin;
        long end = dataModel.end;
        boolean isDown = dataModel.isDown;

        int beginPx = timeToRulerPx(begin);
        int endPx = timeToRulerPx(end);

        int left = rulerStartX + rulerMargin + beginPx;
        int top = isDown ? rulerStartY + rulerSpace : rulerStartY - pillarHeight;
        int right = rulerStartX + rulerMargin + endPx;
        int bottom = isDown ? rulerStartY + rulerSpace + pillarHeight : rulerStartY;
        return new Rect(left, top, right, bottom);
    }

    private int[] getPillarGradientPoint(Rect rect) {
        int width = rect.right - rect.left;
        int widthHalf = width / 2;

        int startX = rect.left + widthHalf;
        int startY = rect.top;
        int endX = rect.right - widthHalf;
        int endY = rect.bottom;
        return new int[]{startX, startY, endX, endY};
    }

    private int timeToRulerPx(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        int totalSecond = hour * 3600 + minute * 60 + second;
        return (int) (rulerStep * (totalSecond / 3600f));
    }

    private String getHourText(int hour) {
        String format = DateFormat.is24HourFormat(getContext()) ? "HH:mm" : "hh:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private String getHourText(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return getHourText(calendar.get(Calendar.HOUR_OF_DAY));
    }


    private void drawText(Canvas canvas, String text, float x, float y, int textSize, int textColor, float angle) {
        if (angle != 0) {
            canvas.rotate(angle, x, y);
        }
        drawText(canvas, text, x, y, textSize, textColor);
        if (angle != 0) {
            canvas.rotate(-angle, x, y);
        }
    }

    private void drawText(Canvas canvas, String text, float x, float y, int textSize, int textColor) {
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offsetY = fontTotalHeight / 2 - fontMetrics.bottom;

        int textPadding = 0;
        float newX = x - textPadding;
        float newY = y + offsetY;
        canvas.drawText(text, newX, newY, textPaint);
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private List<DataModel> dataList = new ArrayList<>();

    public void setDataList(List<DataModel> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        postInvalidate();
    }

    public static class DataModel {
        final long begin; // 开始时间（单位：秒）
        final long end; // 结束时间（单位：秒）
        final boolean isDown;

        public DataModel(long begin, long end, boolean isDown) {
            this.begin = begin;
            this.end = end;
            this.isDown = isDown;
        }
    }
}
