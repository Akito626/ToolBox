package com.alha_app.toolbox;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;

import java.util.Objects;

public class TimerActivity extends AppCompatActivity {
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        NumberPicker numberPicker1 = findViewById(R.id.numberPiker1);
        NumberPicker numberPicker2 = findViewById(R.id.numberPiker2);
        NumberPicker numberPicker3 = findViewById(R.id.numberPiker3);
        String hours[] = new String[100];
        String minutes[] = new String[60];
        String seconds[] = new String[60];

        TextView musicText = findViewById(R.id.musictext);
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

        View.OnTouchListener timertouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                    button.setText("スタート");
                }
            }
        };

        musicText.setOnTouchListener(timertouchListener);
        startButton.setOnClickListener(btnListener);
        resetButton.setOnClickListener(btnListener);
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
}
