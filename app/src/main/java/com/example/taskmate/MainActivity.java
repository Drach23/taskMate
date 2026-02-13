package com.example.taskmate;

import android.os.Bundle;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.taskmate.CollectionViewModel;
import com.example.taskmate.data.CollectionModel;

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

        // Insets (para respetar barra de estado)
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

        // Observar cambios en la base de datos
        viewModel.getCollections().observe(this, collections -> {
            adapter.setData(collections);
        });

        // Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            viewModel.insert(new CollectionModel("Nueva colección", 0));
        });
    }
}
