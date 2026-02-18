package com.example.taskmate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmate.data.AppDatabase;
import com.example.taskmate.data.TaskModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private AppDatabase database;
    private TaskModel currentTask;

    private TextView tvTitle;
    private TextView tvDate;
    private EditText etDescription;
    private CheckBox checkCompleted;
    private Button btnSave;
    private Button btnDelete;

    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        database = AppDatabase.getInstance(this);

        tvTitle = findViewById(R.id.detailTitle);
        tvDate = findViewById(R.id.detailDate);
        etDescription = findViewById(R.id.detailDescription);
        checkCompleted = findViewById(R.id.detailCompleted);
        btnSave = findViewById(R.id.buttonSave);
        btnDelete = findViewById(R.id.buttonDelete);

        taskId = getIntent().getIntExtra("taskId", -1);

        if (taskId == -1) {
            finish();
            return;
        }

        loadTask();

        btnSave.setOnClickListener(v -> updateTask());

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadTask() {

        Executors.newSingleThreadExecutor().execute(() -> {

            currentTask = database.taskDao().getTaskById(taskId);

            runOnUiThread(() -> {

                if (currentTask == null) {
                    finish();
                    return;
                }

                tvTitle.setText(currentTask.getTitle());

                SimpleDateFormat sdf =
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                tvDate.setText(
                        sdf.format(new Date(currentTask.getDueDate()))
                );

                etDescription.setText(currentTask.getDescription());
                checkCompleted.setChecked(currentTask.isCompleted());
            });
        });
    }

    private void updateTask() {

        if (currentTask == null) return;

        currentTask.setDescription(
                etDescription.getText().toString().trim()
        );

        currentTask.setCompleted(checkCompleted.isChecked());

        Executors.newSingleThreadExecutor().execute(() ->
                database.taskDao().update(currentTask)
        );

        Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_LONG).show();
        finish();
    }

    private void confirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que quieres eliminar esta tarea?")
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    Executors.newSingleThreadExecutor().execute(() ->
                            database.taskDao().delete(currentTask)
                    );

                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
