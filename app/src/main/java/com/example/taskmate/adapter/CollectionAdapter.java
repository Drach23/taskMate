package com.example.taskmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.R;
import com.example.taskmate.data.CollectionModel;
import com.example.taskmate.data.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter
        extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private List<CollectionModel> data = new ArrayList<>();

    // Interface para Long Click
    public interface OnCollectionLongClickListener {
        void onLongClick(CollectionModel collection, int position);
    }
    //interface para click simple
    public interface OnCollectionClickListener {
        void onClick(CollectionModel collection);
    }

    private OnCollectionClickListener clickListener;

    public void setOnCollectionClickListener(OnCollectionClickListener listener) {
        this.clickListener = listener;
    }



    private OnCollectionLongClickListener longClickListener;

    public void setOnCollectionLongClickListener(OnCollectionLongClickListener listener) {
        this.longClickListener = listener;
    }

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

        AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());

        new Thread(() -> {

            int count = db.taskDao()
                    .getActiveTaskCount(collection.getId());

            holder.itemView.post(() ->
                    holder.quantityTextView.setText(
                            holder.itemView.getContext()
                                    .getString(R.string.tasks_pending, count)
                    )
            );

        }).start();

        applyTheme(holder, collection.getColor());

        // Long click listener
        holder.cardView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(collection, position);
            }
            return true;
        });

        //simple click listener
        holder.cardView.setOnClickListener(v -> {

            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition == RecyclerView.NO_POSITION) return;

            if (clickListener != null) {
                clickListener.onClick(data.get(currentPosition));
            }
        });

    }

    private void applyTheme(CollectionViewHolder holder, String theme) {

        Context context = holder.itemView.getContext();

        int backgroundColor;
        int textColor;

        switch (theme) {

            case "purple":
                backgroundColor = context.getColor(R.color.purple_collection_bg);
                textColor = context.getColor(R.color.purple_collections_txt);
                break;

            case "blue":
                backgroundColor = context.getColor(R.color.blue_collection_bg);
                textColor = context.getColor(R.color.blue_collection_txt);
                break;

            case "red":
                backgroundColor = context.getColor(R.color.red_collection_bg);
                textColor = context.getColor(R.color.red_collection_txt);
                break;

            case "green":
                backgroundColor = context.getColor(R.color.green_collection_bg);
                textColor = context.getColor(R.color.green_collection_txt);
                break;

            case "orange":
                backgroundColor = context.getColor(R.color.orange_collection_bg);
                textColor = context.getColor(R.color.orange_collection_txt);
                break;

            default:
                backgroundColor = context.getColor(R.color.purple_collection_bg);
                textColor = context.getColor(R.color.purple_collections_txt);
        }

        holder.cardView.setCardBackgroundColor(backgroundColor);
        holder.titleTextView.setTextColor(textColor);
        holder.quantityTextView.setTextColor(textColor);
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
        CardView cardView;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            quantityTextView = itemView.findViewById(R.id.titleTextView2);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
