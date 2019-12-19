package com.example.battleship;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class FieldGridView extends View implements UpdateListener {
    Paint mFieldPaint, mMissPaint, mHitPaint, mShipPaint, mShipPaint2;
    private RectF mFieldLeft = new RectF();
    private RectF mFieldRight = new RectF();
    int width, height;

    boolean gameInit;

    GameInitializer gameInitializer;
    public void setGameInitializer(GameInitializer gameInitializer) {
        this.gameInitializer = gameInitializer;
        this.gameInitializer.setUpdateListener(this);
    }

    Game game;
    Game.GameState role;
    List<Rect> myShips;
    public void setRoleAndShips(int role, List<Rect> myShips) {
        this.role = role == 1 ? Game.GameState.FIRST_MOVE : Game.GameState.SECOND_MOVE;
        this.myShips = myShips;
    }

    public void setGame(Game game) {
        this.game = game;
        onUpdate();
    }

    private MakeMoveHandler makeMoveHandler;
    public void setMakeMoveHandler(MakeMoveHandler h) {
        this.makeMoveHandler = h;
    }

    public FieldGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        gameInitializer = null;
        role = null;
        myShips = null;

        TypedArray args = getContext().obtainStyledAttributes(attrs, R.styleable.FieldGridView);
        mFieldPaint = new Paint();
        mFieldPaint.setColor(args.getColor(R.styleable.FieldGridView_fieldColor, 0));
        mFieldPaint.setAntiAlias(true);
        mFieldPaint.setStyle(Paint.Style.STROKE);
        mFieldPaint.setStrokeWidth(5);

        mShipPaint = new Paint();
        mShipPaint.setColor(getResources().getColor(R.color.colorAccent));
        mShipPaint.setAntiAlias(true);
        mShipPaint.setStyle(Paint.Style.STROKE);
        mShipPaint.setStrokeWidth(10);

        mShipPaint2 = new Paint();
        mShipPaint2.setColor(Color.argb(15, 255, 59, 48));
        mShipPaint2.setAntiAlias(true);
        mShipPaint2.setStyle(Paint.Style.FILL);


        mHitPaint = new Paint();
        mHitPaint.setColor(Color.RED);
        mHitPaint.setAntiAlias(true);
        mHitPaint.setStyle(Paint.Style.STROKE);
        mHitPaint.setStrokeWidth(10);

        mMissPaint = new Paint();
        mMissPaint.setColor(Color.BLACK);
        mMissPaint.setAntiAlias(true);
        mMissPaint.setStyle(Paint.Style.STROKE);
        mHitPaint.setStrokeWidth(7);

        gameInit = args.getBoolean(R.styleable.FieldGridView_gameInitialize, false);

        args.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

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


            if (role != null && myShips != null) {
                for (Rect ship : myShips) {
                    drawShip(canvas, mFieldLeft, ship.left, ship.top, ship.right, ship.bottom, mShipPaint);
                    drawShip(canvas, mFieldLeft, ship.left, ship.top, ship.right, ship.bottom, mShipPaint2);
                }
            } else {
                System.out.println("ships or role null");
            }

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (role == Game.GameState.FIRST_MOVE) {
                        switch (game.mField1.get(i).get(j)) {
                            case MISS:
                                drawMiss(canvas, mFieldLeft, i, j);
                                break;
                            case HIT:
                                drawHit(canvas, mFieldLeft, i, j);
                                break;
                        }

                        switch (game.mField2.get(i).get(j)) {
                            case MISS:
                                drawMiss(canvas, mFieldRight, i, j);
                                break;
                            case HIT:
                                drawHit(canvas, mFieldRight, i, j);
                                break;
                        }
                    } else {
                        switch (game.mField2.get(i).get(j)) {
                            case MISS:
                                drawMiss(canvas, mFieldLeft, i, j);
                                break;
                            case HIT:
                                drawHit(canvas, mFieldLeft, i, j);
                                break;
                        }
                        switch (game.mField1.get(i).get(j)) {
                            case MISS:
                                drawMiss(canvas, mFieldRight, i, j);
                                break;
                            case HIT:
                                drawHit(canvas, mFieldRight, i, j);
                                break;
                        }
                    }
                }
            }
        } else {
            for (int i = 1; i <= 4; i++) {
                int countThisShip = 4 - i + 1;
                if (gameInitializer != null) {
                    countThisShip = gameInitializer.getLeftCount(i);
                }

                if (countThisShip != 0) {
                    drawShip(canvas, mFieldRight, i * 2 - 1, 2, i * 2 - 1, 2 + i - 1, mShipPaint);
                    drawShip(canvas, mFieldRight, i * 2 - 1, 2, i * 2 - 1, 2 + i - 1, mShipPaint2);
                }

                if (gameInitializer != null) {
                    for (Rect ship : gameInitializer.getShips()) {
                        drawShip(canvas, mFieldLeft, ship.left, ship.top, ship.right, ship.bottom, mShipPaint);
                        drawShip(canvas, mFieldLeft, ship.left, ship.top, ship.right, ship.bottom, mShipPaint2);
                    }
                }
            }
        }
    }

    void drawShip(Canvas canvas, RectF rect, int x1, int y1, int x2, int y2, Paint p) {
        canvas.drawRoundRect(getCornerX(rect, x1), getCornerY(rect, y1), getCornerX(rect, x2 + 1), getCornerY(rect, y2 + 1),
                50, 50, p);
    }

    void drawHit(Canvas canvas, RectF rect, int x, int y) {
        canvas.drawLine(getCornerX(rect, x), getCornerY(rect, y),
                getCornerX(rect, x + 1), getCornerY(rect, y + 1), mHitPaint);

        canvas.drawLine(getCornerX(rect, x + 1), getCornerY(rect, y),
                getCornerX(rect, x), getCornerY(rect, y + 1), mHitPaint);
    }

    void drawMiss(Canvas canvas, RectF rect, int x, int y) {
        canvas.drawLine(getCornerX(rect, x), getCornerY(rect, y),
                getCornerX(rect, x + 1), getCornerY(rect, y + 1), mMissPaint);

        canvas.drawLine(getCornerX(rect, x + 1), getCornerY(rect, y),
                getCornerX(rect, x), getCornerY(rect, y + 1), mMissPaint);
//        canvas.drawRect(getCornerX(rect, x), getCornerY(rect, y),
//                getCornerX(rect, x + 1), getCornerY(rect, y + 1), mMissPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (mFieldRight.contains(event.getX(), event.getY())) {
                    float unitDistance = mFieldRight.width() / 10;

                    float x = event.getX() - mFieldRight.left;
                    float y = event.getY() - mFieldRight.top;

                    x /= unitDistance;
                    y /= unitDistance;

                    int i = (int)(x);
                    int j = (int)(y);

                    if (game.gameState == role) {
                        boolean ok = game.makeMove(role, i, j);

                        if (makeMoveHandler != null && ok) {
                            makeMoveHandler.makeMove(game);
                        }
                        onUpdate();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onUpdate() {
        invalidate();
    }
}
