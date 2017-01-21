package m2j9702.app.hjpaint;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class TitleActivity extends AppCompatActivity implements View.OnClickListener

{
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Button btnNewCanvas = (Button) findViewById(R.id.btn_new_start);
        Button btnImportCanvas = (Button) findViewById(R.id.btn_import_start);

        btnNewCanvas.setOnClickListener(this);
        btnImportCanvas.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.btn_new_start)
        {
            Intent intent = new Intent(TitleActivity.this, CanvasActivity.class);
            startActivityForResult(intent,0);
        }
        else if(v.getId() == R.id.btn_import_start)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, SELECT_PICTURE);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                int imageHeight = 0;
                int imageWidth = 0;
                try
                {
                    imageHeight = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()).getHeight();
                    imageWidth = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()).getWidth();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                Intent intent = new Intent(TitleActivity.this, CanvasActivity.class);
                intent.putExtra("Image", selectedImagePath);
                intent.putExtra("ImageHeight", imageHeight);
                intent.putExtra("ImageWidth", imageWidth);
                startActivity(intent);
            }
        }
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
}
