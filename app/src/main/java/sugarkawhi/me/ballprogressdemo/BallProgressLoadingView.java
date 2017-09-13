package sugarkawhi.me.ballprogressdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by sugarkawhi on 2017/9/13.
 */

public class BallProgressLoadingView extends View {

    private static final int WRAP_CONTENT_DEFAULT_WIDTH = 300;
    private static final int DEFAULT_WAVE_FACTOR = 20;

    private int mWidth;
    private int mHeight;
    private Paint mBallPaint;
    private Paint mWavePaint;
    private Path mWavePath;
    private PorterDuffXfermode xfermode;
    private float cx;
    private float cy;

    private ValueAnimator mValueAnimator;

    //决定波浪的高度
    private int mWaveY;
    //决定波浪的起始位置
    private int mWaveX;

    //进度 设置set方法 刷新进度
    private float mPercent;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    public BallProgressLoadingView(Context context) {
        this(context, null);
    }

    public BallProgressLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallProgressLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallPaint.setStyle(Paint.Style.FILL);
        mBallPaint.setColor(Color.YELLOW);

        mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWavePaint.setColor(Color.BLUE);

        mWavePath = new Path();

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        mValueAnimator = ValueAnimator.ofFloat(1, 0);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(1000);
        mValueAnimator.setRepeatCount(-1);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = WRAP_CONTENT_DEFAULT_WIDTH;
        }
        int heightSize = widthSize;
        //View中球的半径就是整个View去掉Padding后除2
        mWidth = widthSize - getPaddingLeft() - getPaddingRight();
        //设置宽高相等
        mHeight = mWidth;

        //注意这里是负的
        mWaveX = -mWidth;
        mWaveY = mHeight;

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2;
        cy = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBitmap.eraseColor(Color.TRANSPARENT);
        drawBall();
        drawWave();
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }


    Point point1, point2, point3, point4, point5;
    Point mControll1, mControll2, mControll3, mControll4;

    /**
     * 初始化点坐标
     */
    private void initPoints() {
        //对mWaveX做处理 实现波浪起伏
        float animateValue = (float) mValueAnimator.getAnimatedValue();
        mWaveX = -(int) (mWidth * animateValue);
        //还需要对mWaveY做处理 实现增长
        mWaveY = (int) (mHeight * (1 - mPercent));

        point1 = new Point(mWaveX, mWaveY);
        point2 = new Point(mWaveX + mWidth / 2, mWaveY);
        point3 = new Point(mWaveX + mWidth, mWaveY);
        point4 = new Point(mWaveX + mWidth * 3 / 2, mWaveY);
        point5 = new Point(mWaveX + mWidth * 2, mWaveY);

        mControll1 = new Point(mWaveX + mWidth / 4, mWaveY - DEFAULT_WAVE_FACTOR);
        mControll2 = new Point(mWaveX + mWidth * 3 / 4, mWaveY + DEFAULT_WAVE_FACTOR);
        mControll3 = new Point(mWaveX + mWidth + mWidth / 4, mWaveY - DEFAULT_WAVE_FACTOR);
        mControll4 = new Point(mWaveX + mWidth + mWidth * 3 / 4, mWaveY + DEFAULT_WAVE_FACTOR);
    }

    /**
     * 画波浪
     */
    private void drawWave() {

        mWavePath.reset();

        initPoints();

        mWavePath.moveTo(point1.x, point1.y);
        mWavePath.quadTo(mControll1.x, mControll1.y, point2.x, point2.y);
        mWavePath.quadTo(mControll2.x, mControll2.y, point3.x, point3.y);
        mWavePath.quadTo(mControll3.x, mControll3.y, point4.x, point4.y);
        mWavePath.quadTo(mControll4.x, mControll4.y, point5.x, point5.y);
        mWavePath.lineTo(mWidth, mHeight);
        mWavePath.lineTo(-mWidth, mHeight);

        mWavePath.close();
        mWavePaint.setXfermode(xfermode);
        mCanvas.drawPath(mWavePath, mWavePaint);
        mWavePaint.setXfermode(null);
    }

    /**
     * 画球
     */
    private void drawBall() {
        mCanvas.drawCircle(cx, cy, mWidth / 2, mBallPaint);
    }


    /**
     * 设置当前进度
     *
     * @param percent
     */
    public void setPercent(float percent) {
        this.mPercent = percent;
        if (percent==1 || percent ==0 && mValueAnimator.isRunning()){
           mValueAnimator.cancel();
        }else {
            mValueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mValueAnimator.cancel();
        super.onDetachedFromWindow();
    }
}
