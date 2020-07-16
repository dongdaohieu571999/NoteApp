package com.example.broadcastreciever;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private SlidingUpPanelLayout sliding_layout;
    private ListView listView;
    private CalendarView calendarView;
    private List<Note> listNote;

    private List<Note> dailyNotes;

    private LinearLayout drag;
    private TextView forecast;
    private GifImageView iconWeather;
    private LinearLayout scroll;
    FloatingActionButton floatingActionButton;
    private int i = 0;
    LocationManager locationManager;
    LocationListener locationListener;
    double lon, lat;

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
        scroll = findViewById(R.id.scroll);
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
                setDailyNotes(dayOfMonth,month,year);
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        getDailyNotes();


        loadingWeather();
        iconWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingWeather();
                forecast.setText("");
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SlidingUpPanelLayout.LayoutParams params = (SlidingUpPanelLayout.LayoutParams) scroll.getLayoutParams();
        params.height = displayMetrics.heightPixels-(35+38);


    }

    public void loadAllNote(View view) {
        Intent intent = new Intent(getApplicationContext(), SearchAll.class);
        startActivity(intent);
    }

    public void writeNote(View view) {
        Intent intent = new Intent(getApplicationContext(), WriteNote.class);
        startActivity(intent);
    }

    public void getCurrenLocation(String lat, String lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + getResources().getString(R.string.apiKey) + "&units=metric";
        new Asy(this, forecast, iconWeather).execute(url);
    }

    public void loadingWeather() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    getLocation();
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("locationSetting", Context.MODE_PRIVATE);
                    if (lat != 0 && lon != 0) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lattitude", lat + "");
                        editor.putString("longtitude", lon + "");
                        editor.commit();
                        getCurrenLocation(lat + "", lon + "");
                    } else {
                        if (!sharedPreferences.getString("lattitude", "0").equals("0")) {
                            getCurrenLocation(sharedPreferences.getString("lattitude", "0"), sharedPreferences.getString("longtitude", "0"));
                            locationManager.removeUpdates(locationListener);
                        } else {
                            forecast.setText("Retry again!");
                        }

                    }

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MainActivity.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                            } catch (ClassCastException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
                forecast.setText("Retry again!");
            }
        });

    }

    public void callPermisstion() {
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lon = location.getLongitude();
                lat = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        } else {
            callPermisstion();
        }
    }

    public void getDailyNotes() {
        listNote = new ArrayList<Note>();
        dailyNotes = new ArrayList<Note>();
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
                    DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String s = simpleDateFormat.format(calendarView.getDate());
                    dailyNotes.clear();
                    for (Note note : listNote){
                        if (note.getDate().equals(s)){
                            dailyNotes.add(note);
                        }
                    }
                    Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(dailyNotes,
                            R.layout.custom_item_listview, MainActivity.this);
                    listView.setAdapter(adapter);
                }
            }
        });
    }

    public void setDailyNotes(int day,int month, int year){
        dailyNotes = new ArrayList<Note>();
        dailyNotes.clear();
        for (Note note : listNote){
            String date = note.getDate();
            String[] temp = date.split("/");
            if ( (Integer.parseInt(temp[0]) == day) && (Integer.parseInt(temp[1]) == month+1) && (Integer.parseInt(temp[2]) == year) ){
                dailyNotes.add(note);
            }
        }
        Custom_ListView_ViewChild_Adapter adapter = new Custom_ListView_ViewChild_Adapter(dailyNotes,
                R.layout.custom_item_listview, MainActivity.this);
        listView.setAdapter(adapter);
    }

}