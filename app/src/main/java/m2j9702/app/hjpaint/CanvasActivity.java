package m2j9702.app.hjpaint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CanvasActivity extends AppCompatActivity implements View.OnClickListener
{

    private CanvasView canvasView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        Button btnChangeColor =  (Button) findViewById(R.id.btn_change_color);
        Button btnEraser = (Button) findViewById(R.id.btn_eraser);
        Button btnBrush = (Button) findViewById(R.id.btn_brush);
        canvasView = (CanvasView) findViewById(R.id.canvasview);

        btnChangeColor.setOnClickListener(this);
        btnEraser.setOnClickListener(this);
        btnBrush.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_change_color)
        {
            new AmbilWarnaDialog(this, canvasView.getColor(), true, new AmbilWarnaDialog.OnAmbilWarnaListener()
            {
                @Override
                public void onCancel(AmbilWarnaDialog dialog)
                {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color)
                {
                    canvasView.setColor(color);
                }
            }).show();
        }
        else if(v.getId() == R.id.btn_eraser)
        {
            canvasView.setToolNumber(ToolNumber.Eraser);
        }
        else if(v.getId() == R.id.btn_brush)
        {
            canvasView.setToolNumber(ToolNumber.Brush);
        }



    }
}