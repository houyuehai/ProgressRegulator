package com.example.inf.progressregulator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by inf on 2017/3/21.
 */
public class ProgressRegulator extends View {

    private int roundColor = Color.GRAY;
    private int roundWidth = 10;
    private int roundProgressColor = Color.RED;
    private int startAngle = 270 ;

    private int textColorLight = Color.GRAY;
    private int textColorDrak = Color.BLACK;
    private int lightTextSize = 60;
    private int darkTextSize = 100;

    private int padding = 0 ;
    private int progress = 0;
    private int max = 100;

    private String title = "";
    private String subtitle = "";

    boolean currentDir;
    boolean progressRefreshing ;
    public interface ProgressChange{
        void onProgressChange(int progress);
    }

    private ProgressChange listener;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int newProgress = msg.arg1;
            progress = newProgress;

            if (listener!=null)
            listener.onProgressChange(progress);

            invalidate();
        }
    };

    private Paint paint ;
    public ProgressRegulator(Context context) {
        super(context);
    }

    public ProgressRegulator(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressRegulator);
        roundWidth = array.getInt(R.styleable.ProgressRegulator_roundWidth,roundWidth);
        roundColor = array.getColor(R.styleable.ProgressRegulator_roundColor,roundColor);
        textColorLight = array.getColor(R.styleable.ProgressRegulator_textColorLight,textColorLight);
        textColorDrak =  array.getColor(R.styleable.ProgressRegulator_textColorDrak,textColorDrak);
        lightTextSize = array.getInt(R.styleable.ProgressRegulator_lightTextSize,lightTextSize);
        darkTextSize = array.getInt(R.styleable.ProgressRegulator_darkTextSize,darkTextSize);
        lightTextSize = getWidth()/5;
        darkTextSize = getWidth()/3;

        roundProgressColor = array.getColor(R.styleable.ProgressRegulator_roundProgressColor,roundProgressColor);
        progress = array.getInt(R.styleable.ProgressRegulator_progress,progress);
        max = array.getInt(R.styleable.ProgressRegulator_max,max);
        padding = array.getColor(R.styleable.ProgressRegulator_padding,padding);
        startAngle = array.getColor(R.styleable.ProgressRegulator_startAngle,startAngle);
        array.recycle();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lightTextSize = getWidth()/15;
        darkTextSize = getWidth()/5;
        /**
         *
         * 画最外层的大圆环
        */
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (centre - roundWidth/2 - padding); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        /**
         *中间文字 上
         */
        paint.setStrokeWidth(0);
        paint.setColor(textColorLight);
        paint.setTextSize(lightTextSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        float textWidth = paint.measureText(title);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        canvas.drawText(title, centre - textWidth / 2, centre - centre/3 , paint); //画出进度百分比

        /**
         *中间文字 中
         */
        textWidth = paint.measureText(title+max);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        canvas.drawText(title+max, centre - textWidth / 2, centre + centre/3+lightTextSize/2, paint); //画出进度百分比

        /**
         *中间文字 下
         */
        paint.setColor(textColorDrak);
        paint.setTextSize(darkTextSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        textWidth = paint.measureText(progress+"");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        canvas.drawText(progress+"", centre - textWidth / 2, centre +darkTextSize/3, paint); //画出进度百分比


        /**
         * 画圆弧 ，画圆环的进度
         */

        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval, startAngle, 360f * (float)progress / (float)max, false, paint);  //根据进度画圆弧
    }
    public synchronized int getMax(){
        return max;
    }
    public synchronized int getProgress(){
        return progress;
    }
    public synchronized void setProgress(int progress){
        this.progress = progress ;

        postInvalidate();
    }
    public synchronized void setMax(int max){
        this.max = max ;
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case ACTION_DOWN :{
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        progressRefreshing = true;
                        while(progressRefreshing){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Message msg = new Message();

                            if (progress==max){
                                currentDir = false ;
                            }else if (progress==0){
                                currentDir = true ;
                            }

                            if (currentDir){
                                msg.arg1 = progress + max/100;
                            }else {
                                msg.arg1 = progress - max/100;
                            }
                            handler.sendMessage(msg);
                        }
                    }
                }).start();
            }break;
            case ACTION_UP:{
                progressRefreshing = false ;

            }break;
        }
        return true;
    }

    public ProgressChange getListener() {
        return listener;
    }

    public void setListener(ProgressChange listener) {
        this.listener = listener;
    }
}
