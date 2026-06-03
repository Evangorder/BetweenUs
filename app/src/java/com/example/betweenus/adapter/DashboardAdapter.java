//package com.example.betweenus.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.betweenus.R;
//import com.example.betweenus.model.DashboardMessage;
//
//import java.util.List;
//
//public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
//
//    private List<DashboardMessage> messages;
//
//    public DashboardAdapter(List<DashboardMessage> messages) {
//        this.messages = messages;
//    }
//
//    public void updateList(List<DashboardMessage> newList) {
//        messages = newList;
//        notifyDataSetChanged();
//    }
//
////    @Override
////    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(parent.getContext())
////                .inflate(R.layout.item_dashboard_message, parent, false);
////        return new ViewHolder(view);
////    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.txtMessage.setText(messages.get(position).getText());
//    }
//
//    @Override
//    public int getItemCount() {
//        return messages.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView txtMessage;
//        ViewHolder(View itemView) {
//            super(itemView);
//            txtMessage = itemView.findViewById(R.id.txtMessage);
//        }
//    }
//}
