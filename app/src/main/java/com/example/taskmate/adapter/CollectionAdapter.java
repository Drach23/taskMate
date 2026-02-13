package com.example.taskmate.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.R;
import com.example.taskmate.data.CollectionModel;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter
        extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private List<CollectionModel> data = new ArrayList<>();

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_recycler_view_row, parent, false);

        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {

        CollectionModel collection = data.get(position);

        holder.titleTextView.setText(collection.getTitle());

        holder.quantityTextView.setText(
                holder.itemView.getContext()
                        .getString(R.string.tasks_pending, collection.getQuantity())
        );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<CollectionModel> newData) {
        data = newData;
        notifyDataSetChanged();
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView quantityTextView;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            quantityTextView = itemView.findViewById(R.id.titleTextView2);
        }
    }
}
