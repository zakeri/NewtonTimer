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
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class CountDownFragment extends Fragment {

    Integer TIMER_REFRESH_DELAY = 500;

    Button buttonStart;
    Button buttonPause;
    TextView viewTimer;
    TextView messageTimer;
    EditText inputTime;

    // Variables:
    private long startTimeInMillis;
    private long totalTimeInMillis;
    private long accumulatedTimeInMillis;
    private boolean countingDown; // only false if time is up or have not been started at all.

    private Handler timerHandler;
    private Runnable timerRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_count_down, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long timerValue = totalTimeInMillis - accumulatedTimeInMillis - (System.currentTimeMillis() - startTimeInMillis);
                if (timerValue > 0) {
                    refreshTimer(timerValue);
                    timerHandler.postDelayed(this, TIMER_REFRESH_DELAY);
                }
                else {
                    refreshTimer(0);
                    messageTimer.setText(R.string.count_down_timeout_message);
                    setButtonEnabled(true, false);
                    inputTime.setInputType(1); // i.e. "text"; makes it editable
                    initVariables();
                }
            }
        };

        initVariables();
        findViews(view);
        configureButtons();
        setButtonEnabled(true, false);
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
        ss = ss % 60;
        milli = (int) (milli / 10);
        viewTimer.setText(String.format("%02d:%02d.%02d", mm, ss, milli));
    }

    private void initVariables() {
        accumulatedTimeInMillis = 0;
        totalTimeInMillis = 0;
        startTimeInMillis = 0;
        countingDown = false;
    }

    private void findViews(View view) {
        buttonStart = (Button) view.findViewById(R.id.button_count_down_start);
        buttonPause = (Button) view.findViewById(R.id.button_count_down_pause);
        viewTimer = (TextView) view.findViewById(R.id.text_count_down_timer);
        messageTimer = (TextView) view.findViewById(R.id.text_count_down_message);
        inputTime = (EditText) view.findViewById(R.id.count_down_edit_timer);
    }

    private void setButtonEnabled(boolean start, boolean pause) {
        buttonStart.setEnabled(start);
        buttonPause.setEnabled(pause);
    }

    private void configureButtons() {
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countingDown) {
                    String input = inputTime.getText().toString();
                    if (!Pattern.matches("\\d\\d:\\d\\d", input)) {
                        messageTimer.setText(R.string.count_down_error_message);
                        return;
                    }
                    long mm = Integer.parseInt(input.substring(0,2));
                    long ss = Integer.parseInt(input.substring(3,5));
                    totalTimeInMillis = mm*60*1000+ss*1000;
                    refreshTimer(totalTimeInMillis);
                    inputTime.setInputType(0); // make EditText not editable
                }
                startTimeInMillis = System.currentTimeMillis();
                countingDown = true;
                timerHandler.postDelayed(timerRunnable, TIMER_REFRESH_DELAY);
                setButtonEnabled(false, true);
                messageTimer.setText(R.string.count_down_start_message);
                buttonStart.setText(R.string.count_down_start_button);
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                accumulatedTimeInMillis += (System.currentTimeMillis() - startTimeInMillis);
                refreshTimer(totalTimeInMillis - accumulatedTimeInMillis);
                setButtonEnabled(true, false);
                messageTimer.setText(R.string.count_down_pause_message);
                buttonStart.setText(R.string.count_down_resume_button);
            }
        });
    }
}
