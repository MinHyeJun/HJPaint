package m2j9702.app.hjpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 그림을 그릴 수 있는 뷰
 */
public class CanvasView extends View
{

    enum ToolType { ERASER, BRUSH, SELECT; }

    private Paint paintBrush;
    private Paint paintEraser;
    private Paint paintSelectLine;
    private Paint paintSelectFill;
    private Bitmap bitmap;
    private Path selectedPath;

    /**
     * 0 - 지우개 모드
     * 1 - 브러시 모드
     */
    private ToolType toolType = ToolType.BRUSH;

    public CanvasView(Context context)
    {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        paintBrush = new Paint();
        paintEraser = new Paint();
        paintSelectLine = new Paint();
        paintSelectFill = new Paint();
        selectedPath = new Path();
        paintBrush.setStrokeWidth(10.0f);
        paintBrush.setStrokeCap(Paint.Cap.ROUND);
        paintEraser.setStrokeWidth(50.0f);
        paintEraser.setStrokeCap(Paint.Cap.ROUND);
        paintEraser.setColor(0xFFFFFFFF);
        paintEraser.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSelectLine.setStyle(Paint.Style.STROKE);
        paintSelectLine.setStrokeWidth(3.0f);
        paintSelectLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paintSelectFill.setStyle(Paint.Style.FILL);
        paintSelectFill.setColor(0xFFFFFFFF);
    }

    /**
     * 붓의 색을 바꿔주는 메소드
     * @param color 색깔
     */
    public void setBrushColor(int color)
    {
        paintBrush.setColor(color);
    }

    /**
     * 붓의 현재 색을 리턴하는 메소드
     * @return 붓의 현재 색
     */
    public int getBrushColor()
    {
        return paintBrush.getColor();
    }

    /**
     * 선택 영역 채우기 색을 설정하는 메소드
     * @param color 색깔
     */
    public void setSelectFillColor(int color)
    {
        paintSelectFill.setColor(color);
        fillSelectedArea();
    }

    /**
     * 현재 선택 영역 채우기 색을 리턴하는 메소드
     * @return  현재 선택 영역 채우기 색
     */
    public int getSelectFillColor()
    {
        return paintSelectFill.getColor();
    }

    /**
     * 배경색을 리턴하는 메소드
     * @return 배경색(==지우개 색)
     */
    public int getBitmapBackground()
    {
        return paintEraser.getColor();
    }

    /**
     * 배경색을 설정하는 메소드
     * @param color 배경색
     */
    public void setBitmapBackground(int color)
    {
        bitmap.eraseColor(color);

        if(paintEraser.getColor() != color)
            paintEraser.setColor(color);
        invalidate();
    }

    /**
     * 현재 선택된 툴의 선 두께를 설정
     * @param lineWidth 설정할 선 두께
     */
    public void setLineWidth(int lineWidth)
    {
        if(toolType == ToolType.ERASER)
            paintEraser.setStrokeWidth(lineWidth-1);
        else if (toolType == ToolType.BRUSH)
            paintBrush.setStrokeWidth(lineWidth-1);
    }

    /**
     * 현재 선택된 툴의 선 두깨를 가져오는 메소드
     * @return 현재 선택된 툴의 선 두께
     */
    public int getLineWidth()
    {
        if(toolType == ToolType.ERASER)
            return (int) paintEraser.getStrokeWidth();
        else if (toolType == ToolType.BRUSH)
            return (int) paintBrush.getStrokeWidth();
        else
            return -1;
    }

    /**현재 선택한 도구를 리턴하는 메소드
     * @return 현재 선택한 도구 타입
     */
    public ToolType getToolType()
    {
        return toolType;
    }

    /**도구를 입력 받은 도구로 변경하는 메소드
     * @param toolType 변경한 도구
     */
    public void setToolType(ToolType toolType)
    {
        this.toolType = toolType;
        selectedPath.reset();
        invalidate();
    }

    /**현재 비트맵을 JPEG형태 파일로 저장하는 메소드
     * @param filePath 저장할 파일 경로+이름
     */
    public void saveImage(String filePath)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 해당 경로의 파일을 옵션에 주어진 비율만큼 줄여 비트맵에 불러오는 메소드
     * @param filePath 파일 경로
     * @param options 불러오기 옵션(비트맵 비율이 저장되어 있어야 함)
     */
    public void importBitmap(String filePath, BitmapFactory.Options options)
    {
       this.bitmap = BitmapFactory.decodeFile(filePath,options).copy(Bitmap.Config.ARGB_8888, true);
    }

	/**
     * 캔버스의 선택된 영역을 지우는 메서드
     * ※ 선택된 영역이 없으면 전체 영역을 지운다.
     */
    public void clearSelectedArea()
    {
        if (selectedPath.isEmpty())
            setBitmapBackground(getBitmapBackground());
        else
        {
            Paint paint = new Paint(paintEraser);
            paint.setStyle(Paint.Style.FILL);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawPath(selectedPath, paint);
            invalidate();
        }
    }

    /**
     * 선택된 영역을 채우는 메소드
     */
    public void fillSelectedArea()
    {
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPath(selectedPath,paintSelectFill);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (bitmap == null)
        {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            setBitmapBackground(0xffffffff);
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
        if(toolType == ToolType.SELECT)
            canvas.drawPath(selectedPath, paintSelectLine);
    }

    private float lastX = -1, lastY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Canvas canvas = new Canvas(bitmap);

        if (toolType != ToolType.SELECT)
        {
            if (lastX != -1 && lastY != -1)
            {
                if(toolType == ToolType.ERASER)
                    canvas.drawLine(lastX, lastY, event.getX(), event.getY(), paintEraser);
                else if(toolType == ToolType.BRUSH)
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
        }
        else
        {
            // 사용자가 선택영역을 새로 그리기 시작할 때 path를 리셋한다.
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                lastX = event.getX();
                lastY = event.getY();
                selectedPath.reset();
                selectedPath.moveTo(lastX, lastY);
            }
            else
                selectedPath.lineTo(event.getX(),event.getY());

            // 사용자가 선택영역을 다 그렸을 때 끝점과 시작점을 이어준다.
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                selectedPath.lineTo(lastX, lastY);
                lastX = lastY = -1;
            }
        }

        invalidate();
        return true;
    }
}
