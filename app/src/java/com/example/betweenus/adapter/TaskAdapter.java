package com.example.betweenus.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.R;
import com.example.betweenus.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onTaskDelete(Task task);
        void onTaskToggle(Task task, boolean isComplete);
        void onTaskEdit(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskActionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        
        holder.tvTitle.setText(task.getTaskGoal());
        String displayTime = getRelativeTime(task.getDueDate());
        holder.tvTime.setText(displayTime);

        int[] colors = {
                Color.parseColor("#F6A6A6"), // pink
                Color.parseColor("#FFD166"), // yellow
                Color.parseColor("#A8E6CF"), // green
                Color.parseColor("#F4A261")  // orange
        };

        holder.taskCard.setCardBackgroundColor(colors[position % colors.length]);

        holder.cbComplete.setOnCheckedChangeListener(null);
        holder.cbComplete.setChecked(task.isComplete());
        updateStrikethrough(holder.tvTitle, task.isComplete());

        // Use OnClickListener to handle user interaction and propagate to DB
        holder.cbComplete.setOnClickListener(v -> {
            boolean isChecked = holder.cbComplete.isChecked();
            updateStrikethrough(holder.tvTitle, isChecked);
            if (listener != null) {
                listener.onTaskToggle(task, isChecked);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskDelete(task);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskEdit(task);
            }
        });
    }

    private void updateStrikethrough(TextView textView, boolean isComplete) {
        if (isComplete) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setAlpha(0.6f);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime;
        ImageButton btnDelete;
        CheckBox cbComplete;
        CardView taskCard;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.taskTitle);
            tvTime = itemView.findViewById(R.id.taskTime);
            btnDelete = itemView.findViewById(R.id.deleteTaskButton);
            taskCard = itemView.findViewById(R.id.taskCard);
            cbComplete = itemView.findViewById(R.id.task_complete_checkbox);
        }
    }

    private String getRelativeTime(String dateStr) {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = dbFormat.parse(dateStr);
            if (date == null) return dateStr;

            Calendar now = Calendar.getInstance();
            Calendar taskCal = Calendar.getInstance();
            taskCal.setTime(date);

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            SimpleDateFormat fullFormat = new SimpleDateFormat("MMM, d", Locale.getDefault());

            String timePart = timeFormat.format(date).toLowerCase();

            if (now.get(Calendar.YEAR) == taskCal.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == taskCal.get(Calendar.DAY_OF_YEAR)) {
                return "Today " + timePart;
            }

            now.add(Calendar.DAY_OF_YEAR, 1);
            if (now.get(Calendar.YEAR) == taskCal.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == taskCal.get(Calendar.DAY_OF_YEAR)) {
                return "Tmr " + timePart;
            }

            return fullFormat.format(date) + " " + timePart;
        } catch (Exception e) {
            return dateStr;
        }
    }
}
