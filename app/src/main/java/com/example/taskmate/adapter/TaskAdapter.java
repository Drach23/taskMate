package com.example.taskmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.R;
import com.example.taskmate.data.TaskModel;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.TextView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskModel> taskList = new ArrayList<>();

    public void setTasks(List<TaskModel> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_recycler_view_row, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        TaskModel task = taskList.get(position);

        // Título
        holder.tvTitle.setText(task.getTitle());

        // Formatear fecha (long -> dd/MM/yyyy)
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String formattedDate =
                sdf.format(new Date(task.getDueDate()));

        holder.tvDueDate.setText("Vence: " + formattedDate);

        // Estado checkbox
        holder.checkTask.setChecked(task.isCompleted());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDueDate;
        MaterialCheckBox checkTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.textTaskTitle);
            tvDueDate = itemView.findViewById(R.id.textTaskDueDate);
            checkTask = itemView.findViewById(R.id.checkTask);
        }
    }
}
