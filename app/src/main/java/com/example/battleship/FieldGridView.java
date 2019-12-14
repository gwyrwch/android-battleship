package com.example.battleship;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FieldGridView extends View {
    Paint mFieldPaint;
    private RectF mFieldLeft = new RectF();
    private RectF mFieldRight = new RectF();
    int width, height;

    public boolean isGameInit() {
        return gameInit;
    }

    boolean gameInit;

    GameInitializer gameInitializer;

    public void setGameInitializer(GameInitializer gameInitializer) {
        this.gameInitializer = gameInitializer;
    }


    public FieldGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        gameInitializer = null;

        TypedArray args = getContext().obtainStyledAttributes(attrs, R.styleable.FieldGridView);
        mFieldPaint = new Paint();
        mFieldPaint.setColor(args.getColor(R.styleable.FieldGridView_fieldColor, 0));
        mFieldPaint.setAntiAlias(true);
        mFieldPaint.setStyle(Paint.Style.STROKE);
        mFieldPaint.setStrokeWidth(5);

        gameInit = args.getBoolean(R.styleable.FieldGridView_gameInitialize, false);

        args.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        final int min = Math.min(width, height);

//        mTranslateX = (int) (width);
//        mTranslateY = (int) (height);



//        float top = height / 2 - (arcDiameter / 2);
//        float left = width / 2 - (arcDiameter / 2);
//        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);
//
//        updateIndicatorIconPosition();
        float proposedRight = getPaddingLeft() + (width - getPaddingRight() - getPaddingLeft()) * (float)4/9;
        float proposedBottom = height - getPaddingBottom();
        float real = Math.min(proposedRight, proposedBottom);

        mFieldLeft.set(getPaddingLeft(), getPaddingTop(), real, real);

        mFieldRight.set(width - mFieldLeft.width() - getPaddingRight(), getPaddingTop(),
                width - getPaddingRight(), getPaddingTop() + mFieldLeft.height());

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    float getCornerX(RectF rect, int x) {
        return (float)x / 10 * rect.width() + rect.left;
    }

    float getCornerY(RectF rect, int y) {
        return (float)y / 10 * rect.height() + rect.top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i <= 10; i++) {
            float verX = (float)i / 10 * mFieldLeft.width() + mFieldLeft.left;
            canvas.drawLine(verX, mFieldLeft.top, verX, mFieldLeft.bottom, mFieldPaint);

            float horY = (float)i / 10 * mFieldLeft.height() + mFieldLeft.top;
            canvas.drawLine(mFieldLeft.left, horY, mFieldLeft.right, horY, mFieldPaint);
        }

        if (!gameInit) {
            for (int i = 0; i <= 10; i++) {
                float verX = (float)i / 10 * mFieldRight.width() + mFieldRight.left;
                canvas.drawLine(verX, mFieldRight.top, verX, mFieldRight.bottom, mFieldPaint);

                float horY = (float)i / 10 * mFieldRight.height() + mFieldRight.top;
                canvas.drawLine(mFieldRight.left, horY, mFieldRight.right, horY, mFieldPaint);
            }
        } else {
            for (int i = 1; i <= 4; i++) {
                int countThisShip = 4 - i + 1;
                if (gameInitializer != null) {
                    countThisShip = gameInitializer.getLeftCount(i);
                }
                drawShip(canvas, mFieldRight, i * 2 - 1, 2, i * 2 - 1, 2 + i - 1);

                if (gameInitializer != null) {
                    for (Rect ship : gameInitializer.getShips()) {
                        drawShip(canvas, mFieldLeft, ship.left, ship.top, ship.right, ship.bottom);
                    }
                }
            }
        }
    }

    void drawShip(Canvas canvas, RectF rect, int x1, int y1, int x2, int y2) {
        canvas.drawRoundRect(getCornerX(rect, x1), getCornerY(rect, y1), getCornerX(rect, x2 + 1), getCornerY(rect, y2 + 1),
                50, 50, mFieldPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                if (mOnSwagPointsChangeListener != null)
//                    mOnSwagPointsChangeListener.onStartTrackingTouch(this);
////					updateOnTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
//                updateOnTouch(event);
                break;
            case MotionEvent.ACTION_UP:
//                if (mOnSwagPointsChangeListener != null)
//                    mOnSwagPointsChangeListener.onStopTrackingTouch(this);
//                setPressed(false);
//                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
//                if (mOnSwagPointsChangeListener != null)
//                    mOnSwagPointsChangeListener.onStopTrackingTouch(this);
//                setPressed(false);
//                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }
}
