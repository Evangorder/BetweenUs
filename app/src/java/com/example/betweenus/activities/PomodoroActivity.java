package com.example.betweenus.activities;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.adapter.PomodoroTimer;
import com.example.betweenus.model.ShopItem;

import java.io.InputStream;
import java.util.ArrayList;

public class PomodoroActivity extends AppCompatActivity {

    private boolean isTestMode = false; //for testing

    private int testFocusSeconds = 30;
    private int testBreakSeconds = 10;

    private TextView tvTime, tvPoints;
    private ProgressBar circleTimer;
    private ImageButton btnPlay, btnSound, btnSettings;

    private PomodoroTimer timerManager;

    private int focusMinutes = 25;
    private int breakMinutes = 5;
    private int rounds = 3;

    private int currentRound = 1;
    private boolean isFocusPhase = true;

    private int focusSecondsCompleted = 0;
    private int currentUserID = 1;

    private boolean isRunning = false;
    private boolean soundOn = true;

    private MediaPlayer mediaPlayer;
    private int selectedSound = R.raw.je_te_veux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        tvTime = findViewById(R.id.tvTime);
        tvPoints = findViewById(R.id.tvPoints);
        circleTimer = findViewById(R.id.circleTimer);

        btnPlay = findViewById(R.id.btnPlay);
        btnSound = findViewById(R.id.btnSound);
        btnSettings = findViewById(R.id.btn_timer_settings);

        ImageButton backBtn = findViewById(R.id.btn_back);

        backBtn.setOnClickListener(v -> finish());


        if (isTestMode) {
            tvTime.setText(String.format("00:%02d", testFocusSeconds));
        } else {
            tvTime.setText(String.format("%02d:00", focusMinutes));
        }

        updatePointsUI();

        timerManager = new PomodoroTimer(tvTime, circleTimer, new PomodoroTimer.TimerListener() {
            @Override
            public void onTick(int secondsElapsed) {

                if (isFocusPhase) {
                    focusSecondsCompleted = secondsElapsed;

                }
            }

            @Override
            public void onFinish() {
                handlePhaseFinish();
            }
        });

        // ▶️ PLAY / STOP
        btnPlay.setOnClickListener(v -> {
            if (!isRunning) {
                // START TIMER
                btnPlay.setImageResource(R.drawable.timer_end_btn);
                startSession();
                isRunning = true;
            } else {
                btnPlay.setImageResource(R.drawable.timer_start_btn);
                new AlertDialog.Builder(this)
                        .setTitle("Stop Timer?")
                        .setMessage("Are you sure you want to stop? Your progress for this round will be reset.")
                        .setPositiveButton("Stop", (dialog, which) -> {
                            stopAndResetTimer();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });



        // 🔊 SOUND
        btnSound.setOnClickListener(v -> {
            soundOn = !soundOn;

            if (soundOn) {
                btnSound.setImageResource(R.drawable.sound_on_btn);
                startSound();
            } else {
                btnSound.setImageResource(R.drawable.sound_off_btn);
                stopSound();
            }
        });

        // ⚙️ SETTINGS MENU
        btnSettings.setOnClickListener(v -> showSettingsMenu());
    }

    private void startSession() {
        currentRound = 1;
        isFocusPhase = true;
        startNextPhase();
    }

    private void startNextPhase() {
        focusSecondsCompleted = 0;

        int timeInSeconds;

        if (isTestMode) {
            timeInSeconds = isFocusPhase ? testFocusSeconds : testBreakSeconds;
        } else {
            int minutes = isFocusPhase ? focusMinutes : breakMinutes;
            timeInSeconds = minutes * 60;
        }

        timerManager.startTimer(timeInSeconds);
//        int time = isFocusPhase ? focusMinutes : breakMinutes;
//        timerManager.startTimer(time * 60);

        if (soundOn) startSound();
    }

    private void handlePhaseFinish() {

        if (isFocusPhase) {
            int minutes;

            if (isTestMode) {
                minutes = focusSecondsCompleted; // 1 point per second (for testing)
            } else {
                minutes = focusSecondsCompleted / 60;
            }
//            int minutes = focusSecondsCompleted / 60;

            int points = minutes + 10;

            saveSession(true, minutes);
            updateUserPoints(points);
            updatePointsUI();

            showBreakDialog();

        } else {
            currentRound++;

            if (currentRound > rounds) {
                showSessionCompleteDialog();
                return;
            }

            showFocusDialog();
        }

        isFocusPhase = !isFocusPhase;
        startNextPhase();
    }

    // ===================== SETTINGS =====================

    private void showSettingsMenu() {
        PopupMenu menu = new PopupMenu(this, btnSettings);

        menu.getMenu().add("Customize Timer");
        menu.getMenu().add("Customize Sound");
        menu.getMenu().add("Customize Background");

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Customize Timer":
                    showCustomizeDialog();
                    break;
                case "Customize Sound":
                    showSoundDialog();
                    break;
                case "Customize Background":
                    showBackgroundDialog();
                    break;
            }
            return true;
        });

        menu.show();
    }
    private void stopAndResetTimer() {
        // 1. Stop technical components
        timerManager.stop();
        stopSound();
        isRunning = false;

        // 2. Update Toggle Button
        btnPlay.setImageResource(R.drawable.timer_start_btn);

        // 3. Reset UI Visuals
        circleTimer.setProgress(100); // Reset progress bar to full (or 0 depending on your logic)

        // 4. Reset Text based on current mode
        if (isTestMode) {
            tvTime.setText(String.format("00:%02d", testFocusSeconds));
        } else {
            int displayMins = isFocusPhase ? focusMinutes : breakMinutes;
            tvTime.setText(String.format("%02d:00", displayMins));
        }

        Toast.makeText(this, "Timer Reset", Toast.LENGTH_SHORT).show();
    }

    private void showSoundDialog() {

        String[] sounds = {"je te veux", "None"};

        new AlertDialog.Builder(this)
                .setTitle("Choose Sound")
                .setItems(sounds, (dialog, which) -> {

                    stopSound();

                    if (which == 0) selectedSound = R.raw.je_te_veux;
//                    else if (which == 1) selectedSound = R.raw.cafe;
                    else return;

                    if (soundOn) startSound();
                })
                .show();
    }

    private void showBackgroundDialog() {

        DatabaseHelper db = new DatabaseHelper(this);
        ArrayList<ShopItem> owned = db.getShopItemsOwned(currentUserID);

        String[] names = new String[owned.size()];

        for (int i = 0; i < owned.size(); i++) {
            names[i] = owned.get(i).getItemName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Choose Background")
                .setItems(names, (dialog, which) -> {

                    ShopItem item = owned.get(which);

                    try {
                        // 🔥 Load image from assets
                        InputStream inputStream = getAssets().open(item.getImagePath());
                        Drawable drawable = Drawable.createFromStream(inputStream, null);

                        ImageView bg = findViewById(R.id.bgImage);
                        bg.setImageDrawable(drawable);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showCustomizeDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_customize_time, null);

        Spinner spFocus = view.findViewById(R.id.spFocus);
        Spinner spBreak = view.findViewById(R.id.spBreak);
        Spinner spRounds = view.findViewById(R.id.spRounds);
        Button btnSave = view.findViewById(R.id.btn_save);


        Integer[] focusOptions = {1,3,15, 20, 25, 30, 45};
        Integer[] breakOptions = {1,5, 10, 15};
        Integer[] roundOptions = {1, 2, 3, 4, 5};

        spFocus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, focusOptions));
        spBreak.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, breakOptions));
        spRounds.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roundOptions));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            focusMinutes = (int) spFocus.getSelectedItem();
            breakMinutes = (int) spBreak.getSelectedItem();
            rounds = (int) spRounds.getSelectedItem();

            if (isTestMode) {
                int display = isFocusPhase ? testFocusSeconds : testBreakSeconds;
                tvTime.setText(String.format("00:%02d", display));
            } else {
                int display = isFocusPhase ? focusMinutes : breakMinutes;
                tvTime.setText(String.format("%02d:00", display));
            }

//            int displayTime = isFocusPhase ? focusMinutes : breakMinutes;
//            tvTime.setText(String.format("%02d:00", displayTime));
            circleTimer.setProgress(100);
            dialog.dismiss();
        });
    }

    // ===================== SOUND =====================

    private void startSound() {
        stopSound();
        mediaPlayer = MediaPlayer.create(this, selectedSound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ===================== DB =====================

    private void saveSession(boolean completed, int minutes) {
        String date = java.time.LocalDate.now().toString();
        DatabaseHelper db = new DatabaseHelper(this);
        db.insertStudySession(db.currentUserId, date, minutes, completed ? 1 : 0);
    }

    private void updateUserPoints(int points) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.addPointsToUser(db.currentUserId, points);
    }

    private void updatePointsUI() {
        DatabaseHelper db = new DatabaseHelper(this);
        int points = db.getUserPoints(db.currentUserId);
        tvPoints.setText(String.valueOf(points));
    }

    // ===================== DIALOGS =====================

    private void showBreakDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Break Time 🌿")
                .setMessage("Rest time!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showFocusDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Focus Time 📚")
                .setMessage("Next round!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showSessionCompleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("All Done 🎉")
                .setMessage("Great job!")
                .setPositiveButton("Yay!", null)
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRunning) {
            timerManager.stop();
            stopSound();
            isRunning = false;
            btnPlay.setImageResource(R.drawable.timer_start_btn);
        }
    }
}