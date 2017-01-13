package m2j9702.app.hjpaint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CanvasActivity extends AppCompatActivity implements View.OnClickListener
{

    private CanvasView canvasView;
    private RadioButton btnEraser;
    private RadioButton btnBrush;
    private Button btnClear;
    private RadioButton btnSelect;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        btnEraser = (RadioButton) findViewById(R.id.btn_eraser);
        btnBrush = (RadioButton) findViewById(R.id.btn_brush);
        btnSelect = (RadioButton) findViewById(R.id.btn_select);
        btnClear = (Button) findViewById(R.id.btn_clear);
        canvasView = (CanvasView) findViewById(R.id.canvasview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnEraser.setOnClickListener(this);
        btnBrush.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_canvas, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_change_color)
        {
            new AmbilWarnaDialog(this, canvasView.getBrushColor(), true, new AmbilWarnaDialog.OnAmbilWarnaListener()
            {
                @Override
                public void onCancel(AmbilWarnaDialog dialog)
                {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color)
                {
                    canvasView.setBrushColor(color);
                }
            }).show();
        }
        else if (item.getItemId() == R.id.action_line_width)
        {
            LineWidthSettingDialog dialog = new LineWidthSettingDialog(this, canvasView.getLineWidth(), new LineWidthSettingDialog.LineWidthSettingListener()
            {
                @Override
                public void onLineDialogOk(int lineWidth)
                {
                    canvasView.setLineWidth(lineWidth);
                    Log.d("ASDF", "OK");
                }

                @Override
                public void onLineDialogCancel()
                {
                    Log.d("ASDF", "Cancel");
                }
            });
            dialog.show();
        }
        else if (item.getItemId() == R.id.action_change_background)
        {
            new AmbilWarnaDialog(this, canvasView.getBitmapBackground(), true, new AmbilWarnaDialog.OnAmbilWarnaListener()
            {
                @Override
                public void onCancel(AmbilWarnaDialog dialog)
                {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color)
                {
                    canvasView.setBitmapBackground(color);
                }
            }).show();
        }
        else if(item.getItemId() == R.id.action_change_tool)
        {

        }
        else if(item.getItemId() == R.id.action_save)
        {
            EditFileNameDialog dialog = new EditFileNameDialog(this, new EditFileNameDialog.EditFileNameListener()
            {
                @Override
                public void onEditFileNameOk(String filePath)
                {
                    canvasView.saveImage("/sdcard/" + filePath +".jpeg");
                    Log.d("ASDF", "/sdcard/" + filePath +".jpeg");
                }
            });
            dialog.show();
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_eraser)
        {
            canvasView.setToolType(ToolType.ERASER);
        }
        else if (v.getId() == R.id.btn_brush)
        {
            canvasView.setToolType(ToolType.BRUSH);
        }
        else if (v.getId() == R.id.btn_select)
        {
            canvasView.setToolType(ToolType.SELECT);
        }
        else if (v.getId() == R.id.btn_clear)
        {
            canvasView.clearSelectedArea();
        }
    }
}