package m2j9702.app.hjpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 그림을 그릴 수 있는 뷰
 */
public class CanvasView extends View
{
    private Paint paint;
    private Bitmap bitmap;

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
     *
     * @param color 색깔
     */
    public void setColor(int color)
    {
        paint.setColor(color);
    }

    public int getColor()
    {
        return paint.getColor();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        if (bitmap == null)
        {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private float lastX = -1, lastY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Canvas canvas = new Canvas(bitmap);

        if (lastX != -1 && lastY != -1)
            canvas.drawLine(lastX, lastY, event.getX(), event.getY(), paint);

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            lastX = -1;
            lastY = -1;
        }
        else
        {
            lastX = event.getX();
            lastY = event.getY();
        }

        invalidate();
        return true;
    }
}
