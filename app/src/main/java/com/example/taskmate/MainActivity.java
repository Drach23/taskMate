package com.example.taskmate;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmate.adapter.CollectionAdapter;
import com.example.taskmate.data.CollectionModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private CollectionViewModel viewModel;
    private CollectionAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.headerToolbar);
        setSupportActionBar(toolbar);

        // Insets (respeta barra de estado)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // RecyclerView
        recyclerView = findViewById(R.id.collectionRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CollectionAdapter();
        recyclerView.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this)
                .get(CollectionViewModel.class);

        viewModel.getCollections().observe(this, collections -> {
            adapter.setData(collections);
        });

        adapter.setOnCollectionLongClickListener((collection, position) -> {

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar colección")
                    .setMessage("¿Estás seguro de que quieres eliminar esta colección?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        viewModel.delete(collection);
                        showMessage("Colección eliminada");
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // FAB
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> showAddCollectionDialog());
    }

    private void showAddCollectionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva colección");

        View view = getLayoutInflater()
                .inflate(R.layout.dialog_modal_collection, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etCollectionName);
        Spinner spinner = view.findViewById(R.id.spinnerColor);

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

        builder.setPositiveButton("Guardar", (dialog, which) -> {

            String name = etName.getText().toString().trim();
            if (name.length() < 3) {
                showMessage("La tarea debe tener al menos 3 caracteres");
                return;
            }
            int lineCount = name.split("\n").length;

            if (lineCount > 3) {
                showMessage("Máximo 3 saltos de línea permitidos");
                return;
            }

            int selectedPosition = spinner.getSelectedItemPosition();
            String selectedTheme = getCollectionThemeFromPosition(selectedPosition);

            if (!name.isEmpty()) {
                viewModel.insert(
                        new CollectionModel(name, 0, selectedTheme)
                );
            }

        });

        builder.setNegativeButton("Cancelar",
                (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private String getCollectionThemeFromPosition(int position) {

        switch (position) {
            case 0:
                return "purple";
            case 1:
                return "blue";
            case 2:
                return "red";
            case 3:
                return "green";
            case 4:
                return "orange";
            default:
                return "purple";
        }
    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
