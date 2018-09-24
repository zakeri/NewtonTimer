package ir.ideacenter.newtontimer;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

public class StopWatchFragment extends Fragment {

    Integer STATE_CLEAR = 0;
    Integer STATE_START = 1;
    Integer STATE_STOP = 2;

    Integer TIMER_REFRESH_DELAY = 500;

    Button buttonStart;
    Button buttonStop;
    Button buttonClear;
    TextView viewTimer;
    Integer state;
    private long startTimeInMillis;
    private long accumulatedTimeInMillis;
    private Handler timerHandler;
    private Runnable timerRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_watch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                refreshTimer(accumulatedTimeInMillis + (System.currentTimeMillis() - startTimeInMillis));
                timerHandler.postDelayed(this, TIMER_REFRESH_DELAY);
            }
        };
        accumulatedTimeInMillis = 0;
        startTimeInMillis = 0;

        findViews(view);
        configureButtons();

        state = STATE_CLEAR;
        refreshPage();
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void refreshTimer(long timeInMillis) {
        long milli = timeInMillis % 1000;
        long ss = (long) (timeInMillis / 1000);
        long mm = (long) (ss / 60);
        long hh = (long) (mm / 60);
        mm = mm % 60;
        ss = ss % 60;
        milli = (int) (milli / 10);
        viewTimer.setText(String.format("%02d:%02d:%02d.%02d", hh, mm, ss, milli));
    }

    private void setButtonEnabled(boolean clear, boolean start, boolean stop) {
        buttonClear.setEnabled(clear);
        buttonStart.setEnabled(start);
        buttonStop.setEnabled(stop);
    }

    private void refreshPage() {
        if (state == STATE_CLEAR) {
            accumulatedTimeInMillis = 0;
            timerHandler.removeCallbacks(timerRunnable);
            refreshTimer(0);
            setButtonEnabled(false, true, false);
        }
        else if (state == STATE_START) {
            startTimeInMillis = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, TIMER_REFRESH_DELAY);
            setButtonEnabled(false, false, true);
        }
        else if (state == STATE_STOP) {
            timerHandler.removeCallbacks(timerRunnable);
            accumulatedTimeInMillis += (System.currentTimeMillis() - startTimeInMillis);
            refreshTimer(accumulatedTimeInMillis);
            setButtonEnabled(true, true, false);
        }
    }

    private void findViews(View view) {
        buttonStart = (Button) view.findViewById(R.id.button_stop_watch_start);
        buttonStop = (Button) view.findViewById(R.id.button_stop_watch_stop);
        buttonClear = (Button) view.findViewById(R.id.button_stop_watch_clear);
        viewTimer = (TextView) view.findViewById(R.id.text_stop_watch_timer);
    }

    private void configureButtons() {
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = STATE_START;
                refreshPage();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = STATE_STOP;
                refreshPage();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = STATE_CLEAR;
                refreshPage();
            }
        });
    }
}
