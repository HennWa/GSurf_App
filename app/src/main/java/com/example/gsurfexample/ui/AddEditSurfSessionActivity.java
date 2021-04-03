package com.example.gsurfexample.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.gsurfexample.R;

import java.util.Calendar;

public class AddEditSurfSessionActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.example.architectureexample.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.architectureexample.EXTRA_TITLE";
    public static final String EXTRA_LOCATION =
            "com.example.architectureexample.EXTRA_DESCRIPTION";
    public static final String EXTRA_DATE =
            "com.example.architectureexample.EXTRA_PRIORITY";

    private EditText editTextTitle;
    private EditText editTextLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_surfsession);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextLocation = findViewById(R.id.edit_text_location);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)){
            setTitle("Edit Surf Session");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextLocation.setText(intent.getStringExtra(EXTRA_LOCATION));
        } else{
            setTitle("Add Surf Session");
        }
    }

    private void saveSurfSession(){
        String title = editTextTitle.getText().toString();
        String location = editTextLocation.getText().toString();
        String date = Calendar.getInstance().getTime().toString();

        if(title.trim().isEmpty() || location.trim().isEmpty()){
            Toast.makeText(this, "Please insert a title and a description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_LOCATION, location);
        data.putExtra(EXTRA_DATE, date);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id != -1){
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_surfsession_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.save_surfsession:
                saveSurfSession();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}