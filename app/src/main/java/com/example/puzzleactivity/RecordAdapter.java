package com.example.puzzleactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private ArrayList<Record> arrayList;
    private Context context;

    public RecordAdapter(ArrayList<Record> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_record, parent, false);
        RecordViewHolder holder = new RecordViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.tv_time.setText(String.valueOf(arrayList.get(position).getTime()));
        holder.tv_try.setText(String.valueOf(arrayList.get(position).getCount()));
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tv_try, tv_time, tv_try_label, tv_time_label;
        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_try = itemView.findViewById(R.id.tv_try);
            this.tv_time = itemView.findViewById(R.id.tv_time);
            this.tv_try_label = itemView.findViewById(R.id.tv_try_label);
            this.tv_time_label = itemView.findViewById(R.id.tv_time_label);
        }
    }
}
