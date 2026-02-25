package com.example.taskmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    // CLICK INTERFACES
    // =========================

    public interface OnTaskClickListener {
        void onClick(TaskModel task);
    }

    public interface OnTaskLongClickListener {
        void onLongClick(TaskModel task, int position);
    }

    public interface OnTaskCheckedListener {
        void onTaskChecked(TaskModel task, boolean isChecked);
    }

    private OnTaskClickListener clickListener;
    private OnTaskLongClickListener longClickListener;
    private OnTaskCheckedListener checkedListener;

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnTaskLongClickListener(OnTaskLongClickListener listener) {
        this.longClickListener = listener;
    }



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

        holder.tvTitle.setText(task.getTitle());



        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        holder.tvDate.setText(
                sdf.format(new Date(task.getDueDate()))
        );

        // ===== CHECKBOX =====
        holder.checkCompleted.setOnCheckedChangeListener(null);
        holder.checkCompleted.setChecked(task.isCompleted());

        holder.checkCompleted.setEnabled(false);
        holder.checkCompleted.setClickable(false);

        holder.checkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {

            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            if (checkedListener != null) {
                checkedListener.onTaskChecked(
                        taskList.get(currentPosition),
                        isChecked
                );
            }
        });

        // ===== THEME =====
        if (backgroundColor != 0) {
            holder.cardView.setCardBackgroundColor(backgroundColor);
        }

        if (textColor != 0) {
            holder.tvTitle.setTextColor(textColor);
            holder.tvDate.setTextColor(textColor);
        }

        // ===== CLICK NORMAL =====
        holder.cardView.setOnClickListener(v -> {

            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            if (clickListener != null) {
                clickListener.onClick(taskList.get(currentPosition));
            }
        });

        // ===== LONG CLICK =====
        holder.cardView.setOnLongClickListener(v -> {

            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return true;

            if (longClickListener != null) {
                longClickListener.onLongClick(
                        taskList.get(currentPosition),
                        currentPosition
                );
            }

            return true;
        });
    }
    // =========================
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // =========================
    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDate;
        CardView cardView;
        CheckBox checkCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.textTaskTitle);
            tvDate = itemView.findViewById(R.id.textTaskDueDate);
            cardView = itemView.findViewById(R.id.cardTask);
            checkCompleted = itemView.findViewById(R.id.checkTask);
        }
    }
}
