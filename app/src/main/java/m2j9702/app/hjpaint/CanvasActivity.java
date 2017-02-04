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

    //툴바 메뉴 생성 메소드 오버라이딩
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_canvas, menu);

        //불러온 이미지가 있을 경우(이미지 경로를 받았을 경우) 이미지를 캔버스뷰로 불러옴
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

    //사용자의 이미지 불러오기/저장하기 사용 감지
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //이미지 저장하기
        if (item.getItemId() == R.id.action_save)
        {
            //권한이 필요한 경우 권한 체크하고 물어보기(저장공간 접근 권한 필요함 - 마시멜로우 전용 코드)
            new TedPermission(this)
                    .setPermissionListener(this)
                    .setDeniedMessage("권한을 거부할 경우 서비스 이용에 제한이 있을 수 있습니다.\n\n[설정] > [권한]에서 설정하세요")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }
        //이미지 불러오기
        else if (item.getItemId() == R.id.action_import)
        {
            //갤러리에서 이미지를 1개만 가져오기
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, SELECT_PICTURE);
        }
        return true;
    }

    //권한이 승인된 경우 실행되는 메소드 -> 파일명을 입력받아 해당 이름으로 파일 저장
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

    //권한이 거부된 경우 실행되는 메소드 -> 권한 거부 상태를 Toast로 알림
    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions)
    {
        Toast.makeText(CanvasActivity.this, "권한이 거부되어 저장할 수 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

    }

    //가져온 이미지 불러오기
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SELECT_PICTURE)
        {
            if (resultCode == RESULT_OK)
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

    /**
     * 이미지 크기를 줄여 불러오는 메소드
     * @param imageHeight 이미지의 세로 길이(픽셀)
     * @param imageWidth 이미지의 가로 길이(픽셀)
     */
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

        while (true)
        {
            if (options.inSampleSize >= size)
                break;

            options.inSampleSize++;
        }
        canvasView.importBitmap(selectedImagePath, options, canvasViewWidth, canvasViewHeight);
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

    //도구탭의 버튼 클릭 감지 메소드
    @Override
    public void onClick(View v)
    {
        //지우개를 클릭한 경우
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
        //브러시를 클릭한 경우
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
        //선택툴을 클릭한 경우
        else if (v.getId() == R.id.btn_select)
        {
            canvasView.setToolType(CanvasView.ToolType.SELECT);
            btnChangeWidth.setVisibility(View.GONE);
            btnCanNotChange.setVisibility(View.VISIBLE);

        }
        //전체 지우기를 클릭한 경우
        else if (v.getId() == R.id.btn_clear)
        {
            canvasView.clearSelectedArea();
        }
        //굵기 설정을 클릭한 경우
        else if (v.getId() == R.id.btn_change_width)
        {
            //굵기 설정 다이얼로그를 보여줌
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
        //브러시 색상 설정을 선택한 경우
        else if (v.getId() == R.id.btn_change_brush_color)
        {
            //현재 툴이 브러시인 경우 브러시 색상 변경
            if (canvasView.getToolType() == CanvasView.ToolType.BRUSH)
            {
                //칼라 픽커로 색상 선택 받기
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
            //현재 툴이 선택툴인 경우 선택된 영역 색 채우기
            else if (canvasView.getToolType() == CanvasView.ToolType.SELECT)
            {
                //칼라 픽커
                new AmbilWarnaDialog(this, canvasView.getSelectFillColor(), true, new AmbilWarnaDialog.OnAmbilWarnaListener()
                {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog)
                    {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color)
                    {
                        canvasView.setSelectFillColor(color);
                    }
                }).show();
            }
        }
        //배경색 설정이 선택된 경우
        else if (v.getId() == R.id.btn_change_background)
        {
            //칼라 픽커(투명도는 제공하지 않음 - 지우개 사용시 오류 발생)
            new AmbilWarnaDialog(this, canvasView.getBitmapBackground(), false, new AmbilWarnaDialog.OnAmbilWarnaListener()
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
}