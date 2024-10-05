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

public class  lunch_screen extends AppCompatActivity {

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(lunch_screen.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataList = new ArrayList<>();

        androidData = new DataClass("Vegetable rice & curry", R.string.camera, "", R.drawable.vegetablecurry);
        dataList.add(androidData);

        androidData = new DataClass("Fish rice & curry", R.string.recyclerview, "", R.drawable.fishcurry);
        dataList.add(androidData);

        androidData = new DataClass("Chicken rice & curry", R.string.date, "", R.drawable.chickencurry);
        dataList.add(androidData);

        androidData = new DataClass("Pork rice & curry", R.string.edit, "", R.drawable.porkcurry);
        dataList.add(androidData);

        androidData = new DataClass("Beef rice & curry", R.string.rating, "", R.drawable.beefcurry);
        dataList.add(androidData);

        adapter = new MyAdapter(lunch_screen.this, dataList);
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
