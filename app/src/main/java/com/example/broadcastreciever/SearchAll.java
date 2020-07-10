package com.example.broadcastreciever;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchAll extends AppCompatActivity {
    ListView listView;
    SearchView searchView;
    List<Note> listNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_all);
        listView = findViewById(R.id.list_view_parent);
        searchView = findViewById(R.id.search_bar);

        Note note = new Note("Hôm nay ăn gì?","22/06","khong biet", Color.RED, false);
        listNote = new ArrayList<Note>();
        for(int i=0; i< 100; i++){
            listNote.add(note);
        }

        Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(listNote,R.layout.custom_item_listview,SearchAll.this);
        listView.setAdapter(adapter);
    }

    public void backMain(View view){
        finish();
    }
}
