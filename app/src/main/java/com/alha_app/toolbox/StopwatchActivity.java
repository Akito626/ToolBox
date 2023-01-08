package com.alha_app.toolbox;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class StopwatchActivity extends AppCompatActivity {
    private Timer timer;
    private MyTimerTask timerTask;
    private long mm, ss, ms;
    private long prevmm, prevss, prevms;
    private long lapmm, lapss, lapms;
    private int count;
    private TextView timerText;
    private Button button1;
    private Button button2;
    private int textsize;
    private ArrayList<String> lapnum;
    private ArrayList<String> time;
    private ArrayList<String> totaltime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        lapnum = new ArrayList<String>();
        time = new ArrayList<String>();
        totaltime = new ArrayList<String>();

        timerText = findViewById(R.id.timertext);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        mm = ss = ms = count = 0;
        lapmm = lapss = lapms = 0;
        textsize = 0;

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String btext = "";

                btext = ((Button) v).getText().toString();
                if(btext.equals("開始")){
                    timer = new Timer();
                    // TimerTask インスタンスを生成
                    timerTask = new MyTimerTask();
                    timer.schedule(timerTask, 0, 10);
                    button1.setText("ラップ");
                    button2.setText("停止");
                } else if(btext.equals("リセット")){
                    if(timer != null) timer.cancel();
                    mm = ss = ms = count = 0;
                    lapmm = lapss = lapms = 0;
                    timerText.setText("00:00:00");
                    lapnum.clear();
                    time.clear();
                    totaltime.clear();
                    setList();
                } else if(btext.equals("停止")){
                    timer.cancel();
                    button1.setText("リセット");
                    button2.setText("開始");
                } else if(btext.equals("ラップ")){
                    createLapText();
                }
            }
        };

        findViewById(R.id.button1).setOnClickListener(btnListener);
        findViewById(R.id.button2).setOnClickListener(btnListener);
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

    public void createLapText(){
        count++;
        lapnum.add(String.valueOf(count));
        time.add(String.format("%02d:%02d:%02d", lapmm, lapss, lapms));
        totaltime.add(String.format("%02d:%02d:%02d", mm, ss, ms));

        setList();
        lapmm = lapss = lapms = 0;
    }

    public void setList(){
        ArrayList<Map<String, Object>> listData = new ArrayList<>();
        for (int i=lapnum.size()-1; i >= 0; i--) {
            Map<String, Object> item = new HashMap<>();
            item.put("lapnum", lapnum.get(i));
            item.put("time", time.get(i));
            item.put("totaltime", totaltime.get(i));
            listData.add(item);
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.laplist);
        list.setAdapter(new SimpleAdapter(
                this,
                listData,
                R.layout.row_item,
                new String[] {"lapnum", "time", "totaltime"},
                new int[] {R.id.lapnum, R.id.time, R.id.totaltime}
        ));
    }

    private class MyTimerTask extends TimerTask{
        @Override
        public void run(){
            ms++;
            if(ms == 100){
                ms = 0;
                ss++;
            }
            if(ss == 60){
                ss = 0;
                mm++;
            }

            lapms++;
            if(lapms == 99){
                lapms = 0;
                lapss++;
            }
            if(lapss == 60){
                lapss = 0;
                lapmm++;
            }

            // 2桁ずつ
            timerText.setText(String.format("%02d:%02d:%02d", mm, ss, ms));
        }
    }
}
