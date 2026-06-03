package com.example.betweenus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.R;
import com.example.betweenus.model.ScheduleItem;

import java.util.List;

// PURPOSE:
// Displays user's daily schedule inside ProfileFragment.
//
// WHAT THIS FILE SHOULD DO:
// - Inflate item_schedule.xml
// - Bind time + activity title

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private int[] taskColors;
    private List<ScheduleItem> scheduleList;

    public ScheduleAdapter(List<ScheduleItem> scheduleList, int[] colors) {
        this.scheduleList = scheduleList;
        this.taskColors = colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);
        holder.time.setText(item.getTime());
        holder.title.setText(item.getTitle());
        if (taskColors != null && taskColors.length > 0 && holder.itemView instanceof CardView) {
            ((CardView) holder.itemView).setCardBackgroundColor(taskColors[position % taskColors.length]);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, title;

        public ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.scheduleTime);
            title = itemView.findViewById(R.id.scheduleTitle);
        }
    }
}
