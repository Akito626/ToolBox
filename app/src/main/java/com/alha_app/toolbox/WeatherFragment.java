package com.alha_app.toolbox;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alha_app.toolbox.entities.LatLng;
import com.alha_app.toolbox.entities.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFragment extends Fragment {
    private String locationName;
    private WeatherData[] weatherData;

    public static WeatherFragment newInstance(){
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_weather,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weatherData = new WeatherData[40];
        final Handler handler = new Handler();

        new Thread(() -> {
            getWeathers(new LatLng(35.6894, 139.6917));

            handler.post(() -> {
                setWeathers(view);
            });
        }).start();
    }

    public JsonNode getWeathers(LatLng latLng) {
        String key = BuildConfig.KEY;
        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latLng.getLatitude() + "&lon=" + latLng.getLongitude() + "&appid=" + key + "&lang=ja&units=metric";
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
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }
            json = sb.toString();
            jsonResult = mapper.readTree(json);

            // 名前を保存
            locationName = jsonResult.get("city").get("name").toString().replace("\"", "");

            for(int i = 0; i < jsonResult.get("list").size(); i++){
                weatherData[i] = new WeatherData();
                weatherData[i].setDt_txt(jsonResult.get("list").get(i).get("dt_txt").toString());
                weatherData[i].setWeather(jsonResult.get("list").get(i).get("weather").get(0).get("main").toString().replace("\"", ""));
                weatherData[i].setTemp_min(jsonResult.get("list").get(i).get("main").get("temp_min").asDouble());
                weatherData[i].setTemp_max(jsonResult.get("list").get(i).get("main").get("temp_max").asDouble());
                weatherData[i].setPop(jsonResult.get("list").get(i).get("pop").asDouble());
                weatherData[i].setHumidity(jsonResult.get("list").get(i).get("main").get("humidity").asInt());
            }

            br.close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult;
    }

    public void setWeathers(View view){
        // scrollviewを取得
        LinearLayout weatherList = view.findViewById(R.id.scroll_weathers);

        // 住所をセット
        TextView locationText = view.findViewById(R.id.location_name);
        locationText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        locationText.setGravity(Gravity.CENTER);
        locationText.setText(locationName);
        ViewGroup.MarginLayoutParams locationParams = (ViewGroup.MarginLayoutParams) locationText.getLayoutParams();
        float margin = 10 * getContext().getResources().getDisplayMetrics().scaledDensity;  // sp -> px
        margin = margin / getContext().getResources().getDisplayMetrics().density;  // px -> dp
        int mi = (int) margin;
        locationParams.setMargins(mi, mi, mi, mi);
        locationText.setLayoutParams(locationParams);

        // サイズを取得し、LayoutParamsを作成
        TextView textView = view.findViewById(R.id.day_text);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
        ViewGroup.MarginLayoutParams layoutParams1 = new ViewGroup.MarginLayoutParams(
                textView.getWidth(),
                textView.getHeight()
        );
        float margin1 = params.topMargin * getContext().getResources().getDisplayMetrics().scaledDensity;  // sp -> px
        margin1 = margin1 / getContext().getResources().getDisplayMetrics().density;  // px -> dp
        int mi1 = (int) margin1;
        layoutParams1.setMargins(mi1, mi1, mi1, mi1);

        for(int i = 0; i < weatherData.length; i++){
            // viewを作成
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            TextView dayText = new TextView(view.getContext());
            TextView timeText = new TextView(view.getContext());
            ImageView weatherImage = new ImageView(view.getContext());
            TextView maxTmpText = new TextView(view.getContext());
            TextView minTmpText = new TextView(view.getContext());
            TextView rainyText = new TextView(view.getContext());
            TextView humidityText = new TextView(view.getContext());

            // レイアウトの大きさを変更
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            dayText.setLayoutParams(layoutParams1);
            timeText.setLayoutParams(layoutParams1);
            weatherImage.setLayoutParams(layoutParams1);
            maxTmpText.setLayoutParams(layoutParams1);
            minTmpText.setLayoutParams(layoutParams1);
            rainyText.setLayoutParams(layoutParams1);
            humidityText.setLayoutParams(layoutParams1);

            // GravityをCenterに
            dayText.setGravity(Gravity.CENTER);
            timeText.setGravity(Gravity.CENTER);
            maxTmpText.setGravity(Gravity.CENTER);
            minTmpText.setGravity(Gravity.CENTER);
            rainyText.setGravity(Gravity.CENTER);
            humidityText.setGravity(Gravity.CENTER);

            // レイアウトに枠線を追加
            linearLayout.setBackgroundResource(R.drawable.border);

            // viewにデータをセット
            dayText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            if(i-1 >= 0 && !weatherData[i-1].getDay().equals(weatherData[i].getDay())){
                dayText.setText(weatherData[i].getDay());
            }
            timeText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            timeText.setText(weatherData[i].getTime());

            switch (weatherData[i].getWeather()){
                case "Clear":
                    weatherImage.setImageResource(R.drawable.sun);
                    break;
                case "Clouds":
                    weatherImage.setImageResource(R.drawable.cloudy);
                    break;
                case "Rain":
                    weatherImage.setImageResource(R.drawable.rain);
                    break;
                case "Snow":
                    weatherImage.setImageResource(R.drawable.snow);
                    break;
                default:
                    break;
            }
            maxTmpText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            maxTmpText.setTextColor(Color.RED);
            maxTmpText.setText(String.valueOf(weatherData[i].getTemp_max()));

            minTmpText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            minTmpText.setTextColor(Color.parseColor("#9DCCE0"));
            minTmpText.setText(String.valueOf(weatherData[i].getTemp_min()));

            rainyText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            rainyText.setText(weatherData[i].getPop() * 100 + "%");

            humidityText.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            humidityText.setText(weatherData[i].getHumidity() + "%");

            // listに追加
            linearLayout.addView(dayText);
            linearLayout.addView(timeText);
            linearLayout.addView(weatherImage);
            linearLayout.addView(maxTmpText);
            linearLayout.addView(minTmpText);
            linearLayout.addView(rainyText);
            linearLayout.addView(humidityText);

            weatherList.addView(linearLayout);
        }
    }
}
