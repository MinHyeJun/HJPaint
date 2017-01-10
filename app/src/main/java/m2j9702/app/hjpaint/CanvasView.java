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
    private Paint paintBrush;
    private Paint paintEraser;
    private Bitmap bitmap;

    /**
     * 0 - 지우개 모드
     * 1 - 브러시 모드
     */
    private int toolNumber;

    public CanvasView(Context context)
    {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        paintBrush = new Paint();
        paintEraser = new Paint();
        paintBrush.setStrokeWidth(10.0f);
        paintEraser.setStrokeWidth(50.0f);
        paintEraser.setColor(0xFFFFFFFF);
    }

    /**
     * 붓의 색을 바꿔주는 메소드
     *
     * @param color 색깔
     */
    public void setColor(int color)
    {
        paintBrush.setColor(color);
    }

    public int getColor()
    {
        return paintBrush.getColor();
    }

    public void setToolNumber(int toolNumber)
    {
        this.toolNumber = toolNumber;
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
        {
            if(toolNumber == 0)
                canvas.drawLine(lastX, lastY, event.getX(), event.getY(), paintEraser);
            else if(toolNumber == 1)
                canvas.drawLine(lastX, lastY, event.getX(), event.getY(), paintBrush);
        }

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
