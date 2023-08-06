package com.alha_app.toolbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alha_app.toolbox.entities.Tool;
import com.google.android.gms.oss.licenses.OssLicensesActivity;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Set<String> isFavorite = new HashSet<>();
    private SimpleAdapter adapter;
    private List<Map<String, Object>> listData = new ArrayList<>();

    private List<Tool> tools = new ArrayList<>();
    private int star;
    private boolean isPushed;
    private final String mFileName = "favorite.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初期化
        String[] toolNames = getResources().getStringArray(R.array.tools);
        isPushed = false;
        star = R.drawable.star;

        // 画像初期化
        int[] toolImages = {R.drawable.calculator, R.drawable.counter, R.drawable.stopwatch, R.drawable.timer, R.drawable.clock,
                R.drawable.ic_baseline_qr_code_scanner_24, R.drawable.weather, R.drawable.translate, R.drawable.ic_palette, R.drawable.ruler,
                R.drawable.ic_money};

        for(int i = 0; i < toolNames.length; i++){
            Tool tool = new Tool(i, toolNames[i], toolImages[i]);
            tools.add(tool);
        }

        loadFavorite();
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
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // メニューの設定
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
            case R.id.action_license:
                OssLicensesMenuActivity.setActivityTitle("ライセンス");
                startActivity(new Intent(getApplication(), OssLicensesMenuActivity.class));
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
                if(isFavorite.contains(appname)){
                    isFavorite.remove(appname);
                    for(int i = 0; i < tools.size(); i++){
                        if(appname.equals(tools.get(i).getName())){
                            listData.get(i).put("image_favorite", null);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    isFavorite.add(appname);
                    for(int i = 0; i < tools.size(); i++){
                        if(appname.equals(tools.get(i).getName())){
                            listData.get(i).put("image_favorite", star);
                            adapter.notifyDataSetChanged();
                        }
                    }
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
        listData.clear();
        for (Tool tool: tools) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", tool.getName());
            item.put("image", tool.getImage());
            if(isFavorite.contains(tool.getName())){
                item.put("image_favorite", star);
            }
            listData.add(item);
        }
        setListView();
    }

    // お気に入りに追加したアイテムのリスト
    public void prepareFavoriteList(){
        listData.clear();
        for (Tool tool: tools) {
            Map<String, Object> item = new HashMap<>();
            if(isFavorite.contains(tool.getName())) {
                item.put("name", tool.getName());
                item.put("image", tool.getImage());
                item.put("image_favorite", star);
                listData.add(item);
            }
        }
        setListView();
    }

    public void setListView(){
        // ListViewにデータをセットする
        ListView list = findViewById(R.id.list);
        adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.list_item,
                new String[] {"name", "image", "image_favorite"},
                new int[] {R.id.name, R.id.image, R.id.image_favorite}
        );
        list.setAdapter(adapter);

        // クリックイベント
        list.setOnItemClickListener((adapterView, view, position, l) -> {
            String appname = adapter.getItem(position).toString();
            int index = appname.indexOf("name=");
            appname = appname.substring(index+5, appname.length()-1);
            switch (appname){
                case "電卓":
                    startActivity(new Intent(getApplication(), CalcActivity.class));
                    break;
                case "カウンター":
                    startActivity(new Intent(getApplication(), CounterActivity.class));
                    break;
                case "ストップウォッチ":
                    startActivity(new Intent(getApplication(), StopwatchActivity.class));
                    break;
                case "タイマー":
                    startActivity(new Intent(getApplication(), TimerActivity.class));
                    break;
                case "時計":
                    startActivity(new Intent(getApplication(), ClockActivity.class));
                    break;
                case "QRコードリーダー":
                    startActivity(new Intent(getApplication(), QRScannerActivity.class));
                    break;
                case "天気":
                    startActivity(new Intent(getApplication(), WeatherActivity.class));
                    break;
                case "翻訳":
                    startActivity(new Intent(getApplication(), TranslatorActivity.class));
                    break;
                case "カラーピッカー":
                    startActivity(new Intent(getApplication(), ColorPickerActivity.class));
                    break;
                case "単位変換":
                    startActivity(new Intent(getApplication(), UnitConverterActivity.class));
                    break;
                case "通貨変換":
                    startActivity(new Intent(getApplication(), CurrencyConverterActivity.class));
                    break;
            }
        });

        registerForContextMenu(list);
    }

    public void saveFavorite(){
        // 保存
        OutputStream out = null;
        PrintWriter writer = null;
        try{
            out = this.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

            for (String name: isFavorite) {
                writer.println(name);
            }
        }catch(Exception e){
            Toast.makeText(this, "File save error!", Toast.LENGTH_LONG).show();
        }finally {
            if(writer != null){
                try{
                    writer.close();
                } catch (Exception e2){ };
            }
            if(out != null){
                try{
                    out.close();
                } catch (Exception e2){ };
            }
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
        String savePath = this.getFilesDir().getPath();
        File[] files = new File(savePath).listFiles(filter);

        if(files.length != 0) {
            String fileName = files[0].getName();
            //　ファイルを読み込み
            InputStream in = null;
            BufferedReader reader = null;
            try {
                // ファイルオープン
                in = this.openFileInput(fileName);
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String str = reader.readLine();
                while (str != null){
                    isFavorite.add(str);
                    str = reader.readLine();
                }

                reader.close();
                in.close();
            } catch (Exception e) {
                Toast.makeText(this, "File read error!", Toast.LENGTH_LONG).show();
            } finally {
                if(reader != null){
                    try{
                        reader.close();
                    } catch (Exception e2){ };
                }
                if(in != null){
                    try{
                        in.close();
                    } catch (Exception e2){ };
                }
            }
        }
    }
}