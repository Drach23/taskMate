package com.example.taskmate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private AppDatabase database;
    private TaskModel currentTask;

    private EditText etTitle;
    private TextView tvDate;
    private EditText etDescription;
    private EditText etLocation;
    private CheckBox checkCompleted;
    private Button btnSave;
    private Button btnDelete;
    private Button btnRoute;

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
        etLocation = findViewById(R.id.detailLocation);
        checkCompleted = findViewById(R.id.detailCompleted);
        btnSave = findViewById(R.id.buttonSave);
        btnDelete = findViewById(R.id.buttonDelete);
        btnRoute = findViewById(R.id.btnCalculateRoute);

        taskId = getIntent().getIntExtra("taskId", -1);

        if (taskId == -1) {
            finish();
            return;
        }

        loadTask();

        tvDate.setOnClickListener(v -> openDatePicker());
        btnSave.setOnClickListener(v -> updateTask());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnRoute.setOnClickListener(v -> showDestinationSelector());
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
                etLocation.setText(currentTask.getAddress());
                checkCompleted.setChecked(currentTask.isCompleted());

                selectedDate = currentTask.getDueDate();
                updateDateText();
            });
        });
    }

    // =========================
    // NUEVA LÓGICA DE RUTA
    // =========================

    private void showDestinationSelector() {

        if (currentTask == null ||
                currentTask.getAddress() == null ||
                currentTask.getAddress().trim().isEmpty()) {

            Toast.makeText(this,
                    "La tarea actual no tiene ubicación válida",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {

            List<TaskModel> allTasks =
                    database.taskDao().getAllTasks();

            // Filtrar: quitar tarea actual
            List<TaskModel> filteredTasks = new ArrayList<>();
            for (TaskModel task : allTasks) {
                if (task.getId() != currentTask.getId()
                        && task.getAddress() != null
                        && !task.getAddress().trim().isEmpty()) {
                    filteredTasks.add(task);
                }
            }

            runOnUiThread(() -> {

                if (filteredTasks.isEmpty()) {
                    Toast.makeText(this,
                            "No hay otras tareas con ubicación",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] taskTitles = new String[filteredTasks.size()];
                for (int i = 0; i < filteredTasks.size(); i++) {
                    taskTitles[i] = filteredTasks.get(i).getTitle()
                            + " (" + filteredTasks.get(i).getAddress() + ")";
                }

                new AlertDialog.Builder(this)
                        .setTitle("Selecciona destino")
                        .setItems(taskTitles, (dialog, which) -> {

                            TaskModel selectedTask =
                                    filteredTasks.get(which);

                            openGoogleMaps(
                                    currentTask.getAddress(),
                                    selectedTask.getAddress()
                            );
                        })
                        .show();
            });
        });
    }

    private void openGoogleMaps(String origin, String destination) {

        String uri = "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + Uri.encode(origin)
                + "&destination=" + Uri.encode(destination)
                + "&travelmode=driving";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    // =========================
    // RESTO DEL CÓDIGO ORIGINAL
    // =========================

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
        String newLocation = etLocation.getText().toString().trim();

        if (newTitle.isEmpty()) {
            etTitle.setError("El título no puede estar vacío");
            return;
        }

        if (newLocation.isEmpty()) {
            etLocation.setError("La ubicación no puede estar vacía");
            return;
        }

        currentTask.setTitle(newTitle);
        currentTask.setDescription(
                etDescription.getText().toString().trim()
        );
        currentTask.setAddress(newLocation);
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