package com.alha_app.toolbox;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.alha_app.toolbox.entities.LatLng;
import com.alha_app.toolbox.entities.WeatherData;
import com.alha_app.toolbox.entities.WeatherFragmentStateAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class WeatherActivity extends AppCompatActivity {
    private WeatherData weatherData;
    private Handler handler;
    private List<Address> address;

    private static final int RC_PERMISSION = 10;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ViewPager2 pager = (ViewPager2)findViewById(R.id.weather_viewPager);
        pager.setUserInputEnabled(false);
        WeatherFragmentStateAdapter adapter = new WeatherFragmentStateAdapter(this);
        pager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.weathertab_layout);
        new TabLayoutMediator(tabs, pager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("天気");
                    break;
                case 1:
                    tab.setText("雨雲レーダー");
            }
        }).attach();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuButton) {
        boolean result = true;
        int buttonId = menuButton.getItemId();
        switch (buttonId) {
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
                System.out.println("push");
                break;
            //それ以外の時
            default:
                result = super.onOptionsItemSelected(menuButton);
                break;
        }
        return result;
    }

    // パーミッションダイアログの結果を受け取る
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);

        if (grantResults.length <= 0) {
            return;
        }
        switch (requestCode) {
            case RC_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 許可が取れた場合の処理
                } else {
                    Toast toast = Toast.makeText(this, "許可が必要です", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
        }
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, RC_PERMISSION);
            }
        }
    }

//    public void getCurrentLocationWeather() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
//            if(location == null){
//                Toast toast = Toast.makeText(this, "位置情報が取得できませんでした", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }
//
//            System.out.println("latitude" + location.getLatitude());
//            System.out.println("longitude" + location.getLongitude());
//
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            new Thread(() -> {
//                getCurrentWeather(latLng);
//            });
//        });
//    }

//    public void serchLocationWeather(){
//        String key = BuildConfig.KEY;
//        String pName = "kobe";
//        String urlString = "http://api.openweathermap.org/geo/1.0/direct?q=" + pName + "&limit=1&appid=" + key;
//        String json = "";
//        StringBuilder sb = new StringBuilder();
//        JsonNode jsonResult = null;
//        ObjectMapper mapper = new ObjectMapper();
//        LatLng latLng = null;
//
//        System.out.println("取得開始");
//
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.connect();
//            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String tmp = "";
//            while ((tmp = br.readLine()) != null) {
//                sb.append(tmp);
//            }
//            json = sb.toString();
//            jsonResult = mapper.readTree(json);
//            latLng = new LatLng(jsonResult.get(0).get("lat").asDouble(), jsonResult.get(0).get("lon").asDouble());
//
//            br.close();
//            con.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        JsonNode jsonNode = getCurrentWeather(latLng);
//    }
}
