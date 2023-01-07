package com.alha_app.toolbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_WRITE_EX_STR = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] tools = getResources().getStringArray(R.array.tools);
        int [] images = {
                R.drawable.calculator, R.drawable.counter, R.drawable.stopwatch, R.drawable.timer,
                R.drawable.clock, R.drawable.ic_baseline_qr_code_scanner_24};

        ArrayList<Map<String, Object>> listData = new ArrayList<>();
        for (int i=0; i < tools.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", tools[i]);
            item.put("image", images[i]);
            listData.add(item);
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.list_item,
                new String[] {"name", "image"},
                new int[] {R.id.name, R.id.image}
        );
        list.setAdapter(adapter);

        // クリックイベント
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String appname = adapter.getItem(position).toString();
                int index = appname.indexOf("name=");
                appname = appname.substring(index+5, appname.length()-1);
                Intent intent;
                switch (appname){
                    case "電卓":
                        intent = new Intent(getApplication(), CalcActivity.class);
                        startActivity(intent);
                        break;
                    case "カウンター":
                        intent = new Intent(getApplication(), CounterActivity.class);
                        startActivity(intent);
                        break;
                    case "ストップウォッチ":
                        intent = new Intent(getApplication(), StopwatchActivity.class);
                        startActivity(intent);
                        break;
                    case "タイマー":
                        intent = new Intent(getApplication(), TimerActivity.class);
                        startActivity(intent);
                        break;
                    case "時計":
                        intent = new Intent(getApplication(), ClockActivity.class);
                        startActivity(intent);
                        break;
                    case "QRコードリーダー":
                        intent = new Intent(getApplication(), QRScannerActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}