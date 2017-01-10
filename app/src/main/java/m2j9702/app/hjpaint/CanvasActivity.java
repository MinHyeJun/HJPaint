package m2j9702.app.hjpaint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CanvasActivity extends AppCompatActivity implements View.OnClickListener
{

    private CanvasView canvasView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        Button btnRed = (Button) findViewById(R.id.btn_red);
        Button btnGreen = (Button) findViewById(R.id.btn_green);
        Button btnBlue = (Button) findViewById(R.id.btn_blue);
        canvasView = (CanvasView) findViewById(R.id.canvasview);

        btnRed.setOnClickListener(this);
        btnGreen.setOnClickListener(this);
        btnBlue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        TextView textView = (TextView) v;
        canvasView.changeColor(textView.getCurrentTextColor());

    }
}