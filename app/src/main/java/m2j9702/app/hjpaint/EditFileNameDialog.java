package m2j9702.app.hjpaint;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by m2j97 on 2017-01-13.
 */

public class EditFileNameDialog extends Dialog implements View.OnClickListener
{
    private EditText editFileName;
    private EditFileNameListener listener;

    public interface EditFileNameListener
    {
        /**
         * 파일 저장 확인 버튼 메소드
         */
        void onEditFileNameOk(String filePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_file_name);
        editFileName = (EditText) findViewById(R.id.edit_filename);
        Button btnOkFileName = (Button) findViewById(R.id.btn_ok_filename);
        Button btnCancelFileName = (Button) findViewById(R.id.btn_cancel_filename);

        btnOkFileName.setOnClickListener(this);
        btnCancelFileName.setOnClickListener(this);
    }

    public EditFileNameDialog(Context context,EditFileNameListener listener)
    {
        super(context);
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_ok_filename)
        {
            if(listener != null)
                listener.onEditFileNameOk(editFileName.getText().toString());
            dismiss();
        }
        else if (v.getId() == R.id.btn_cancel_filename)
        {
            dismiss();
        }

    }
}
