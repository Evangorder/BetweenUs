package com.example.betweenus.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

//    private int currentUserID = 1;

    private TextView tvTotal, tvToday, tvWeek, tvMonth, tvAvg, tvSuccess;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tvTotal = findViewById(R.id.tvTotal);
        tvToday = findViewById(R.id.tvToday);
        tvWeek = findViewById(R.id.tvWeek);
        tvMonth = findViewById(R.id.tvMonth);
        tvAvg = findViewById(R.id.tvAvg);
//        tvSuccess = findViewById(R.id.tvSuccess);

        barChart = findViewById(R.id.barChart);



        ImageButton backbtn = findViewById(R.id.btn_back);
        backbtn.setOnClickListener(v -> finish());


        loadData();
    }

    private void loadData() {

        DatabaseHelper db = new DatabaseHelper(this);

        int total = db.getTotalMinutes(db.currentUserId);
        int today = db.getTodayMinutes(db.currentUserId);
        int week = db.getWeekMinutes(db.currentUserId);
        int month = db.getMonthMinutes(db.currentUserId);

        float avg = db.getAverageStudyTime(db.currentUserId);
        float success = db.getSuccessRate(db.currentUserId) * 100;

        tvTotal.setText("🌸 Total: " + formatTime(total));
        tvToday.setText("📅 Today: " + formatTime(today));
        tvWeek.setText("🗓 Week: " + formatTime(week));
        tvMonth.setText("🌙 Month: " + formatTime(month));
        tvAvg.setText("📊 Avg Session: " + String.format("%.1f min", avg));
//        tvSuccess.setText("✨ Success Rate: " + String.format("%.0f%%", success));

        setupChart(db);
    }

    private String formatTime(int minutes) {
        int hrs = minutes / 60;
        int mins = minutes % 60;
        return hrs + "h " + mins + "m";
    }

    private void setupChart(DatabaseHelper db) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Last 7 days, today = 0
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            int minutes = db.getMinutesForDate(db.currentUserId, date.toString());

            entries.add(new BarEntry(6 - i, minutes)); // X = 0..6
            labels.add(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Minutes Studied");
        dataSet.setColors(new int[]{
                0xFFFFCDD2, // pastel red
                0xFFC8E6C9, // pastel green
                0xFFBBDEFB, // pastel blue
                0xFFFFF9C4, // pastel yellow
                0xFFD1C4E9, // pastel purple
                0xFFFFE0B2, // pastel orange
                0xFFB2DFDB  // pastel teal
        });
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        // X-axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                } else {
                    return "";
                }
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}