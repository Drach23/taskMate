package com.example.taskmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.R;
import com.example.taskmate.data.TaskModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskModel> taskList = new ArrayList<>();

    private int backgroundColor = 0;
    private int textColor = 0;

    // =========================
    // RECIBE COLORES DESDE ACTIVITY
    // =========================
    public void setTheme(int bgColor, int txtColor) {
        this.backgroundColor = bgColor;
        this.textColor = txtColor;
        notifyDataSetChanged();
    }

    // =========================
    // RECIBE LISTA DE TAREAS
    // =========================
    public void setTasks(List<TaskModel> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    // =========================
    // CREATE VIEW HOLDER
    // =========================
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_recycler_view_row, parent, false);

        return new TaskViewHolder(view);
    }

    // =========================
    // BIND VIEW HOLDER
    // =========================
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        TaskModel task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());

        // Formatear fecha
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        holder.tvDate.setText(
                sdf.format(new Date(task.getDueDate()))
        );

        // 🔥 Aplicar colores SOLO si ya existen
        if (backgroundColor != 0) {
            holder.cardView.setCardBackgroundColor(backgroundColor);
        }

        if (textColor != 0) {
            holder.tvTitle.setTextColor(textColor);
            holder.tvDate.setTextColor(textColor);
        }
    }

    // =========================
    // ITEM COUNT
    // =========================
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // =========================
    // VIEW HOLDER
    // =========================
    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDate;
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.textTaskTitle);
            tvDate = itemView.findViewById(R.id.textTaskDueDate);
            cardView = itemView.findViewById(R.id.cardTask);
        }
    }
}
