package com.example.broadcastreciever;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private SlidingUpPanelLayout sliding_layout;
    private ListView listView;
    private CalendarView calendarView;
    private List<Note> listNote;
    private LinearLayout drag;
    private TextView forecast;
    private GifImageView iconWeather;
    FloatingActionButton floatingActionButton;
    private int i = 0;
    List<String> locationList;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sliding_layout = findViewById(R.id.sliding_layout);
        calendarView = findViewById(R.id.calendar);
        listView = findViewById(R.id.list_view_child);
        drag = findViewById(R.id.dragView);
        forecast = findViewById(R.id.forecast);
        iconWeather = findViewById(R.id.iconWeather);
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

        Note note = new Note("Hôm nay ăn gì?", "22/06", "khong biet", Color.RED, false);
        listNote = new ArrayList<Note>();
        for (int i = 0; i < 100; i++) {
            listNote.add(note);
        }

        Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(listNote, R.layout.custom_item_listview, MainActivity.this);
        listView.setAdapter(adapter);

            if (ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        getCurrenLocation(latitude+","+longitude);
        final String position = latitude+","+longitude;
        iconWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+getResources().getString(R.string.apiKey)+"&q="+position;
                new Asy(MainActivity.this,forecast,iconWeather).execute(url);
            }
        });
        }

        public void loadAllNote(View view){
            Intent intent = new Intent(getApplicationContext(),SearchAll.class);
            startActivity(intent);
        }

        public void writeNote(View view){
            Intent intent = new Intent(getApplicationContext(),WriteNote.class);
            startActivity(intent);
        }

        public void getCurrenLocation(String position){
            String url = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+getResources().getString(R.string.apiKey)+"&q="+position;
            new Asy(this,forecast,iconWeather).execute(url);
        }

}