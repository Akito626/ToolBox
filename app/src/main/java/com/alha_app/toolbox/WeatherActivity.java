package com.alha_app.toolbox;

import android.os.Bundle;
import android.util.JsonReader;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WeatherActivity extends AppCompatActivity {
    ExecutorService es = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        // 別スレッドで天気を取得
        es.execute(() -> getCurrentWeather());
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

    public void getCurrentWeather(){
        String key = BuildConfig.KEY;
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=tokyo&appid=" + key + "&lang=ja&units=metric";
        String result = "";
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String tmp = "";
            while((tmp = br.readLine()) != null){
                sb.append(tmp);
            }
            result = sb.toString();

            br.close();
            con.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(result);
    }
}
