package com.example.kussiya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.Kussiya.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class  breakfast_screen extends AppCompatActivity {

    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    DataClass androidData;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breakfast_screen);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(breakfast_screen.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataList = new ArrayList<>();

        androidData = new DataClass("Kiribath", R.string.camera, "Java", R.drawable.kiribath);
        dataList.add(androidData);

        androidData = new DataClass("B1", R.string.recyclerview, "Kotlin", R.drawable.breakfast2);
        dataList.add(androidData);

        androidData = new DataClass("B2", R.string.date, "Java", R.drawable.breakfast3);
        dataList.add(androidData);

        androidData = new DataClass("Kiribath", R.string.edit, "Kotlin", R.drawable.kiribath);
        dataList.add(androidData);

        androidData = new DataClass("B1", R.string.rating, "Java", R.drawable.breakfast3);
        dataList.add(androidData);

        adapter = new MyAdapter(breakfast_screen.this, dataList);
        recyclerView.setAdapter(adapter);
    }

    private void searchList(String text){
        List<DataClass> dataSearchList = new ArrayList<>();
        for (DataClass data : dataList){
            if (data.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()){
            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }
    }
}