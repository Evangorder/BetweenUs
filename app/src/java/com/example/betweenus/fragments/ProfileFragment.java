package com.example.betweenus.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.activities.ProfileDetailActivity;
import com.example.betweenus.activities.ReportActivity;
import com.example.betweenus.activities.ShopActivity;
import com.example.betweenus.adapter.ChatListAdapter;
import com.example.betweenus.adapter.ScheduleAdapter;
import com.example.betweenus.model.ScheduleItem;
import com.example.betweenus.model.Task;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView friendsCount, addFriendLink, username, bio;

    private ImageButton shopbtn, btnStats;
    private ShapeableImageView pfp;
    private DatabaseHelper dbHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        username = view.findViewById(R.id.profileUsername);
        bio = view.findViewById(R.id.profileBio);
        pfp = view.findViewById(R.id.profileImage);
        friendsCount = view.findViewById(R.id.friendsCount);
        addFriendLink = view.findViewById(R.id.addFriendLink);
        shopbtn = view.findViewById(R.id.btn_shop);
        btnStats = view.findViewById(R.id.btn_stats);

        View weeklyScrollView = view.findViewById(R.id.weeklyScrollView);
        GridLayout weeklyContainer = view.findViewById(R.id.weeklyContainer);

        View calendarContainer = view.findViewById(R.id.googleCalendarContainer);
        View premiumPlaceholder = view.findViewById(R.id.premiumPlaceholder);
        TextView btnMonthly = view.findViewById(R.id.btn_monthly);
        TextView btnWeekly = view.findViewById(R.id.btn_Weekly);




        if (dbHelper.isUserPremium(dbHelper, dbHelper.currentUserId)) {
            // USER IS PREMIUM
            calendarContainer.setVisibility(View.VISIBLE);
            premiumPlaceholder.setVisibility(View.GONE);

            btnMonthly.setEnabled(true);
            btnWeekly.setEnabled(true);
        } else {
            shopbtn.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            shopbtn.setAlpha(0.6f);

            btnStats.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            btnStats.setAlpha(0.6f);

            calendarContainer.setVisibility(View.GONE);
            premiumPlaceholder.setVisibility(View.VISIBLE);

            btnMonthly.setEnabled(false);
            btnWeekly.setEnabled(false);
            btnMonthly.setAlpha(0.5f);
            btnWeekly.setAlpha(0.5f);


            view.findViewById(R.id.btn_get_premium).setOnClickListener(v -> {
                Toast.makeText(getContext(), "Redirecting to Premium Shop...", Toast.LENGTH_SHORT).show();
            });
        }


        btnMonthly.setOnClickListener(v -> {
            view.findViewById(R.id.materialCalendarView).setVisibility(View.VISIBLE);
            if (weeklyScrollView != null) weeklyScrollView.setVisibility(View.GONE);
        });

        btnWeekly.setOnClickListener(v -> {
            view.findViewById(R.id.materialCalendarView).setVisibility(View.GONE);
            if (weeklyScrollView != null) weeklyScrollView.setVisibility(View.VISIBLE);
            setupWeeklyView(view);
        });

        btnStats.setOnClickListener(v -> {

            if (dbHelper.isUserPremium(dbHelper, dbHelper.currentUserId)) {

                Intent intent = new Intent(getActivity(), ReportActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getContext(), "Premium feature 🌟", Toast.LENGTH_SHORT).show();
            }
        });

        loadUserData();

        friendsCount.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileDetailActivity.class));
        });

        addFriendLink.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileDetailActivity.class));
        });

        shopbtn.setOnClickListener(v -> {
            if (dbHelper.isUserPremium(dbHelper, dbHelper.currentUserId)) {

                Intent intent = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getContext(), "Premium feature 🌟", Toast.LENGTH_SHORT).show();
            }
        });

        if (dbHelper.isUserPremium(dbHelper, dbHelper.currentUserId)){
        setupPersonalSchedule(view);}



        return view;
    }


    private void loadUserData() {
        Cursor cursor = dbHelper.getUserByID(dbHelper.currentUserId);

        if (cursor != null && cursor.moveToFirst()) {

            String userName = cursor.getString(cursor.getColumnIndexOrThrow("UserName"));

            String userBio = cursor.getString(cursor.getColumnIndexOrThrow("UserBio"));

            int avatarId = cursor.getInt(cursor.getColumnIndexOrThrow("Avatar"));

            int friendNum = cursor.getInt(cursor.getColumnIndexOrThrow("FriendNumber"));

            // SET DATA
            username.setText(userName);
            bio.setText(userBio);
            friendsCount.setText(friendNum + " Friends");

            // SET PROFILE IMAGE
            setProfileImage(avatarId);

            cursor.close();
        }
    }

    private void setProfileImage(int avatarId) {
        int imageRes;

        switch (avatarId) {
            case 1:
                imageRes = R.drawable.bunny_profile;
                break;
            case 2:
                imageRes = R.drawable.giraffe_profile;
                break;
            case 3:
                imageRes = R.drawable.kitty_profile;
                break;
            case 4:
                imageRes = R.drawable.puppy_profile;
                break;
            default:
                imageRes = R.drawable.profilebtn_filled;
        }

        pfp.setImageResource(imageRes);

        // SET BORDER COLOR BASED ON AVATAR
        int borderColor = ChatListAdapter.getBoarder(avatarId);
        pfp.setStrokeColor(ColorStateList.valueOf(borderColor));
    }

    private void setupWeeklyView(View view) {

        GridLayout weeklyContainer = view.findViewById(R.id.weeklyContainer);
        if (weeklyContainer == null) return;

        weeklyContainer.removeAllViews(); // IMPORTANT

        List<Task> tasks = dbHelper.getTasksForUser(dbHelper.currentUserId);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        String today = dateFormat.format(new Date());

        for (int i = 0; i < 7; i++) {

            String dateStr = dateFormat.format(calendar.getTime());
            String dayName = dayFormat.format(calendar.getTime());

            // Inflate ONE day card
            View dayView = LayoutInflater.from(getContext()).inflate(R.layout.item_day_schedule, weeklyContainer, false);

            TextView dayTitle = dayView.findViewById(R.id.dayTitle);
            RecyclerView recycler = dayView.findViewById(R.id.dayRecycler);

            dayTitle.setText(dayName);

            // Filter tasks for this day
            List<ScheduleItem> dayTasks = new ArrayList<>();



            for (Task t : tasks) {
                if (t.getDueDate().startsWith(dateStr)) {
                    String formattedTime = "";
                    try {
                        // Use multiple formats to be safe
                        String[] formats = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm"};
                        Date date = null;
                        for (String f : formats) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat(f, Locale.getDefault());
                                date = sdf.parse(t.getDueDate());
                                if (date != null) break;
                            } catch (Exception ignored) {}
                        }

                        if (date != null) {
                            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                            formattedTime = timeFormat.format(date).toLowerCase();
                        } else {
                             // If no time is present in string, fallback to showing date only
                             formattedTime = t.getDueDate().split(" ")[0];
                        }
                    } catch (Exception e) {
                        formattedTime = dateStr;
                    }

                    dayTasks.add(new ScheduleItem(formattedTime, t.getTaskGoal()));
                }
            }

            int[] colors = {
                    Color.parseColor("#F6A6A6"), // pink
                    Color.parseColor("#FFD166"), // yellow
                    Color.parseColor("#A8E6CF"), // green
                    Color.parseColor("#F4A261")  // orange
            };

            TextView tvEmpty = dayView.findViewById(R.id.tv_empty_state);

            if (dayTasks.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
                dayTasks.sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()));
            }

            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler.setAdapter(new ScheduleAdapter(dayTasks, colors));

            LinearLayout container = (LinearLayout) dayView;


            if (dateStr.equals(today)) {
                dayTitle.setTextColor(Color.parseColor("#3290c8"));

                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.parseColor("#A2FFFEFE"));
                border.setCornerRadius(40);
                border.setStroke(5, Color.parseColor("#3290c8"));
                container.setBackground(border);

            } else {
                dayTitle.setTextColor(Color.BLACK);
                container.setBackgroundResource(R.drawable.image_round);
                container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A2FFFEFE")));
            }

            // Add to grid
            weeklyContainer.addView(dayView);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }


    private void setupPersonalSchedule(View view) {

        com.applandeo.materialcalendarview.CalendarView calendarView = view.findViewById(R.id.materialCalendarView);

        List<Task> tasks = dbHelper.getTasksForUser(dbHelper.currentUserId);


        // EVENTS (DOTS)
        List<EventDay> events = new ArrayList<>();

        for (Task task : tasks) {
            try {
                String[] parts = task.getDueDate().split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int day = Integer.parseInt(parts[2].split(" ")[0]); // Fix for dates with time

                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);

                // Use the dot indicator drawable
                events.add(new EventDay(cal, R.drawable.dot_indicator));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        calendarView.setEvents(events);

        // CLICK LISTENER
        calendarView.setOnDayClickListener(eventDay -> {

            Calendar clickedDay = eventDay.getCalendar();

            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", clickedDay.get(Calendar.YEAR), clickedDay.get(Calendar.MONTH) + 1, clickedDay.get(Calendar.DAY_OF_MONTH));

            StringBuilder sb = new StringBuilder();

            for (Task t : tasks) {
                if (t.getDueDate().startsWith(selectedDate)) {
                    sb.append("📌 ").append(t.getTaskGoal()).append("\n");
                }
            }

            String msg = sb.length() == 0 ? "No task for today!" : sb.toString().trim();

            String displayDate = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(clickedDay.getTime());

            new AlertDialog.Builder(requireContext()).setTitle("📅 " + displayDate).setMessage(msg).setPositiveButton("Got it!", null).show();
        });
    }


}
