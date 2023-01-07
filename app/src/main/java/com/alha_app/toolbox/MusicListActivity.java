package com.alha_app.toolbox;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MusicListActivity extends AppCompatActivity {
    private ArrayList<String> musictitle;
    private ArrayList<String> musicpaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        musictitle = new ArrayList<String>();
        musicpaths = new ArrayList<String>();

        readContent();

        ArrayList<Map<String, String>> listData = new ArrayList<>();
        for (int i = 0; i < musictitle.size(); i++) {
            Map<String, String> item = new HashMap<>();
            item.put("title", musictitle.get(i));
            item.put("path", musicpaths.get(i));
            listData.add(item);
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.musiclist);
        list.setAdapter(new SimpleAdapter(
                this,
                listData,
                R.layout.musiclist_item,
                new String[] {"title", "path"},
                new int[] {R.id.title, R.id.path}
        ));

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

    @SuppressLint("Range")
    private void readContent(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = null;

        // 例外を受け取る
        try {
            cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,null,null,null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    musictitle.add(cursor.getString(cursor.getColumnIndex(
                            MediaStore.Audio.Media.TITLE)));
                    musicpaths.add(cursor.getString(cursor.getColumnIndex(
                            MediaStore.Audio.Media.DATA)));
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

        } finally{
            if(cursor != null){
                cursor.close();
            }
        }
    }
}
