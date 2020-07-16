package com.example.broadcastreciever;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchAll extends AppCompatActivity {
    ListView listView;
    SearchView searchView;
    List<Note> listNote;
    List<Note> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_all);
        listView = findViewById(R.id.list_view_parent);
        searchView = findViewById(R.id.search_bar);
        getAllNotes();

        searchList = new ArrayList<Note>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s != null){
                    searchList.clear();
                    for (Note note : listNote) {
                        if (note.getTitle().contains(s.toLowerCase()) || note.getTitle().contains(s.toUpperCase()) ) {
                            searchList.add(note);
                        }
                    }
                    Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(searchList, R.layout.custom_item_listview, SearchAll.this);
                    listView.setAdapter(adapter);
                }
                else {
                    getAllNotes();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s != null) {
                    searchList.clear();
                    for (Note note : listNote) {
                        if (note.getTitle().contains(s.toLowerCase()) || note.getTitle().contains(s.toUpperCase())) {
                            searchList.add(note);
                        }
                    }
                    Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(searchList, R.layout.custom_item_listview, SearchAll.this);
                    listView.setAdapter(adapter);
                }
                else{
                    getAllNotes();
                }
                return true;
            }
        });

    }

    public void backMain(View view){
        finish();
    }

    public void getAllNotes(){
        listNote = new ArrayList<Note>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String title = doc.get("title").toString();
                        String date = doc.get("date").toString();
                        String content = doc.get("content").toString();
                        int color = Color.GREEN;
                        boolean checked = Boolean.parseBoolean(doc.get("checked").toString());
                        Note note = new Note(title, date, content, color, checked);
                        listNote.add(note);
                    }
                    Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(listNote, R.layout.custom_item_listview, SearchAll.this);
                    listView.setAdapter(adapter);
                }
            }
        });
    }

}
