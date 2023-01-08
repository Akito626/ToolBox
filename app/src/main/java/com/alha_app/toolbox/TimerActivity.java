package com.alha_app.toolbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
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
import java.util.Objects;

public class TimerActivity extends AppCompatActivity {
    private final String mFileName = "Timer.txt";
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;

    NumberPicker numberPicker1;
    NumberPicker numberPicker2;
    NumberPicker numberPicker3;
    TextView musicText;
    private MediaPlayer mp;
    private String hour;
    private String minute;
    private String second;
    private String music;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        // デフォルトの設定
        mp = MediaPlayer.create(this, R.raw.defaultmusic);
        mp.setLooping(true);
        music = "defauntmusic";
        path = null;
        hour = "0";
        minute = "0";
        second = "0";

        numberPicker1 = findViewById(R.id.numberPiker1);
        numberPicker2 = findViewById(R.id.numberPiker2);
        numberPicker3 = findViewById(R.id.numberPiker3);
        String hours[] = new String[100];
        String minutes[] = new String[60];
        String seconds[] = new String[60];

        musicText = findViewById(R.id.musictext);
        Button startButton = findViewById(R.id.startbutton);
        Button resetButton = findViewById(R.id.resetbutton);

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
                hour = String.valueOf(numberPicker1.getValue());
                minute = String.valueOf(numberPicker2.getValue());
                second = String.valueOf(numberPicker3.getValue());
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

        View.OnTouchListener timertouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ファイルの読み取り許可
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        mPermissionGranted = false;
                        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION);
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
        };

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String str = button.getText().toString();

                if(str.equals("スタート")){
                    button.setText("停止");
                }else if(str.equals("リセット")){

                }else if(str.equals("停止")){
                    mp.stop();
                    mp.reset();
                    button.setText("スタート");
                }
            }
        };

        musicText.setOnTouchListener(timertouchListener);
        startButton.setOnClickListener(btnListener);
        resetButton.setOnClickListener(btnListener);

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

    public void audioPlay(){

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
        }

        numberPicker1.setValue(Integer.parseInt(hour));
        numberPicker2.setValue(Integer.parseInt(minute));
        numberPicker3.setValue(Integer.parseInt(second));
        musicText.setText(music);
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
