package com.example.taskmate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.taskmate.data.AppDatabase;
import com.example.taskmate.data.TaskModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class PlanificationActivity extends AppCompatActivity {

    private AppDatabase database;
    private FusedLocationProviderClient fusedLocationClient;

    private ListView listView;
    private Button btnCalculate;

    private double userLat;
    private double userLon;

    private List<TaskModel> orderedTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planification);

        database = AppDatabase.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        listView = findViewById(R.id.listViewTasks);
        btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(v -> obtenerUbicacion());
    }

    private void obtenerUbicacion() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location == null) {
                        Toast.makeText(this,
                                "No se pudo obtener ubicación",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    userLat = location.getLatitude();
                    userLon = location.getLongitude();

                    cargarYOrdenarTareas();
                });
    }

    private void cargarYOrdenarTareas() {

        Executors.newSingleThreadExecutor().execute(() -> {

            List<TaskModel> allTasks = database.taskDao().getAllTasks();
            List<TaskModel> validTasks = new ArrayList<>();

            for (TaskModel task : allTasks) {
                if (!task.isCompleted()
                        && task.getLatitude() != 0.0
                        && task.getLongitude() != 0.0) {
                    validTasks.add(task);
                }
            }

            Collections.sort(validTasks, (t1, t2) -> {

                float[] r1 = new float[1];
                float[] r2 = new float[1];

                Location.distanceBetween(
                        userLat, userLon,
                        t1.getLatitude(), t1.getLongitude(),
                        r1);

                Location.distanceBetween(
                        userLat, userLon,
                        t2.getLatitude(), t2.getLongitude(),
                        r2);

                return Float.compare(r1[0], r2[0]);
            });

            orderedTasks = validTasks;

            runOnUiThread(() -> mostrarLista());
        });
    }

    private void mostrarLista() {

        if (orderedTasks.isEmpty()) {
            Toast.makeText(this,
                    "No hay tareas disponibles",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> nombres = new ArrayList<>();

        for (TaskModel task : orderedTasks) {

            float[] results = new float[1];

            Location.distanceBetween(
                    userLat, userLon,
                    task.getLatitude(), task.getLongitude(),
                    results);

            double km = results[0] / 1000.0;

            nombres.add(task.getTitle() + " - "
                    + String.format("%.2f km", km));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        nombres);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            abrirRutaCompleta();
        });
    }

    private void abrirRutaCompleta() {

        StringBuilder builder = new StringBuilder();

        builder.append(userLat).append(",").append(userLon);

        for (TaskModel task : orderedTasks) {
            builder.append("/")
                    .append(task.getLatitude())
                    .append(",")
                    .append(task.getLongitude());
        }

        Uri uri = Uri.parse("https://www.google.com/maps/dir/" + builder);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");

        startActivity(intent);
    }
}