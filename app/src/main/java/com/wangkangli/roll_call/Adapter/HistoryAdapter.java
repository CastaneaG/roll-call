package com.wangkangli.roll_call.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangkangli.roll_call.Json.BRHistory;
import com.wangkangli.roll_call.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<BRHistory> mBRHistory;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View BRHView;
        TextView historystate;
        TextView historytime;

        public ViewHolder(View itemView) {
            super(itemView);
            BRHView = itemView;
            historystate = itemView.findViewById(R.id.tv_state);
            historytime = itemView.findViewById(R.id.tv_time);
        }
    }

    public HistoryAdapter(List<BRHistory> mBRHistory) {
        this.mBRHistory = mBRHistory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brhistory_item,viewGroup,false);
        HistoryAdapter.ViewHolder holder = new HistoryAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        BRHistory brHistory = mBRHistory.get(i);
        if (brHistory.getFflag().equals("6")) {
            viewHolder.historystate.setText("成功签到");
            viewHolder.historytime.setText(brHistory.getFqdsj());
            viewHolder.historystate.setTextColor(Color.parseColor("#32CD32"));
        }else{
            viewHolder.historystate.setText("在外签到");
            viewHolder.historytime.setText(brHistory.getFqdsj());
            viewHolder.historystate.setTextColor(Color.parseColor("#DC143C"));
        }

    }


    @Override
    public int getItemCount() {
        return mBRHistory.size();
    }
}
