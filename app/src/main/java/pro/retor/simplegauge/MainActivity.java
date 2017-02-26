package pro.retor.simplegauge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pro.retor.simplegauge.gauge.GaugeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaugeView gaugeView = new GaugeView(this);
        setContentView(gaugeView);
        gaugeView.setMinValue(0);
        gaugeView.setMaxValue(4);
        gaugeView.setValue(3);
        gaugeView.setFormat(GaugeView.Companion.getINTEGER_FORMAT());

    }
}
