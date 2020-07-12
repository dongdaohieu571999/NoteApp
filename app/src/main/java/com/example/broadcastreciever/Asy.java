package com.example.broadcastreciever;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class Asy extends AsyncTask<String, Void, List<String>> {

    TextView forecast;
    Context context;
    GifImageView iconWeather;

    public Asy(Context context,TextView forecast, GifImageView iconWeather) {
        this.context = context;
        this.forecast = forecast;
        this.iconWeather = iconWeather;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onPreExecute() {
        iconWeather.setImageResource(R.raw.loading);
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(final String... strings) {
        final OkHttpClient client=new OkHttpClient();
        final List<String> listdata = new ArrayList<>();
        Request request = new Request.Builder()
                .url(strings[0])
                .get()
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Response response= client.newCall(request).execute();
                try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String temperature = jsonObject.getJSONObject("main").getString("temp");
                        String humidity = jsonObject.getJSONObject("main").getString("humidity");
                        String city = jsonObject.getString("name");
                        String country = jsonObject.getJSONObject("sys").getString("country");
                        String icon = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                        listdata.add(temperature);
                        listdata.add(humidity);
                        listdata.add(city);
                        listdata.add(country);
                        listdata.add(icon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listdata;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onPostExecute(List<String> list) {
        if(list.size() == 0 || list == null){
            iconWeather.setImageResource(R.raw.reload);
            forecast.setText("Retry again!");
        } else {
            super.onPostExecute(list);
            forecast.setText(list.get(2) + " - " + list.get(3) + ": " + list.get(0) + "°C\nĐộ ẩm: " + list.get(1)+"%");
            List<String> sunny = Arrays.asList(context.getResources().getStringArray(R.array.sunny));
            List<String> cloudy = Arrays.asList(context.getResources().getStringArray(R.array.cloudy));
            List<String> rain = Arrays.asList(context.getResources().getStringArray(R.array.rain));
            List<String> storm = Arrays.asList(context.getResources().getStringArray(R.array.storm));
            List<String> snow = Arrays.asList(context.getResources().getStringArray(R.array.snow));
            List<String> windy = Arrays.asList(context.getResources().getStringArray(R.array.windy));

            if (sunny.contains(list.get(4))) {
                iconWeather.setImageResource(R.raw.sunny);
            } else if (cloudy.contains(list.get(4))) {
                iconWeather.setImageResource(R.raw.cloudy);
            } else if (rain.contains(list.get(4))) {
                iconWeather.setImageResource(R.raw.rain);
            } else if (storm.contains(list.get(4))) {
                iconWeather.setImageResource(R.raw.storm);
            } else if (snow.contains(list.get(4))) {
                iconWeather.setImageResource(R.raw.snow);
            } else if (windy.contains(list.get(4))) {
                iconWeather.setImageResource(R.raw.windy);
            }
        }
    }
}
