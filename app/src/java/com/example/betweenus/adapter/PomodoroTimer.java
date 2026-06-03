package com.example.betweenus.adapter;

import android.animation.ObjectAnimator;
import android.os.CountDownTimer;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PomodoroTimer {

    private CountDownTimer timer;
    private TextView tvTime;
    private ObjectAnimator progressAnimator;
    private ProgressBar progressBar;

    private int totalTime;
    private TimerListener listener;
    private boolean isRunning = false;

    public PomodoroTimer(TextView tvTime, ProgressBar progressBar, TimerListener listener) {
        this.tvTime = tvTime;
        this.progressBar = progressBar;
        this.listener = listener;
    }

    public void startTimer(int seconds) {

        if (timer != null) {
            timer.cancel();
        }

        totalTime = seconds;
        isRunning = true;

        progressBar.setProgress(100);
        progressBar.getProgressDrawable().setTint(0xFF6BCB77);

        // 🎯 SMOOTH ANIMATION (100 → 0)
        progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 100, 0);
        progressAnimator.setDuration(seconds * 1000L);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();

        timer = new CountDownTimer(seconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {

                int secondsLeft = (int) (millisUntilFinished / 1000);
                int secondsElapsed = totalTime - secondsLeft;

                if (listener != null) {
                    listener.onTick(secondsElapsed);
                }

                int min = secondsLeft / 60;
                int sec = secondsLeft % 60;

                tvTime.setText(String.format("%02d:%02d", min, sec));

                // ✅ FIXED progress
                int progress = (int) ((secondsLeft / (float) totalTime) * 100);
                progressBar.setProgress(progress);

                if (secondsLeft < 5) {
                    progressBar.getProgressDrawable().setTint(0xFFFF6B6B); // soft red
                }
            }

            public void onFinish() {
                isRunning = false;

                progressBar.setProgress(100);

                if (listener != null) {
                    listener.onFinish();
                }
            }
        }.start();
    }

    public void stop() {
        if (timer != null) timer.cancel();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public interface TimerListener {
        void onTick(int secondsElapsed);
        void onFinish();
    }
}