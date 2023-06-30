package com.alha_app.toolbox;

import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alha_app.toolbox.entities.WeatherData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
    WeatherData weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        final Handler handler = new Handler();
        weatherData = new WeatherData();

        // 別スレッドで天気を取得
        new Thread(() -> {
            JsonNode jsonResult = getCurrentWeather();

            // メインスレッドに処理を依頼
            handler.post(() -> {
                TextView textView = findViewById(R.id.weather_text);
                textView.setText(jsonResult.toString());
                LinearLayout linearLayout = findViewById(R.id.weather_Load);
                linearLayout.setVisibility(View.GONE);
            });
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
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
            // 現在地から天気を取得
            case R.id.action_curtweather:
                break;
            // 天気を検索
            case R.id.action_serchweather:
                break;
            //それ以外の時
            default:
                result = super.onOptionsItemSelected(menuButton);
                break;
        }
        return result;
    }

    public JsonNode getCurrentWeather(){
        String key = BuildConfig.KEY;
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=tokyo&appid=" + key + "&lang=ja&units=metric";
        String json = "";
        StringBuilder sb = new StringBuilder();
        JsonNode jsonResult = null;

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
            jsonResult = mapper.readTree(json);

            weatherData.setWeather(jsonResult.get("weather").get(0).get("main").toString());
            weatherData.setTemp_min(jsonResult.get("main").get("temp_min").asDouble());
            weatherData.setTemp_max(jsonResult.get("main").get("temp_max").asDouble());
            weatherData.setHumidity(jsonResult.get("main").get("humidity").asInt());

            br.close();
            con.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }

        return jsonResult;
    }
}
