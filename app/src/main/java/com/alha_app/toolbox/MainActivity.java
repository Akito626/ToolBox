package com.alha_app.toolbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String, Boolean> isFavorite;
    private SimpleAdapter adapter;
    private boolean isPushed;
    private final String mFileName = "favorite.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] tools = getResources().getStringArray(R.array.tools);
        isFavorite = new HashMap<>();
        isPushed = false;

        for(int i = 0; i < tools.length; i++){
            isFavorite.put(tools[i], false);
        }

        prepareList();
    }

    @Override
    public void onPause(){
        super.onPause();
        saveFavorite();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(isPushed) {
            prepareFavoriteList();
        } else {
            prepareList();
        }
        loadFavorite();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                if(isPushed){
                    isPushed = false;
                    prepareList();
                } else {
                    isPushed = true;
                    prepareFavoriteList();
                }
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, view, info);
        getMenuInflater().inflate(R.menu.context_main, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        String appname = adapter.getItem(info.position).toString();
        int index = appname.indexOf("name=");
        appname = appname.substring(index+5, appname.length()-1);
        switch(item.getItemId()) {
            case R.id.context_favorite:
                if(isFavorite.get(appname)){
                    isFavorite.put(appname, false);
                } else {
                    isFavorite.put(appname, true);
                }
                if(isPushed){
                    prepareFavoriteList();
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void prepareList(){
        String [] tools = getResources().getStringArray(R.array.tools);
        int [] images = {
                R.drawable.calculator, R.drawable.counter, R.drawable.stopwatch, R.drawable.timer,
                R.drawable.clock, R.drawable.ic_baseline_qr_code_scanner_24};

        ImageView imageView;
        ArrayList<Map<String, Object>> listData = new ArrayList<>();
        for (int i=0; i < tools.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", tools[i]);
            item.put("image", images[i]);
            listData.add(item);
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.list);
        adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.list_item,
                new String[] {"name", "image"},
                new int[] {R.id.name, R.id.image}
        );
        list.setAdapter(adapter);

        // クリックイベント
        list.setOnItemClickListener((adapterView, view, position, l) -> {
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
        });

        registerForContextMenu(list);
    }

    public void prepareFavoriteList(){
        String [] tools = getResources().getStringArray(R.array.tools);
        int [] images = {
                R.drawable.calculator, R.drawable.counter, R.drawable.stopwatch, R.drawable.timer,
                R.drawable.clock, R.drawable.ic_baseline_qr_code_scanner_24};

        ArrayList<Map<String, Object>> listData = new ArrayList<>();
        for (int i=0; i < tools.length; i++) {
            Map<String, Object> item = new HashMap<>();
            if(isFavorite.get(tools[i])) {
                item.put("name", tools[i]);
                item.put("image", images[i]);
                listData.add(item);
            }
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.list);
        adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.list_item,
                new String[] {"name", "image"},
                new int[] {R.id.name, R.id.image}
        );
        list.setAdapter(adapter);

        // クリックイベント
        list.setOnItemClickListener((adapterView, view, position, l) -> {
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
        });

        registerForContextMenu(list);
    }

    public void saveFavorite(){
        String [] tools = getResources().getStringArray(R.array.tools);
        // 保存
        OutputStream out = null;
        PrintWriter writer = null;
        try{
            out = this.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            for (int i=0; i < tools.length; i++) {
                if(isFavorite.get(tools[i])) {
                    writer.println(tools[i]);
                }
            }
            writer.close();
            out.close();
        }catch(Exception e){
            Toast.makeText(this, "File save error!", Toast.LENGTH_LONG).show();
        }
    }

    public void loadFavorite() {
        FilenameFilter filter = new FilenameFilter(){
            public boolean accept(File file, String str){
                //指定文字列でフィルタする
                if(str.indexOf(mFileName) != -1) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        // アプリの保存フォルダ内のファイル一覧を取得
        String savePath = this.getFilesDir().getPath().toString();
        File[] files = new File(savePath).listFiles(filter);

        if(files.length != 0) {
            String fileName = files[0].getName();
            //　ファイルを読み込み
            try {
                // ファイルオープン
                InputStream in = this.openFileInput(fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String str = reader.readLine();
                while (str != null){
                    isFavorite.put(str, true);
                    str = reader.readLine();
                }

                reader.close();
                in.close();
            } catch (Exception e) {
                Toast.makeText(this, "File read error!", Toast.LENGTH_LONG).show();
            }
        }
    }
}