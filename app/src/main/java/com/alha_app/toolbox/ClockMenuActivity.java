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
    private String mFileName;
    private String[] myTimeZones = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clockzonelist);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

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
