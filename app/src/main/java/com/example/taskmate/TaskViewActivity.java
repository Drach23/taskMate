package com.example.taskmate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmate.data.CollectionModel;

public class TaskViewActivity extends AppCompatActivity {

    private CollectionViewModel viewModel;
    private int collectionId;

    private int backgroundColor;
    private int textColor;

    private EditText etCollectionTitle;
    private CollectionModel currentCollection;

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
                });

        etCollectionTitle.setOnClickListener(v -> showEditCollectionDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Guardar", (d, which) -> {});
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", (d, which) -> d.dismiss());

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String newName = etName.getText().toString().trim();

                        if (newName.length() < 3) {
                            showMessage("El nombre debe tener al menos 3 caracteres");
                            return;
                        }

                        int lineCount = newName.split("\n").length;
                        if (lineCount > 3) {
                            showMessage("Máximo 3 saltos de línea permitidos");
                            return;
                        }

                        String newTheme =
                                getCollectionThemeFromPosition(
                                        spinner.getSelectedItemPosition()
                                );

                        currentCollection.setTitle(newName);
                        currentCollection.setColor(newTheme);

                        viewModel.update(currentCollection);

                        dialog.dismiss();
                    });
        });

        dialog.show();
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
