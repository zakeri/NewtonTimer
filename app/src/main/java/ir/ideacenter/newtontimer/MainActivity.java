package ir.ideacenter.newtontimer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonStopWatch;
    Button buttonCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        configure();
    }

    private void findViews() {
        buttonStopWatch = (Button) findViewById(R.id.button_stop_watch);
        buttonCountDown = (Button) findViewById(R.id.button_count_down);
    }

    private void configure() {
        buttonStopWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopWatchFragment stopWatchFragment = new StopWatchFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_stop_watch, stopWatchFragment)
                        .addToBackStack("STOP_WATCH_FRAGMENT")
                        .commit();
            }
        });

        buttonCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountDownFragment countDownFragment = new CountDownFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_count_down, countDownFragment)
                        .addToBackStack("COUNT_DOWN_FRAGMENT")
                        .commit();
            }
        });
    }
}
