package com.alha_app.toolbox;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
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

public class MusicListActivity extends AppCompatActivity {
    private ArrayList<String> musictitle;
    private ArrayList<String> musicpaths;
    private final String mFileName = "Timer.txt";

    private String hour;
    private String minute;
    private String second;
    private String music;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        musictitle = new ArrayList<String>();
        musicpaths = new ArrayList<String>();
        musictitle.add("defaultmusic");
        musicpaths.add(null);

        readContent();
        loadFile();

        ArrayList<Map<String, String>> listData = new ArrayList<>();
        for (int i = 0; i < musictitle.size(); i++) {
            Map<String, String> item = new HashMap<>();
            item.put("title", musictitle.get(i));
            item.put("path", musicpaths.get(i));
            listData.add(item);
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.musiclist);
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.musiclist_item,
                new String[] {"title", "path"},
                new int[] {R.id.title, R.id.path}
        );
        list.setAdapter(adapter);

        // テキストフィルターを有効にする
        list.setTextFilterEnabled(true);
        SearchView searchView = findViewById(R.id.search);

        // クリックイベント
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapter.getItem(i).toString();
                int beginindex = str.indexOf("path=");
                int endindex = str.indexOf("title=");
                path = str.substring(beginindex+5, endindex-2);
                music = str.substring(endindex+6, str.length()-1);
                saveFile();
                finish();
            }
        });

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
    }

    public void saveFile(){
        // 保存
        OutputStream out = null;
        PrintWriter writer = null;
        try{
            out = this.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            // タイトル書き込み
            writer.println(hour);
            writer.println(minute);
            writer.println(second);
            writer.println(music);
            writer.println(path);
            writer.close();
            out.close();
        }catch(Exception e){
            Toast.makeText(this, "File save error!", Toast.LENGTH_LONG).show();
        }
    }
}
