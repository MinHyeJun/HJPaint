package m2j9702.app.hjpaint;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CanvasActivity extends AppCompatActivity implements View.OnClickListener, PermissionListener
{

    private CanvasView canvasView;
    private RadioButton btnEraser;
    private RadioButton btnBrush;
    private RadioButton btnSelect;
    private Button btnClear;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private Button btnChangeWidth;
    private Button btnChangeBrushColor;
    private Button btnChangeBackground;
    private Button btnCanNotChange;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        btnEraser = (RadioButton) findViewById(R.id.btn_eraser);
        btnBrush = (RadioButton) findViewById(R.id.btn_brush);
        btnSelect = (RadioButton) findViewById(R.id.btn_select);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnChangeWidth = (Button) findViewById(R.id.btn_change_width);
        btnChangeBrushColor = (Button) findViewById(R.id.btn_change_brush_color);
        btnChangeBackground = (Button) findViewById(R.id.btn_change_background);
        btnCanNotChange = (Button) findViewById(R.id.btn_can_not_change);
        canvasView = (CanvasView) findViewById(R.id.canvasview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        btnEraser.setOnClickListener(this);
        btnBrush.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnChangeBackground.setOnClickListener(this);
        btnChangeBrushColor.setOnClickListener(this);
        btnChangeWidth.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_canvas, menu);
        if (getIntent().getStringExtra("Image") != null)
        {
            selectedImagePath = getIntent().getStringExtra("Image");
            int imageHeight = getIntent().getIntExtra("ImageHeight", 0);
            int imageWidth = getIntent().getIntExtra("ImageWidth", 0);
            ImportImage(imageHeight, imageWidth);
            Toast.makeText(this, "이미지를 성공적으로 불러왔습니다.", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_save)
        {
            new TedPermission(this)
                    .setPermissionListener(this)
                    .setDeniedMessage("권한을 거부할 경우 서비스 이용에 제한이 있을 수 있습니다.\n\n[설정] > [권한]에서 설정하세요")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }
        else if (item.getItemId() == R.id.action_import)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, SELECT_PICTURE);
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                try
                {
                    int imageHeight = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()).getHeight();
                    int imageWidth = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()).getWidth();
                    ImportImage(imageHeight, imageWidth);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ImportImage(int imageHeight, int imageWidth)
    {
        int canvasViewWidth = canvasView.getWidth();
        int canvasViewHeight = canvasView.getHeight();
        double size;

        BitmapFactory.Options options = new BitmapFactory.Options();

        if (((double)imageHeight / canvasViewHeight) > ((double)imageWidth / canvasViewWidth))
            size = (double) imageHeight / canvasViewHeight;
        else
            size = (double) imageWidth / canvasViewWidth;

        Log.d("ASDF","Canvas View Width: " + canvasViewWidth);
        Log.d("ASDF","Canvas View Height: " + canvasViewHeight);
        Log.d("ASDF", "Device Width: " + getApplicationContext().getResources().getDisplayMetrics().widthPixels);
        Log.d("ASDF", "Device Height: " + getApplicationContext().getResources().getDisplayMetrics().heightPixels);


        while (true)
        {
            if (options.inSampleSize >= size)
                break;

            options.inSampleSize++;
        }
        canvasView.importBitmap(selectedImagePath, options);
    }

    /**
     * 사진의 URI 경로를 받는 메소드
     */
    public String getPath(Uri uri)
    {
        // uri가 null일경우 null반환
        if (uri == null)
        {
            return null;
        }
        // 미디어스토어에서 유저가 선택한 사진의 URI를 받아온다.
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null)
        {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // URI경로를 반환한다.
        return uri.getPath();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_eraser)
        {
            canvasView.setToolType(CanvasView.ToolType.ERASER);
            btnChangeWidth.setText("" + (canvasView.getLineWidth() + 1));

            if (btnChangeWidth.getVisibility() == View.GONE)
            {
                btnChangeWidth.setVisibility(View.VISIBLE);
                btnCanNotChange.setVisibility(View.GONE);
            }
        }
        else if (v.getId() == R.id.btn_brush)
        {
            canvasView.setToolType(CanvasView.ToolType.BRUSH);
            btnChangeWidth.setText("" + (canvasView.getLineWidth() + 1));
            if (btnChangeWidth.getVisibility() == View.GONE)
            {
                btnChangeWidth.setVisibility(View.VISIBLE);
                btnCanNotChange.setVisibility(View.GONE);
            }
        }
        else if (v.getId() == R.id.btn_select)
        {
            canvasView.setToolType(CanvasView.ToolType.SELECT);
            btnChangeWidth.setVisibility(View.GONE);
            btnCanNotChange.setVisibility(View.VISIBLE);

        }
        else if (v.getId() == R.id.btn_clear)
        {
            canvasView.clearSelectedArea();
        }
        else if (v.getId() == R.id.btn_change_width)
        {

            LineWidthSettingDialog dialog = new LineWidthSettingDialog(this, canvasView.getLineWidth(), new LineWidthSettingDialog.LineWidthSettingListener()
            {
                @Override
                public void onLineDialogOk(int lineWidth)
                {
                    canvasView.setLineWidth(lineWidth);
                    btnChangeWidth.setText("" + lineWidth);
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
        else if (v.getId() == R.id.btn_change_brush_color)
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
        else if (v.getId() == R.id.btn_change_background)
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
    }

    @Override
    public void onPermissionGranted()
    {
        EditFileNameDialog dialog = new EditFileNameDialog(this, new EditFileNameDialog.EditFileNameListener()
        {
            @Override
            public void onEditFileNameOk(String filePath)
            {
                if (filePath.length() == 0)
                {
                    Toast.makeText(CanvasActivity.this, "파일명을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    canvasView.saveImage("/sdcard/" + filePath + ".jpeg");
                    Toast.makeText(CanvasActivity.this, "/내 디바이스/" + filePath + ".jpeg 로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions)
    {
        Toast.makeText(CanvasActivity.this, "권한이 거부되어 저장할 수 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

    }
}