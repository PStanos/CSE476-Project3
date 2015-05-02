package edu.msu.stanospa.project3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class DrawView extends View {
    public enum PaintColor {
        Black,
        White,
        Red,
        Green,
        Blue
    }

    private class DrawPoint {
        public static final float MAX_RADIUS = 25.0f;

        public DrawPoint(float x, float y, float radius, Paint currentPaint) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.paint = currentPaint;
        }

        public void onDraw(Canvas canvas, DrawPoint prev) {
            if(prev != null) {
                canvas.drawCircle(x, y, radius, paint);

                if(x == prev.getX() && y == prev.getY()) {
                    return;
                }

                float angle = -90.0f + (float)Math.toDegrees(Math.atan2(y - prev.getY(), x - prev.getX()));

                canvas.save();
                canvas.translate(prev.getX(), prev.getY());
                canvas.rotate(angle);
                canvas.translate(-radius, 0.0f);
                canvas.drawRect(0.0f, 0.0f, radius * 2.0f, (float)Math.sqrt(Math.pow(x - prev.getX(), 2.0f) + Math.pow(y - prev.getY(), 2.0f)), paint);
                canvas.restore();

                canvas.drawCircle(prev.getX(), prev.getY(), prev.getRadius(), prev.getPaint());
            }
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getRadius() {
            return radius;
        }

        public Paint getPaint() {
            return paint;
        }

        private float x;
        private float y;
        private float radius;
        private Paint paint;
    }

    private Paint greenPaint;
    private Paint blackPaint;
    private Paint bluePaint;
    private Paint whitePaint;
    private Paint redPaint;

    private Paint currentPaint;

    private ArrayList<DrawPoint> points = new ArrayList<DrawPoint>();

    public DrawView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setStyle(Paint.Style.FILL);
        greenPaint.setColor(0xff00ff00);

        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(0xff000000);

        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(0xff0000ff);

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(0xffffffff);

        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(0xffff0000);

        currentPaint = blackPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        DrawPoint prev = null;
        for(DrawPoint drawPoint : points) {
            drawPoint.onDraw(canvas, prev);
            prev = drawPoint;
        }
    }

    public void addPoint(float scaledX, float scaledY, float scaledRadius) {
        points.add(new DrawPoint(scaledX * this.getWidth(), scaledY * this.getHeight(), scaledRadius * DrawPoint.MAX_RADIUS, currentPaint));
        invalidate();
    }

    public void setPaintColor(PaintColor chosenColor) {
        switch(chosenColor) {
            case Black:
                currentPaint = blackPaint;
                break;
            case White:
                currentPaint = whitePaint;
                break;
            case Red:
                currentPaint = redPaint;
                break;
            case Green:
                currentPaint = greenPaint;
                break;
            case Blue:
                currentPaint = bluePaint;
                break;
        }
    }
}
