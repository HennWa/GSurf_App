package com.example.gsurfexample.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gsurfexample.R;
import com.example.gsurfexample.utils.factory.TestViewModelFactory;
import com.example.gsurfexample.source.local.historic.SurfSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_SURFSESSION_REQUEST = 1;
    public static final int EDIT_SURFSESSION_REQUEST = 2;
    public static final int TEST_REQUEST = 3;
    public static final int START_SESSION_REQUEST = 3;
    private SurfSessionViewModel surfSessionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddSession = findViewById(R.id.button_add_session);
        buttonAddSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditSurfSessionActivity.class);
                startActivityForResult(intent, ADD_SURFSESSION_REQUEST);
            }
        });

        FloatingActionButton buttonTest = findViewById(R.id.button_test);
        buttonTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivityForResult(intent, TEST_REQUEST);
            }
        });

        ImageView buttonStartSession = findViewById(R.id.button_start_session);
        buttonStartSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                startActivityForResult(intent, START_SESSION_REQUEST);
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        SurfSessionAdapter adapter = new SurfSessionAdapter(this);
        recyclerView.setAdapter(adapter);

        // Instantiate and connect surfSessionViewModel to Live Data
        TestViewModelFactory viewModelFactory;
        viewModelFactory = new TestViewModelFactory(this.getApplication());
        surfSessionViewModel = new ViewModelProvider(this, viewModelFactory).get(SurfSessionViewModel.class);
        surfSessionViewModel.getAllSurfSessions().observe(this, new Observer<List<SurfSession>>() {
            @Override
            public void onChanged(@Nullable List<SurfSession> surfSessions) {
                adapter.submitList(surfSessions);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                surfSessionViewModel.delete(adapter.getSurfSessionAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Session deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new SurfSessionAdapter.onItemClickListener() {
            @Override
            public void onItemClick(SurfSession surfSession) {
                Intent intent = new Intent(MainActivity.this, AddEditSurfSessionActivity.class);
                intent.putExtra(AddEditSurfSessionActivity.EXTRA_ID, surfSession.getId());
                intent.putExtra(AddEditSurfSessionActivity.EXTRA_TITLE, surfSession.getTitle());
                intent.putExtra(AddEditSurfSessionActivity.EXTRA_LOCATION, surfSession.getLocation());
                intent.putExtra(AddEditSurfSessionActivity.EXTRA_DATE, surfSession.getDate());
                startActivityForResult(intent, EDIT_SURFSESSION_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_SURFSESSION_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_LOCATION);
            String date = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_DATE);

            SurfSession surfSession = new SurfSession("0", title, description, date);
            surfSessionViewModel.insert(surfSession);

            Toast.makeText(this,"Session saved",Toast.LENGTH_SHORT).show();

        }else if(requestCode == EDIT_SURFSESSION_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditSurfSessionActivity.EXTRA_ID, -1);

            if(id == -1){
                Toast.makeText(this,"Session could not be updated",Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_LOCATION);
            String date = data.getStringExtra(AddEditSurfSessionActivity.EXTRA_DATE);

            SurfSession surfSession = new SurfSession("0", title, description, date);
            surfSession.setId(id);
            surfSessionViewModel.update(surfSession);

            Toast.makeText(this,"Session updated",Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this,"Nothing happens",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delte_all_surfsessions:
                surfSessionViewModel.deleteAllSurfSessions();
                Toast.makeText(this,"All surf sessions deleted",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
























