package com.example.betweenus.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.activities.PomodoroActivity;
import com.example.betweenus.activities.ReportActivity;
import com.example.betweenus.adapter.TaskAdapter;
import com.example.betweenus.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProductivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private EditText etTaskInput;
    private Button btnAddTask;
    private SharedTodoViewModel todoViewModel;

    private ImageButton btnSuggestion, btnPomodoro, btnStats;

    public ProductivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productivity, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        if (getArguments() != null) {
            dbHelper.currentUserId = getArguments().getInt("USER_ID", 1);
        }
        
        taskList = new ArrayList<>();
        todoViewModel = new ViewModelProvider(requireActivity()).get(SharedTodoViewModel.class);

        recyclerView = view.findViewById(R.id.taskRecyclerView);
        etTaskInput = view.findViewById(R.id.et_task_input);
        btnAddTask = view.findViewById(R.id.btn_add_task);

        btnSuggestion = view.findViewById(R.id.btn_suggestion);
        btnPomodoro = view.findViewById(R.id.btn_pomodoro);

        btnSuggestion.setOnClickListener(v -> {
            String suggestion = dbHelper.getRandomSuggestion();
            if (suggestion != null) {
                showSuggestionDialog(suggestion);
            } else {
                Toast.makeText(getContext(), "No suggestions found", Toast.LENGTH_SHORT).show();
            }
        });

        if (!dbHelper.isUserPremium(dbHelper, dbHelper.currentUserId)) {
            btnPomodoro.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            btnPomodoro.setAlpha(0.6f);
        } else {
            // Ensure it looks normal for Premium users
            btnPomodoro.clearColorFilter();
            btnPomodoro.setAlpha(1.0f);
            btnPomodoro.setEnabled(true);
        }

        btnPomodoro.setOnClickListener(v -> {
            if (DatabaseHelper.isUserPremium(dbHelper, dbHelper.currentUserId)) {
                Intent intent = new Intent(getActivity(), PomodoroActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Premium feature 🌟", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onTaskDelete(Task task) {
                dbHelper.deleteTask(task.getTaskId());
                loadTasksMain(); // Refresh main list
                todoViewModel.loadTasks(dbHelper, dbHelper.currentUserId); // Refresh widget!
                Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTaskToggle(Task task, boolean isComplete) {
                dbHelper.toggleTaskStatus(task.getTaskId(), isComplete);
                // We don't necessarily need to reload everything if we just want to keep the state,
                // but for consistency with the widget and other fragments, we refresh the ViewModel.
                todoViewModel.loadTasks(dbHelper, dbHelper.currentUserId);
                // If we want to keep completed tasks in the list, we might not need loadTasksMain() here 
                // if the adapter handles local state, but since loadTasksMain filters, we should be careful.
                loadTasksMain(); 
            }

            @Override
            public void onTaskEdit(Task task) {
                showEditTaskDialog(task);
            }
        });
        recyclerView.setAdapter(taskAdapter);

        // Observe the ViewModel so we stay in sync with the HomeFragment's preview
        todoViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            // Update local taskList and adapter
            // Note: ProductivityFragment might want to show ALL tasks or just incomplete ones.
            // If it shows ALL, we can just use the ViewModel's list directly.
            // For now, let's stick to the current logic of loadTasksMain but fix the bug.
            loadTasksMain();
        });

        loadTasksMain();

        btnAddTask.setOnClickListener(v -> {
            String goal = etTaskInput.getText().toString().trim();
            if (!goal.isEmpty()) {
                String dueDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                long result = dbHelper.createTask(dbHelper.currentUserId, goal, 0, dueDate);
                if (result != -1) {
                    etTaskInput.setText("");
                    todoViewModel.loadTasks(dbHelper, dbHelper.currentUserId);
                    Toast.makeText(getContext(), "Task Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter a task", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void loadTasksMain() {
        taskList.clear();
        Cursor cursor = dbHelper.getUserTasks(String.valueOf(dbHelper.currentUserId));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("TaskID"));
                int makerId = cursor.getInt(cursor.getColumnIndexOrThrow("TaskCreator"));
                String goal = cursor.getString(cursor.getColumnIndexOrThrow("TaskGoal"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("DueDate"));
                int complete = cursor.getInt(cursor.getColumnIndexOrThrow("IsComplete"));

                // BUG FIX: Actually use the 'complete' status from DB
                // AND: Decided to show all tasks in ProductivityFragment so users can see/uncheck them
                taskList.add(new Task(id, makerId, goal, date, complete == 1));
                
            } while (cursor.moveToNext());
            cursor.close();
        }
        taskAdapter.updateTasks(taskList);
    }

    private void showSuggestionDialog(String suggestion) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_ui, null);

        TextView tvSuggestion = view.findViewById(R.id.tv_suggestion_text);
        Button btnClose = view.findViewById(R.id.btn_close_dialog);

        tvSuggestion.setText(suggestion);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        dialog.show();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(800,ViewGroup.LayoutParams.WRAP_CONTENT);

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void showEditTaskDialog(Task task) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_task, null);
        EditText etGoal = view.findViewById(R.id.et_edit_task_goal);
        EditText etDueDate = view.findViewById(R.id.et_edit_task_due_date);

        etGoal.setText(task.getTaskGoal());
        etDueDate.setText(task.getDueDate());

        final Calendar selectedCalendar = Calendar.getInstance();

        etDueDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(
                    new ContextThemeWrapper(getContext(), R.style.MyPickerTheme),
                    (view1, year, month, day) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, day);

                        TimePickerDialog timePicker = new TimePickerDialog(
                                new ContextThemeWrapper(getContext(), R.style.MyPickerTheme),
                                (view2, hour, minute) -> {
                                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
                                    selectedCalendar.set(Calendar.MINUTE, minute);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                    etDueDate.setText(sdf.format(selectedCalendar.getTime()));
                                },
                                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                                selectedCalendar.get(Calendar.MINUTE),
                                false
                        );
                        timePicker.show();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();

        view.findViewById(R.id.btn_cancel_edit).setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.btn_save_task).setOnClickListener(v -> {
            String newGoal = etGoal.getText().toString().trim();
            String newDueDate = etDueDate.getText().toString().trim();

            if (!newGoal.isEmpty() && !newDueDate.isEmpty()) {
                dbHelper.updateTaskGoal(task.getTaskId(), newGoal);
                dbHelper.updateTaskDueDate(task.getTaskId(), newDueDate);
                todoViewModel.loadTasks(dbHelper, dbHelper.currentUserId);
                dialog.dismiss();
                Toast.makeText(getContext(), "Task updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
