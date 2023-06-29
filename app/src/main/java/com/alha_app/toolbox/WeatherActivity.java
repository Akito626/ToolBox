package com.alha_app.toolbox;

import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        final Handler handler = new Handler();

        // 別スレッドで天気を取得
        new Thread(() -> {
            String json = getCurrentWeather();
            handler.post(() -> {
                TextView textView = findViewById(R.id.weather_text);
                textView.setText(json);
                LinearLayout linearLayout = findViewById(R.id.weather_Load);
                linearLayout.setVisibility(View.GONE);
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuButton){
        boolean result = true;
        int buttonId = menuButton.getItemId();
        switch(buttonId){
            //戻るボタンが押されたとき
            case android.R.id.home:
                //画面を終了させる
                finish();
                break;
            //それ以外の時
            default:
                result = super.onOptionsItemSelected(menuButton);
                break;
        }
        return result;
    }

    public String getCurrentWeather(){
        String key = BuildConfig.KEY;
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=tokyo&appid=" + key + "&lang=ja&units=metric";
        String json = "";
        StringBuilder sb = new StringBuilder();

        Map<String, Object> map = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String tmp = "";
            while((tmp = br.readLine()) != null){
                sb.append(tmp);
            }
            json = sb.toString();

            map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});

            br.close();
            con.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }
}
