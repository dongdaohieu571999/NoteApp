package com.example.broadcastreciever;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {
    private SlidingUpPanelLayout sliding_layout;
    private ListView listView;
    private CalendarView calendarView;
    private List<Note> listNote;
    private LinearLayout drag;
    FloatingActionButton floatingActionButton;
        private int i = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            sliding_layout = findViewById(R.id.sliding_layout);
            calendarView = findViewById(R.id.calendar);
            listView = findViewById(R.id.list_view_child);
            drag = findViewById(R.id.dragView);
            floatingActionButton = findViewById(R.id.fab);
            sliding_layout.setScrollableView(listView);
            sliding_layout.setFadeOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            });
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            });

            Note note = new Note("Hôm nay ăn gì?","22/06","khong biet", Color.RED, false);
            listNote = new ArrayList<Note>();
            for(int i=0; i< 100; i++){
                listNote.add(note);
            }

            Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(listNote,R.layout.custom_item_listview,MainActivity.this);
            listView.setAdapter(adapter);




        }

        public void loadAllNote(View view){
            Intent intent = new Intent(getApplicationContext(),SearchAll.class);
            startActivity(intent);
        }

        public void writeNote(View view){
            Intent intent = new Intent(getApplicationContext(),WriteNote.class);
            startActivity(intent);
        }

}