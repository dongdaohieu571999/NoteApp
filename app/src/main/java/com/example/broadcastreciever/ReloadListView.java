package com.example.broadcastreciever;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class ReloadListView {
    Context context;
    ListView listView;
    GifImageView loading_note;

    public ReloadListView(Context context, ListView listView, GifImageView loading_note) {
        this.context = context;
        this.listView = listView;
        this.loading_note = loading_note;
    }

    public void reload(final int day, final int month, final int year){
        final List listNote = new ArrayList<Note>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        loading_note.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        firestore.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String[] time = doc.get("date").toString().split("/");
                        if(Integer.parseInt(time[0]) == day && Integer.parseInt(time[1]) == (month+1) && Integer.parseInt(time[2]) == year){
                            String id = doc.getId();
                            String title = doc.get("title").toString();
                            String date = doc.get("date").toString();
                            String content = doc.get("content").toString();
                            int color = Color.GREEN;
                            boolean checked = Boolean.parseBoolean(doc.get("checked").toString());
                            listNote.add(new Note(id,title, date, content, color, checked));
                        }
                    }
                    Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(listNote,
                            R.layout.custom_item_listview, context);
                    listView.setAdapter(adapter);
                    loading_note.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void reload(){
        final List listNote = new ArrayList<Note>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        loading_note.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        firestore.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String id = doc.getId();
                        String title = doc.get("title").toString();
                        String date = doc.get("date").toString();
                        String content = doc.get("content").toString();
                        int color = Color.GREEN;
                        boolean checked = Boolean.parseBoolean(doc.get("checked").toString());
                        Note note = new Note(id,title, date, content, color, checked);
                        listNote.add(note);
                    }
                    Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(listNote, R.layout.custom_item_listview, context);
                    listView.setAdapter(adapter);
                    loading_note.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
