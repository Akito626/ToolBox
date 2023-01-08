package com.alha_app.toolbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {
    private final String mFileName = "Timer.txt";
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;

    NumberPicker numberPicker1;
    NumberPicker numberPicker2;
    NumberPicker numberPicker3;
    private Timer timer;
    TextView musicText;
    private MediaPlayer mp;
    // 保存用
    private String hour;
    private String minute;
    private String second;
    private String music;
    private String path;
    // 現在値
    private int nphour;
    private int npminute;
    private int npsecond;

    private boolean isstop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        // デフォルトの設定
        music = "defauntmusic";
        path = null;
        hour = minute = second = "0";
        nphour = npminute = npsecond = 0;

        isstop = true;
        musicText = findViewById(R.id.musictext);

        Button startButton = findViewById(R.id.startbutton);
        Button resetButton = findViewById(R.id.resetbutton);

        View.OnTouchListener timertouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isstop) {
                    // ファイルの読み取り許可
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            mPermissionGranted = false;
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION);
                        } else {
                            mPermissionGranted = true;
                        }
                    } else {
                        mPermissionGranted = true;
                    }
                    if (mPermissionGranted) {
                        Intent intent = new Intent(getApplication(), MusicListActivity.class);
                        startActivity(intent);
                    }
                    return false;
                }
                return false;
            }
        };

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String str = button.getText().toString();

                if(str.equals("スタート")){
                    if(nphour == 0 && npminute == 0 && npsecond == 0) {
                        Toast.makeText(TimerActivity.this, "値をセットしてください", Toast.LENGTH_SHORT).show();
                    } else {
                        // レイアウトの切り替え
                        LinearLayout layout = findViewById(R.id.stoplayout);
                        layout.removeAllViews();
                        getLayoutInflater().inflate(R.layout.activity_timerrun, layout);
                        TextView timeText = findViewById(R.id.timetext);
                        TextView stoptimeText = findViewById(R.id.stoptimetext);
                        isstop = false;
                        // 時間の取得
                        LocalTime localTime = LocalTime.now(ZoneId.of("Japan"));
                        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        localTime = localTime.plusHours(nphour);
                        localTime = localTime.plusMinutes(npminute);
                        localTime = localTime.plusSeconds(npsecond);
                        // タイマーが止まる時間を表示
                        stoptimeText.setText(localTime.format(sdf));
                        // タイマーの生成
                        timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                npsecond--;
                                if (nphour == 0 && npminute == 0 && npsecond == 0) {
                                    timeText.setText(String.format("%02d:%02d:%02d", nphour, npminute, npsecond));
                                    timer.cancel();
                                    mp.start();
                                    return;
                                }
                                if (npsecond == -1) {
                                    npsecond = 59;
                                    npminute--;
                                }
                                if (npminute == -1) {
                                    npminute = 59;
                                    nphour--;
                                }
                                timeText.setText(String.format("%02d:%02d:%02d", nphour, npminute, npsecond));
                            }
                        };
                        timer.schedule(timerTask, 0, 1000);
                        button.setText("停止");
                    }
                }else if(str.equals("リセット")){
                    if(isstop) {
                        numberPicker1.setValue(Integer.parseInt(hour));
                        numberPicker2.setValue(Integer.parseInt(minute));
                        numberPicker3.setValue(Integer.parseInt(second));
                    }
                }else if(str.equals("停止")){
                    // レイアウトの切り替え
                    LinearLayout layout = findViewById(R.id.runlayout);
                    layout.removeAllViews();
                    getLayoutInflater().inflate(R.layout.activity_timerstop, layout);
                    prepareLayout();
                    isstop = true;
                    timer.cancel();
                    if(mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                    }
                    button.setText("スタート");
                }
            }
        };

        musicText.setOnTouchListener(timertouchListener);
        startButton.setOnClickListener(btnListener);
        resetButton.setOnClickListener(btnListener);

        prepareLayout();
        loadFile();
    }

    @Override
    public void onPause(){
        super.onPause();
        saveFile();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadFile();

        if(path != null){
            try {
                mp = new MediaPlayer();
                mp.setDataSource(path);
                mp.prepare();
            }catch (Exception e){
                mp = MediaPlayer.create(this, R.raw.defaultmusic);
            }
        } else{
            mp = MediaPlayer.create(this, R.raw.defaultmusic);
        }

        mp.setLooping(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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

    public void prepareLayout(){
        numberPicker1 = findViewById(R.id.numberPiker1);
        numberPicker2 = findViewById(R.id.numberPiker2);
        numberPicker3 = findViewById(R.id.numberPiker3);

        String hours[] = new String[100];
        String minutes[] = new String[60];
        String seconds[] = new String[60];

        // NumberPicker用の配列を作成
        for(int i = 0; i < hours.length; i++){
            if(i < minutes.length){
                if(i < 10){
                    minutes[i] = "0" + String.valueOf(i);
                    seconds[i] = "0" + String.valueOf(i);
                }else{
                    minutes[i] = String.valueOf(i);
                    seconds[i] = String.valueOf(i);
                }
            }

            if(i < 10){
                hours[i] = "0" + String.valueOf(i);
            }else{
                hours[i] = String.valueOf(i);
            }
        }

        // NumberPickerが変更されると値を取得
        NumberPicker.OnValueChangeListener pickerListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                nphour = numberPicker1.getValue();
                npminute = numberPicker2.getValue();
                npsecond = numberPicker3.getValue();
                hour = String.valueOf(nphour);
                minute = String.valueOf(npminute);
                second = String.valueOf(npsecond);
            }
        };

        // NumberPickerの設定
        numberPicker1.setMaxValue(99);
        numberPicker1.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setMinValue(0);
        numberPicker1.setDisplayedValues(hours);
        numberPicker2.setDisplayedValues(minutes);
        numberPicker3.setDisplayedValues(seconds);
        numberPicker1.setOnValueChangedListener(pickerListener);
        numberPicker2.setOnValueChangedListener(pickerListener);
        numberPicker3.setOnValueChangedListener(pickerListener);

        numberPicker1.setValue(nphour);
        numberPicker2.setValue(npminute);
        numberPicker3.setValue(npsecond);
    }

    public void loadFile(){
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

                hour = reader.readLine();
                minute = reader.readLine();
                second = reader.readLine();
                music = reader.readLine();
                path = reader.readLine();

                reader.close();
                in.close();
            } catch (Exception e) {
                Toast.makeText(this, "File read error!", Toast.LENGTH_LONG).show();
            }

            if (hour.matches("[\\d]")) {
                nphour = Integer.parseInt(hour);
                npminute = Integer.parseInt(minute);
                npsecond = Integer.parseInt(second);
                numberPicker1.setValue(nphour);
                numberPicker2.setValue(npminute);
                numberPicker3.setValue(npsecond);
                musicText.setText(music);
            } else {
                files[0].delete();
            }
        }
    }

    public void saveFile(){
        // 保存
        OutputStream out = null;
        PrintWriter writer = null;
        try{
            out = this.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            writer.println(hour);
            writer.println(minute);
            writer.println(second);
            writer.println(music);
            writer.println(path);
            writer.close();
            out.close();
        }catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
