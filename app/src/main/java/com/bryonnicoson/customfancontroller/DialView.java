package com.bryonnicoson.customfancontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bryon on 3/21/18.
 */

public class DialView extends View {

    // member variables necessary to draw view
    private static int SELECTION_COUNT = 4;         // total number of selections
    private float mWidth;                           // custom view width
    private float mHeight;                          // custom view height
    private Paint mTextPaint;                       // for text in the view
    private Paint mDialPaint;                       // for dial circle in the view
    private float mRadius;                          // dial circle radius
    private int mActiveSelection;                   // the active selection

    // string buffer for dial labels and float for ComputeXY result
    private final StringBuffer mTempLabel = new StringBuffer(8);
    private final float[] mTempResult = new float[2];

    // constructors
    public DialView(Context context) {
        super(context);
        init();
    }

    public DialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // initialize the view - paint styles created in init rather than at render time = +performance
    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40f);
        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPaint.setColor(Color.GRAY);
        // initialize current selection
        mActiveSelection = 0;
        // set up onClick listener for this view
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // rotate selection to the next valid choice
                mActiveSelection = (mActiveSelection + 1) % SELECTION_COUNT;
                // set dial background color to green if selection is >= 1
                if (mActiveSelection >= 1) {
                    mDialPaint.setColor(Color.GREEN);
                } else {
                    mDialPaint.setColor(Color.GRAY);
                }
                // redraw the view - invalidate forces call to onDraw
                invalidate();
            }
        });
    }

    //  when layout is inflated or view has changed - calc radius from width and height
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mRadius = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
    }

    /**
     * Compute X and Y coordinates for the text label and indicator
     * @param pos position index
     * @param radius outer circle radius
     * @return two-element array with [0] = x and [1] = y coordinates
     */
    private float[] computeXYForPosition(final int pos, final float radius) {
        float[] result = mTempResult;
        Double startAngle = Math.PI * (9 / 8d);  // angles are in radians
        Double angle = startAngle + (pos * (Math.PI / 4));
        result[0] = (float) (radius * Math.cos(angle)) + (mWidth / 2);
        result[1] = (float) (radius * Math.sin(angle)) + (mHeight / 2);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the dial
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mDialPaint);
        // draw the text labels
        final float labelRadius = mRadius + 20;
        StringBuffer label = mTempLabel;
        for (int i = 0; i < SELECTION_COUNT; i++) {
            float[] xyData = computeXYForPosition(i, labelRadius);
            float x = xyData[0];
            float y = xyData[1];
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
        }
        // draw the indicator mark
        final float markerRadius = mRadius - 35;
        float[] xyData = computeXYForPosition(mActiveSelection, markerRadius);
        float x = xyData[0];
        float y = xyData[1];
        canvas.drawCircle(x, y, 20, mTextPaint);
    }
}
