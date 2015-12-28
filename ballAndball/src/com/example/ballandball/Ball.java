package com.example.ballandball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class Ball extends View{

    private static final String TAG = "dyl++";
    private int mScreenWidth;
    private int mScreenHeight;
    private volatile int ballX;
    private volatile int ballY;
    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mBitmap;
    private float ballRadius;
    private boolean ballCanMove;
    public Ball(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }
    public Ball(Context context){
        this(context,null);
    }

    /**
     * 初始化相关数据
     * @param context 上下文环境{@link Context}
     */
    private void init(Context context) {
        caculateScreenSize(context);
        ballRadius = 40;
        setupBall(mScreenWidth/2, (int)(mScreenHeight - ballRadius));
        mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Config.ARGB_8888);
        setupPaint();
        mCanvas = new Canvas(mBitmap);
        ballCanMove = false;
    }
    
    /**
     * 设置画笔属性
     */
    private void setupPaint() {
//        Log.i(TAG, "setup Paint");
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.parseColor("#999999"));
    }

    /**
     * 计算屏幕宽高
     * @param context 上下文对象{@link Context}
     */
    private void caculateScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outSize = new Point();
//        display.getRealSize(outSize);
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;//outSize.x;
        mScreenHeight = outMetrics.heightPixels;//outSize.y;
    }

    /**
     * 利用自定义{@link Canvas}绘制
     */
    private void drawBall(){
        Log.i(TAG, "draw Ball...");
        mCanvas.drawColor(Color.parseColor("#ffff00"));
        mCanvas.drawCircle(ballX, ballY, ballRadius, mPaint);
    }
    
    /**
     * 设置小球的球心坐标
     * @param x 球心x坐标
     * @param y 球心y坐标
     */
    private void setupBall(int x,int y){
        ballX = x;
        ballY = y;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw...");
        drawBall();
        canvas.drawBitmap(mBitmap, 0, 0, null);
//        canvas.drawColor(Color.parseColor("#99ffff00"));
//        canvas.drawCircle(ballX, ballY, ballRadius, mPaint);
        invalidate();
    }
    
    /**
     * 由手指控制小球的运动
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int startX = 0;
        int startY = 0;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            startX = (int) event.getX();
            startY = (int) event.getY();
            if(isBallCanMove(startX, startY)){
                Log.i(TAG, "touch down ...");
                ballCanMove = true;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if(ballCanMove){
                setupBall((int)event.getX(), (int)event.getY());
            }
            break;
        case MotionEvent.ACTION_UP:
            if(ballCanMove){
                if(ballX-ballRadius < 0){
                    setupBall((int) ballRadius, (int)event.getY());
                }else if(ballX+ballRadius > mScreenWidth){
                    setupBall((int)(mScreenWidth - ballRadius), (int)event.getY());
                }else if(ballY - ballRadius < 0){
                    setupBall((int)event.getX(), (int) ballRadius);
                }else if(ballY + ballRadius > mScreenHeight){
                    setupBall((int)event.getX(), (int)(mScreenHeight - ballRadius));
                }else {
                    setupBall((int)event.getX(), (int)event.getY());
                }
            }
            ballCanMove = false;
            new Thread(mRunnable).start();
            break;
        default:
            break;
        }
        return true;
    }
    

    /**
     * 判断手指在按下是触摸的位置是否为小球身上
     * @param f 手指按下的x坐标
     * @param g 手指按下的y坐标
     * @return 
     */
    private boolean isBallCanMove(float f, float g) {
        if(f > ballX - ballRadius && f < ballX + ballRadius && g > ballY - ballRadius && g < ballY + ballRadius){
            return true;
        }
        return false;
    }

    /**
     * 释放小球后，由此线程控制小球的运动
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int speed = 0;
            while(true){
                if(ballY <= mScreenHeight - ballRadius){
                    setupBall(ballX, ballY);
                    try {
                        Thread.sleep(30);
                        speed+=2;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ballY += speed;
                }else{
                    setupBall(ballX, (int)(mScreenHeight - ballRadius));
                    break;
                }
            }
        }
    };
}
