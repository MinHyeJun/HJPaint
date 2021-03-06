package m2j9702.app.hjpaint;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class LineWidthSettingDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
    private LineWidthSettingListener listener;
    private SeekBar seekLineWidth;
    private int initialLineWidth;
    private TextView textLineWidth;

    public interface LineWidthSettingListener
    {
        /**
         * 대화상자 확인 눌렀을 때 호출되는 메소드
         * @param lineWidth 사용자가 선택한 선 두께
         */
        void onLineDialogOk(int lineWidth);

        /**
         * 대화상자 취소 눌렀을 때 호출되는 메소드
         */
        void onLineDialogCancel();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_line_width_setting);

        seekLineWidth = (SeekBar) findViewById(R.id.seek_line_width);
        textLineWidth = (TextView) findViewById(R.id.text_line_width);
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        seekLineWidth.setOnSeekBarChangeListener(this);
        seekLineWidth.setProgress(initialLineWidth);
    }

    public LineWidthSettingDialog(Context context, int lineWidth, LineWidthSettingListener listener)
    {
        super(context);
        initialLineWidth = lineWidth;
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.btn_ok)
        {
            if(listener != null)
            {
                listener.onLineDialogOk(seekLineWidth.getProgress()+1);
            }
            dismiss();
        }
        else if (v.getId() == R.id.btn_cancel)
        {
            if (listener != null)
            {
                listener.onLineDialogCancel();
            }
            dismiss();
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        textLineWidth.setText(progress+1+"");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }
}
