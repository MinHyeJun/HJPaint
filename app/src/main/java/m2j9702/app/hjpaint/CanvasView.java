package m2j9702.app.hjpaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 그림을 그릴 수 있는 뷰
 */
public class CanvasView extends View
{
    private Paint paint;
    private ArrayList<Point> arrayPoint = new ArrayList<>();

    public CanvasView(Context context)
    {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        paint = new Paint();
        paint.setStrokeWidth(10.0f);
    }

    /**
     * 붓의 색을 바꿔주는 메소드
     * @param color 색깔
     */
    public void changeColor(int color)
    {
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        for (int i = 0; i < arrayPoint.size() - 1; i++)
            canvas.drawLine(arrayPoint.get(i).x, arrayPoint.get(i).y, arrayPoint.get(i+1).x, arrayPoint.get(i+1).y, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Point p = new Point((int) event.getX(), (int) event.getY());
        arrayPoint.add(p);
        invalidate();
        return true;
    }
}
