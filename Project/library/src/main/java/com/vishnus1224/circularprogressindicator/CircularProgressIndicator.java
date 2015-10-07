package com.vishnus1224.circularprogressindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Vishnu on 10/3/2015.
 */
public class CircularProgressIndicator extends View {

    //constants for saving and restoring the view state.
    private static final String KEY_PROGRESS_STROKE_COLOR = "progressColor";
    private static final String KEY_BACKGROUND_STROKE_COLOR = "backgroundColor";
    private static final String KEY_STROKE_WIDTH = "strokeWidth";
    private static final String KEY_PROGRESS = "progress";
    private static final String KEY_MAX_PROGRESS = "maxProgress";
    private static final String KEY_TEXT_SIZE = "textSize";
    private static final String KEY_TEXT_COLOR = "textColor";
    private static final String KEY_SUPER = "super";

    /**
     * The default radius to use for setting the minimum width and height.
     */
    private int defaultRadius = 50;

    /**
     * Color for showing the progress.
     */
    private int progressStrokeColor = Color.RED;

    /**
     * Color for the background of the arc.
     */
    private int backgroundStrokeColor = Color.BLACK;

    /**
     * The stroke width for drawing the circle.
     */
    private int strokeWidth = 10;

    /**
     * Paint representing the background of the arc.
     */
    private Paint backgroundPaint;

    /**
     * Paint representing the progress portion of the arc.
     */
    private Paint progressPaint;

    /**
     * The start angle for drawing the arc.
     */
    private int startAngle = 0;

    /**
     * The end angle for drawing the background arc
     */
    private int endAngle = 360;

    /**
     * Maximum possible progress;
     */
    private int maxProgress = 100;

    /**
     * For showing the total progress.
     */
    private int progress;

    /**
     * Size of the progress percentage text.
     */
    private float textSize = 20;

    /**
     * Color of the progress percentage text.
     */
    private int textColor = Color.BLACK;

    /**
     * Paint for drawing the text to the canvas.
     */
    private TextPaint textPaint;

    /**
     * Animator for animating the drawing of the progress arc.
     */
    private ObjectAnimator progressAnimator = ObjectAnimator.ofInt(this, "progress", 0);

    /**
     * Callback to signal the end of the animation.
     */
    private AnimationEndListener animationEndListener;

    /**
     * Flag to check if the view is currently animating.
     */
    private boolean animationInProgress;


    /**
     * Callback interface for signaling the end of the animation.
     */
    public interface AnimationEndListener{
        void onAnimationComplete(Animator animator);
    }


    public CircularProgressIndicator(Context context) {
        super(context);

        initializeView();
    }

    public CircularProgressIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator, defStyleAttr, 0);

        //initialize the variables with values set from the xml layout, if any.
        progressStrokeColor = typedArray.getColor(com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator_progressColor, progressStrokeColor);
        backgroundStrokeColor = typedArray.getColor(com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator_backgroundColor, backgroundStrokeColor);
        strokeWidth = typedArray.getInt(com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator_strokeWidth, strokeWidth);
        progress = typedArray.getInt(com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator_progress, 0);
        maxProgress = typedArray.getInt(com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator_maxProgress, 100);
        textSize = typedArray.getDimension(com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator_textSize, 20);
        textColor = typedArray.getInt(com.vishnus1224.circularprogressindicator.R.styleable.CircularProgressIndicator_textColor, Color.BLACK);

        typedArray.recycle();

        initializeView();
    }

    public int getProgressStrokeColor() {
        return progressStrokeColor;
    }

    public void setProgressStrokeColor(int progressStrokeColor) {
        this.progressStrokeColor = progressStrokeColor;
        invalidate();
    }

    public int getBackgroundStrokeColor() {
        return backgroundStrokeColor;
    }

    public void setBackgroundStrokeColor(int backgroundStrokeColor) {
        this.backgroundStrokeColor = backgroundStrokeColor;
        invalidate();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        if(maxProgress > 0) {
            this.maxProgress = maxProgress;
            invalidate();
        }
    }

    public int getProgress() {
        return progress;
    }

    /**
     * Cannot be accessed outside this class. It is only used by the object animator to set the value for progress.
     * @param progress
     */
    private void setProgress(int progress) {
        this.progress = progress;
        if(progress > maxProgress){
            this.progress = maxProgress;
        }else if(progress < 0){
            this.progress = 0;
        }
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public AnimationEndListener getAnimationEndListener() {
        return animationEndListener;
    }

    public void setAnimationEndListener(AnimationEndListener animationEndListener) {
        this.animationEndListener = animationEndListener;
    }

    /**
     * Scale the progress with respect to the end angle to draw the progress arc correctly.
     * @return The scaled progress
     */
    private float getScaledProgress(){
        return (progress / (float)maxProgress) * endAngle;
    }

    private void initializeView() {
        //set the minimum dimensions.
        setMinimumWidth((defaultRadius + strokeWidth) * 2);
        setMinimumHeight((defaultRadius + strokeWidth) * 2);

        initializePaints();

        //enable saving state of the view.
        setSaveEnabled(true);
    }

    /**
     * Initializes the paints to draw the complete and incomplete portions of the circle.
     */
    private void initializePaints() {

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundStrokeColor);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStyle(Paint.Style.STROKE);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(progressStrokeColor);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measuredWidth = measureWidth(widthMeasureSpec);

        int measuredHeight = measureHeight(heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);

    }

    @Override
    public void invalidate() {
        initializePaints();
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        drawEntireArc(width, height, canvas);

        drawProgressArc(width, height, canvas);

        showProgressPercentage(width / 2, height / 2, canvas);
    }

    /**
     * Draw the entire view as an oval.
     * @param width Width of the view.
     * @param height Height of the view.
     * @param canvas Canvas to draw on.
     */
    private void drawEntireArc(int width, int height, Canvas canvas) {
        RectF rectF = new RectF();
        rectF.set(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth);
        canvas.drawArc(rectF, startAngle, endAngle, false, backgroundPaint);
    }

    /**
     * Draw the arc representing the progress.
     * @param width Width of the view.
     * @param height Height of the view.
     * @param canvas Canvas to draw on.
     */
    private void drawProgressArc(int width, int height, Canvas canvas) {
        RectF rectF = new RectF();
        rectF.set(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth);
        canvas.drawArc(rectF, startAngle - 90, getScaledProgress(), false, progressPaint);
    }


    /**
     * Show the progress percentage in the center of the view.
     * @param viewCenterX Center X coordinate.
     * @param viewCenterY Center Y coordinate.
     * @param canvas Canvas to draw on.
     */
    private void showProgressPercentage(int viewCenterX, int viewCenterY, Canvas canvas) {

        int centerX = viewCenterX + (int)(textSize / 4f);

        int centerY = viewCenterY + (int)(textSize / 4f);

        int progressPercentage = (int) (((float)progress / maxProgress) * 100);

        String textToDisplay = progressPercentage + "%";

        canvas.drawText(textToDisplay, centerX, centerY, textPaint);
    }

    /**
     * Animate the progress arc to the specified value.
     * @param duration Duration of the animation.
     * @param finalValue End value of the animation.
     */
    public void animateProgress(long duration, int finalValue){

        //start the animation if no animation is currently in progress.
        if(!animationInProgress) {
            animationInProgress = true;
            progressAnimator.setIntValues(progress, finalValue);
            progressAnimator.setDuration(duration);
            progressAnimator.setInterpolator(new AccelerateInterpolator());
            progressAnimator.addUpdateListener(updateListener);
            progressAnimator.addListener(animatorListenerAdapter);
            progressAnimator.start();
        }

    }

    /**
     * Listener for signaling the end of the animation.
     */
    private AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            animationInProgress = false;
            //tell the caller that the animation is over
            if(animationEndListener != null){
                animationEndListener.onAnimationComplete(animation);
            }
        }
    };

    /**
     * Listener for redrawing the view on every update frame.
     */
    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
        }
    };


    /**
     * Measures the width of the view.
     * @param widthMeasureSpec MeasureSpec containing the mode and size.
     * @return The measured width.
     */
    private int measureWidth(int widthMeasureSpec) {

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        switch (mode){
            case MeasureSpec.UNSPECIFIED:

                return getMaximumWidth();

            case MeasureSpec.AT_MOST:

                return getSuggestedMinimumWidth();

            case MeasureSpec.EXACTLY:

                return size;
        }

        return size;
    }


    /**
     * Measures the height of the view.
     * @param heightMeasureSpec MeasureSpec containing the mode and size.
     * @return The measured height.
     */
    private int measureHeight(int heightMeasureSpec) {

        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        switch (mode){
            case MeasureSpec.UNSPECIFIED:

                return getMaximumHeight();

            case MeasureSpec.AT_MOST:

                return getSuggestedMinimumHeight();

            case MeasureSpec.EXACTLY:

                return size;
        }

        return size;
    }

    /**
     * Calculates the maximum width for the unspecified measuring mode.
     * @return Twice the default radius.
     */
    private int getMaximumWidth() {
        return defaultRadius * 2;
    }


    /**
     * Calculates the maximum height for the unspecified measuring mode.
     * @return Twice the default radius.
     */
    private int getMaximumHeight() {
        return defaultRadius * 2;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PROGRESS_STROKE_COLOR, progressStrokeColor);
        bundle.putInt(KEY_BACKGROUND_STROKE_COLOR, backgroundStrokeColor);
        bundle.putInt(KEY_STROKE_WIDTH, strokeWidth);
        bundle.putInt(KEY_PROGRESS, progress);
        bundle.putInt(KEY_MAX_PROGRESS, maxProgress);
        bundle.putFloat(KEY_TEXT_SIZE, textSize);
        bundle.putInt(KEY_TEXT_COLOR, textColor);
        bundle.putParcelable(KEY_SUPER, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if(state instanceof Bundle){

            final Bundle bundle = (Bundle) state;

            progressStrokeColor = bundle.getInt(KEY_PROGRESS_STROKE_COLOR);
            backgroundStrokeColor = bundle.getInt(KEY_BACKGROUND_STROKE_COLOR);
            strokeWidth = bundle.getInt(KEY_STROKE_WIDTH);
            progress = bundle.getInt(KEY_PROGRESS);
            maxProgress = bundle.getInt(KEY_MAX_PROGRESS);
            textSize = bundle.getFloat(KEY_TEXT_SIZE);
            textColor = bundle.getInt(KEY_TEXT_COLOR);

            Parcelable parcelable = bundle.getParcelable(KEY_SUPER);

            super.onRestoreInstanceState(parcelable);

        }else{

            super.onRestoreInstanceState(state);
        }

        initializeView();

    }


}
