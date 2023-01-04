package com.alha_app.toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ClockActivity extends AppCompatActivity {
    private TextView clockViews[];
    private TextView countryViews[];
    private TextView dateViews[];
    private LinearLayout clocklayouts[];
    private Timer timer;

    private final String mFileName = "MyTimeZone.txt";
    private String [] myTimeZones = {"Asia/Tokyo", "Asia/Seoul", "Asia/Shanghai", "America/New_York", "Europe/London"};
    //private String [] mtzName = {"東京/日本", "ソウル/大韓民国", "上海/中国", "ニューヨーク/アメリカ", "ロンドン/イギリス"};

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        clockViews = new TextView[5];
        countryViews = new TextView[5];
        dateViews = new TextView[5];
        clocklayouts = new LinearLayout[5];

        clockViews[0] = findViewById(R.id.mainclock);
        countryViews[0] = findViewById(R.id.maincountry);
        dateViews[0] = findViewById(R.id.maindate);
        clocklayouts[0] = findViewById(R.id.mainlayout);

        clockViews[1] = findViewById(R.id.subclock1);
        countryViews[1] = findViewById(R.id.subcountry1);
        dateViews[1] = findViewById(R.id.subdate1);
        clocklayouts[1] = findViewById(R.id.sublayout1);

        clockViews[2] = findViewById(R.id.subclock2);
        countryViews[2] = findViewById(R.id.subcountry2);
        dateViews[2] = findViewById(R.id.subdate2);
        clocklayouts[2] = findViewById(R.id.sublayout2);

        clockViews[3] = findViewById(R.id.subclock3);
        countryViews[3] = findViewById(R.id.subcountry3);
        dateViews[3] = findViewById(R.id.subdate3);
        clocklayouts[3] = findViewById(R.id.sublayout3);

        clockViews[4] = findViewById(R.id.subclock4);
        countryViews[4] = findViewById(R.id.subcountry4);
        dateViews[4] = findViewById(R.id.subdate4);
        clocklayouts[4] = findViewById(R.id.sublayout4);

        for(int i = 0; i < 5; i++){
            clocklayouts[i].setId(i);
        }

        View.OnTouchListener clocktouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LinearLayout layout = (LinearLayout) v;
                id = layout.getId();


                Intent intent = new Intent(getApplication(), ClockMenuActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);

                return false;
            }
        };

        for(int i = 0; i < 5; i++){
            clocklayouts[i].setOnTouchListener(clocktouchListener);
        }

        createTimer();
    }

    @Override
    public void onPause(){
        super.onPause();
        saveTimeZone();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadTimeZone();
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

    public void createTimer(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");
                DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                for(int i = 0; i < 5; i++) {
                    ZonedDateTime time = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
                    countryViews[i].setText(myTimeZones[i]);
                    clockViews[i].setText(time.format(timeformatter));
                    dateViews[i].setText(time.format(dateformatter));
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void saveTimeZone(){
        // 保存
        OutputStream out = null;
        PrintWriter writer = null;
        try{
            out = this.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            // タイトル書き込み
            for(int i = 0; i < 5; i++) {
                writer.println(myTimeZones[i]);
            }
            writer.close();
            out.close();
        }catch(Exception e){
            Toast.makeText(this, "File save error!", Toast.LENGTH_LONG).show();
        }
    }

    public void loadTimeZone() {
        FilenameFilter filter = new FilenameFilter(){
            public boolean accept(File file, String str){
                //指定文字列でフィルタする
                if(str.indexOf("MyTimeZone.txt") != -1) {
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

                String str;
                for (int i = 0; i < 5; i++) {
                    if ((str = reader.readLine()) == null) break;
                    myTimeZones[i] = str;
                }

                reader.close();
                in.close();
            } catch (Exception e) {
                Toast.makeText(this, "File read error!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
