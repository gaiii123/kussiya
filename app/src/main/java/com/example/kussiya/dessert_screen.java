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

public class  dessert_screen extends AppCompatActivity {

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(dessert_screen.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataList = new ArrayList<>();

        androidData = new DataClass("Watalappan", R.string.camera, "", R.drawable.watalappan);
        dataList.add(androidData);

        androidData = new DataClass("Caramel bread pudding", R.string.recyclerview, "", R.drawable.caramel);
        dataList.add(androidData);

        androidData = new DataClass("Jelly", R.string.date, "", R.drawable.jelly);
        dataList.add(androidData);

        androidData = new DataClass("Ice cream", R.string.edit, "", R.drawable.ice);
        dataList.add(androidData);

        androidData = new DataClass("Fruit salad", R.string.rating, "Java", R.drawable.fruit);
        dataList.add(androidData);

        adapter = new MyAdapter(dessert_screen.this, dataList);
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
