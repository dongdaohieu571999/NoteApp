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
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
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
    static int day1,month1,year1;
    private LinearLayout drag;
    private TextView forecast, message;
    private GifImageView iconWeather,loading_note;
    private LinearLayout scroll;
    FloatingActionButton floatingActionButton;
    LocationManager locationManager;
    LocationListener locationListener;
    double lon, lat;
    static ReloadListView reloadListView;
    ImageView add_by_day,setting;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sliding_layout = findViewById(R.id.sliding_layout);
        message = findViewById(R.id.message);
        calendarView = findViewById(R.id.calendar);
        listView = findViewById(R.id.list_view_child);
        loading_note = findViewById(R.id.loading_note);
        drag = findViewById(R.id.dragView);
        forecast = findViewById(R.id.forecast);
        iconWeather = findViewById(R.id.iconWeather);
        floatingActionButton = findViewById(R.id.fab);
        scroll = findViewById(R.id.scroll);
        reloadListView = new ReloadListView(this,listView,loading_note);
        sliding_layout.setScrollableView(listView);
        add_by_day = findViewById(R.id.add_by_day);
        setting = findViewById(R.id.setting);
        sliding_layout.getChildAt(1).setOnClickListener(null);
        sliding_layout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VOICE_COMMAND)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                day1 = dayOfMonth;
                month1=month;
                year1=year;
                setDailyNotes();
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        loadingWeather();
        iconWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingWeather();
                forecast.setText("");
            }
        });
        add_by_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteNote.class);
                intent.putExtra("saveNoteSign",100);
                intent.putExtra("time",day1+"/"+(month1+1)+"/"+year1);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }
        });
        listNote = new ArrayList<>();

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
        intent.putExtra("saveNoteSign",100);
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
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
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

    @SuppressLint("ResourceType")
    public static void setDailyNotes() {
        reloadListView.reload(day1,month1,year1);
    }


}