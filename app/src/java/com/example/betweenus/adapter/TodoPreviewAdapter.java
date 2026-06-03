package com.example.betweenus.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.R;
import com.example.betweenus.model.Task;

import java.util.List;

public class TodoPreviewAdapter extends RecyclerView.Adapter<TodoPreviewAdapter.ViewHolder> {

    private List<Task> tasks;
    private OnTaskClickListener listener;
    // Your pastel palette
    private final int[] colors = {
            Color.parseColor("#F6A6A6"), // pink
            Color.parseColor("#FFD166"), // yellow
            Color.parseColor("#A8E6CF"), // green
            Color.parseColor("#F4A261")  // orange
    };

    public interface OnTaskClickListener {
        void onTaskToggle(Task task);
    }

    public TodoPreviewAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.txtGoal.setText(task.getTaskGoal());

        // 1. Position-based coloring
        holder.cardView.setCardBackgroundColor(colors[position % colors.length]);

        // 2. Handle Strikethrough and Checkbox state
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isComplete());

        if (task.isComplete()) {
            holder.txtGoal.setPaintFlags(holder.txtGoal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtGoal.setAlpha(0.5f); // Fade it out a bit
        } else {
            holder.txtGoal.setPaintFlags(holder.txtGoal.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtGoal.setAlpha(1.0f);
        }

        // 3. Sync Click: Toggle completion when clicked in widget
        View.OnClickListener toggleAction = v -> {
            if (listener != null) listener.onTaskToggle(task);
        };

        holder.itemView.setOnClickListener(toggleAction);
        holder.checkBox.setOnClickListener(toggleAction);
    }

    @Override
    public int getItemCount() {
        return Math.min(tasks.size(), 3);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtGoal;
        CheckBox checkBox;
        androidx.cardview.widget.CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            txtGoal = itemView.findViewById(R.id.txtTaskGoal);
            checkBox = itemView.findViewById(R.id.checkTodo);
            cardView = (androidx.cardview.widget.CardView) itemView.findViewById(R.id.taskCard);
        }
    }
}
