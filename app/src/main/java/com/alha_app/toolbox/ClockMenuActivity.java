package com.alha_app.toolbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class ClockMenuActivity extends AppCompatActivity {
    private int id;
    private final String mFileName = "MyTimeZone.txt";
    private String[] myTimeZones = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clockzonelist);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);

        ArrayList<String> timezone = new ArrayList<String>();
        ArrayList<String> timezonename = new ArrayList<String>();

        for(String str : TimeZone.getAvailableIDs()){
            TimeZone tz = TimeZone.getTimeZone(str);
            timezone.add(str);
            timezonename.add(tz.getDisplayName());
        }


        ArrayList<Map<String, String>> listData = new ArrayList<>();
        for (int i = 0; i < timezone.size(); i++) {
            Map<String, String> item = new HashMap<>();
            item.put("timezone", timezone.get(i));
            item.put("name", timezonename.get(i));
            listData.add(item);
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.timezonelist);
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.zonelist_item,
                new String[] {"timezone", "name"},
                new int[] {R.id.timezone, R.id.name}
        );
        list.setAdapter(adapter);

        // クリックイベント
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String message = adapter.getItem(i).toString();
                message = message.replaceAll("[^a-z A-Z /]", "");
                message = message.replaceAll("timezone", "");
                int index = message.indexOf("name");
                message = message.substring(0, index);

                myTimeZones[id] = message;
                changeZoneId();
                finish();
            }
        });

        // テキストフィルターを有効にする
        list.setTextFilterEnabled(true);
        SearchView searchView = findViewById(R.id.search);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    // 入力テキストに変更があったとき
                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (s.equals("")) {
                            list.clearTextFilter();
                        } else {
                            list.setFilterText(s);
                        }
                        return false;
                    }

                    // 検索ボタンを押したとき
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }
                }
        );

        loadZoneId();
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

    public void changeZoneId(){
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

    public void loadZoneId(){
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
