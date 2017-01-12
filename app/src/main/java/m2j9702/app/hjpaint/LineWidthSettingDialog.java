package m2j9702.app.hjpaint;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class LineWidthSettingDialog extends Dialog
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_line_width_setting);

        SeekBar seekLineWidth = (SeekBar) findViewById(R.id.seek_line_width);
        TextView textLineWidth = (TextView) findViewById(R.id.text_line_width);
    }

    public LineWidthSettingDialog(Context context)
    {
        super(context);
    }
}
