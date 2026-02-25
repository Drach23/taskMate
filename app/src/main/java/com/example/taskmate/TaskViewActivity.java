package com.example.taskmate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.adapter.TaskAdapter;
import com.example.taskmate.data.AppDatabase;
import com.example.taskmate.data.CollectionModel;
import com.example.taskmate.data.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TaskViewActivity extends AppCompatActivity {

    private CollectionViewModel viewModel;
    private int collectionId;

    private int backgroundColor;
    private int textColor;

    private EditText etCollectionTitle;
    private CollectionModel currentCollection;

    private TaskAdapter activeAdapter;
    private TaskAdapter completedAdapter;

    private AppDatabase database;

    private boolean completedVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        EdgeToEdge.enable(this);

        etCollectionTitle = findViewById(R.id.taskCollectionTitle);

        collectionId = getIntent().getIntExtra("collectionId", -1);
        if (collectionId == -1) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this)
                .get(CollectionViewModel.class);

        database = AppDatabase.getInstance(this);

        RecyclerView recyclerActive = findViewById(R.id.recyclerActive);
        RecyclerView recyclerCompleted = findViewById(R.id.recyclerCompleted);
        LinearLayout headerCompleted = findViewById(R.id.layoutCompletedHeader);

        recyclerActive.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(this));

        activeAdapter = new TaskAdapter();
        completedAdapter = new TaskAdapter();

        recyclerActive.setAdapter(activeAdapter);
        recyclerCompleted.setAdapter(completedAdapter);

        headerCompleted.setOnClickListener(v -> {
            completedVisible = !completedVisible;
            recyclerCompleted.setVisibility(
                    completedVisible ? View.VISIBLE : View.GONE
            );
        });

        activeAdapter.setOnTaskLongClickListener((task, position) -> {

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar tarea")
                    .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {

                        Executors.newSingleThreadExecutor().execute(() -> {

                            database.taskDao().delete(task);

                            viewModel.updateTaskCount(collectionId);
                        });

                        showMessage("Tarea eliminada");
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        activeAdapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(TaskViewActivity.this, TaskDetailActivity.class);
            intent.putExtra("taskId", task.getId());
            startActivity(intent);
        });

        completedAdapter.setOnTaskClickListener(task -> {
            Intent intent = new Intent(TaskViewActivity.this, TaskDetailActivity.class);
            intent.putExtra("taskId", task.getId());
            startActivity(intent);
        });

        viewModel.getCollection(collectionId)
                .observe(this, collection -> {

                    if (collection == null) {
                        finish();
                        return;
                    }

                    currentCollection = collection;

                    etCollectionTitle.setText(collection.getTitle());

                    applyTheme(collection.getColor());

                    etCollectionTitle.setBackgroundColor(backgroundColor);
                    etCollectionTitle.setTextColor(textColor);

                    activeAdapter.setTheme(backgroundColor, textColor);
                    completedAdapter.setTheme(backgroundColor, textColor);
                });

        database.taskDao()
                .getActiveTasks(collectionId)
                .observe(this, tasks -> {
                    activeAdapter.setTasks(tasks);
                });

        database.taskDao()
                .getCompletedTasks(collectionId)
                .observe(this, tasks -> {
                    completedAdapter.setTasks(tasks);
                });

        etCollectionTitle.setOnClickListener(v -> showEditCollectionDialog());

        FloatingActionButton fab = findViewById(R.id.taskFabAdd);
        fab.setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modal_task, null);

        EditText etTitle = view.findViewById(R.id.etTaskTitle);
        EditText etDueDate = view.findViewById(R.id.etTaskDueDate);
        EditText etLocation = view.findViewById(R.id.etTaskAddress);

        etTitle.setTextColor(textColor);
        etDueDate.setTextColor(textColor);
        etLocation.setTextColor(textColor); // NUEVO

        final long[] selectedDate = {0};

        etDueDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (datePicker, year, month, day) -> {

                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, day);
                        selected.set(Calendar.HOUR_OF_DAY, 0);
                        selected.set(Calendar.MINUTE, 0);
                        selected.set(Calendar.SECOND, 0);

                        selectedDate[0] = selected.getTimeInMillis();

                        SimpleDateFormat sdf =
                                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                        etDueDate.setText(
                                sdf.format(new Date(selectedDate[0]))
                        );
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.getDatePicker()
                    .setMinDate(System.currentTimeMillis() - 1000);

            datePickerDialog.show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Nueva Tarea")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String title = etTitle.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();

                    if (title.isEmpty() || selectedDate[0] == 0 || location.isEmpty()) {
                        showMessage("Completa todos los campos");
                        return;
                    }

                    Executors.newSingleThreadExecutor().execute(() -> {

                        try {
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocationName(location, 1);

                            if (addresses == null || addresses.isEmpty()) {
                                runOnUiThread(() ->
                                        showMessage("No se pudo encontrar la ubicación")
                                );
                                return;
                            }

                            Address address = addresses.get(0);
                            double latitude = address.getLatitude();
                            double longitude = address.getLongitude();

                            TaskModel task = new TaskModel(
                                    title,
                                    "", // descripción igual
                                    selectedDate[0],
                                    false,
                                    collectionId,
                                    location,
                                    latitude,
                                    longitude
                            );

                            database.taskDao().insert(task);
                            viewModel.updateTaskCount(collectionId);

                            runOnUiThread(() ->
                                    showMessage("Tarea guardada con ubicación")
                            );

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() ->
                                    showMessage("Error al obtener coordenadas")
                            );
                        }
                    });

                });
        new AlertDialog.Builder(this)
                .setTitle("Nueva Tarea")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String title = etTitle.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();

                    if (title.isEmpty() || selectedDate[0] == 0 || location.isEmpty()) {
                        showMessage("Completa todos los campos");
                        return;
                    }

                    Executors.newSingleThreadExecutor().execute(() -> {

                        try {
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocationName(location, 1);

                            if (addresses == null || addresses.isEmpty()) {
                                runOnUiThread(() ->
                                        showMessage("No se pudo encontrar la ubicación")
                                );
                                return;
                            }

                            Address address = addresses.get(0);
                            double latitude = address.getLatitude();
                            double longitude = address.getLongitude();

                            TaskModel task = new TaskModel(
                                    title,
                                    "",
                                    selectedDate[0],
                                    false,
                                    collectionId,
                                    location,
                                    latitude,
                                    longitude
                            );

                            database.taskDao().insert(task);

                            runOnUiThread(() ->
                                    showMessage("Tarea guardada con ubicación")
                            );

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() ->
                                    showMessage("Error al obtener coordenadas")
                            );
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showEditCollectionDialog() {

        if (currentCollection == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar colección");

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modal_collection, null);

        builder.setView(view);

        EditText etName = view.findViewById(R.id.etCollectionName);
        Spinner spinner = view.findViewById(R.id.spinnerColor);

        etName.setText(currentCollection.getTitle());

        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.collection_colors,
                        android.R.layout.simple_spinner_item
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(
                getPositionFromTheme(currentCollection.getColor())
        );

        builder.setPositiveButton("Guardar", (dialog, which) -> {

            String newName = etName.getText().toString().trim();

            if (newName.length() < 3) {
                showMessage("El nombre debe tener al menos 3 caracteres");
                return;
            }

            String newTheme =
                    getCollectionThemeFromPosition(
                            spinner.getSelectedItemPosition()
                    );

            currentCollection.setTitle(newName);
            currentCollection.setColor(newTheme);

            viewModel.update(currentCollection);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void applyTheme(String theme) {

        switch (theme) {

            case "purple":
                backgroundColor = getColor(R.color.purple_collection_bg);
                textColor = getColor(R.color.purple_collections_txt);
                break;

            case "blue":
                backgroundColor = getColor(R.color.blue_collection_bg);
                textColor = getColor(R.color.blue_collection_txt);
                break;

            case "red":
                backgroundColor = getColor(R.color.red_collection_bg);
                textColor = getColor(R.color.red_collection_txt);
                break;

            case "green":
                backgroundColor = getColor(R.color.green_collection_bg);
                textColor = getColor(R.color.green_collection_txt);
                break;

            case "orange":
                backgroundColor = getColor(R.color.orange_collection_bg);
                textColor = getColor(R.color.orange_collection_txt);
                break;

            default:
                backgroundColor = getColor(R.color.purple_collection_bg);
                textColor = getColor(R.color.purple_collections_txt);
        }
    }

    private int getPositionFromTheme(String theme) {
        switch (theme) {
            case "purple": return 0;
            case "blue": return 1;
            case "red": return 2;
            case "green": return 3;
            case "orange": return 4;
            default: return 0;
        }
    }

    private void getLocation(String direccion, TaskModel task) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);

            if (direcciones != null && !direcciones.isEmpty()) {
                Address address = direcciones.get(0);
                task.setLatitude(address.getLatitude());
                task.setLongitude(address.getLongitude());
            } else {
                Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCollectionThemeFromPosition(int position) {
        switch (position) {
            case 0: return "purple";
            case 1: return "blue";
            case 2: return "red";
            case 3: return "green";
            case 4: return "orange";
            default: return "purple";
        }
    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
