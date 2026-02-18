package com.example.taskmate;

import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private AppDatabase database;
    private TaskModel currentTask;

    private EditText etTitle;
    private TextView tvDate;
    private EditText etDescription;
    private CheckBox checkCompleted;
    private Button btnSave;
    private Button btnDelete;

    private int taskId;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        database = AppDatabase.getInstance(this);

        etTitle = findViewById(R.id.detailTitle);
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

        tvDate.setOnClickListener(v -> openDatePicker());

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

                etTitle.setText(currentTask.getTitle());
                etDescription.setText(currentTask.getDescription());
                checkCompleted.setChecked(currentTask.isCompleted());

                selectedDate = currentTask.getDueDate();

                updateDateText();
            });
        });
    }

    private void openDatePicker() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 0, 0, 0);
                    selected.set(Calendar.MILLISECOND, 0);

                    selectedDate = selected.getTimeInMillis();
                    updateDateText();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Bloquear fechas pasadas
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        dialog.show();
    }

    private void updateDateText() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(selectedDate)));
    }

    private void updateTask() {

        if (currentTask == null) return;

        String newTitle = etTitle.getText().toString().trim();

        if (newTitle.isEmpty()) {
            etTitle.setError("El título no puede estar vacío");
            return;
        }

        currentTask.setTitle(newTitle);
        currentTask.setDescription(
                etDescription.getText().toString().trim()
        );
        currentTask.setCompleted(checkCompleted.isChecked());
        currentTask.setDueDate(selectedDate);

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
