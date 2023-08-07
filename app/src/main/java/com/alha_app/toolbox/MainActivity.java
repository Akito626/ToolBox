package com.alha_app.toolbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alha_app.toolbox.database.AppDatabase;
import com.alha_app.toolbox.database.ToolDao;
import com.alha_app.toolbox.database.ToolEntity;
import com.alha_app.toolbox.entities.Tool;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler();
    private SimpleAdapter adapter;
    private List<Map<String, Object>> listData = new ArrayList<>();

    private List<Tool> tools = new ArrayList<>();
    private int star;
    private boolean isPushed;
    private int checkedSortItem;

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

        loadDB();
    }

    @Override
    public void onPause(){
        super.onPause();
        writeDB();
    }

    @Override
    public void onResume(){
        super.onResume();

        sortList();
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
            case R.id.action_sort:
                String[] choiceList = {"デフォルト", "名前順", "使用回数順"};
                new AlertDialog.Builder(this)
                        .setTitle("並び替え")
                        .setSingleChoiceItems(choiceList, checkedSortItem, (dialog, which) -> {
                            checkedSortItem = which;
                        })
                        .setPositiveButton("OK", (dialog, which) -> {
                            sortList();

                            if(isPushed){
                                prepareFavoriteList();
                            } else {
                                prepareList();
                            }
                        })
                        .show();
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
                // お気に入りに入っていれば削除、入っていなければ追加してリストを再表示
                index = tools.indexOf(new Tool(appname));
                if(tools.get(index).isFavorite()){
                    tools.get(index).setFavorite(false);
                    for(int i = 0; i < listData.size(); i++){
                        if(appname.equals(listData.get(i).get("name"))){
                            listData.get(i).put("image_favorite", null);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    tools.get(index).setFavorite(true);
                    for(int i = 0; i < listData.size(); i++){
                        if(appname.equals(listData.get(i).get("name"))){
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
            if(tool.isFavorite()){
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
            if(tool.isFavorite()) {
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

            // 押した回数をカウント
            index = tools.indexOf(new Tool(appname));
            tools.get(index).addCount();

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

    // toolsをソートする
    public void sortList(){
        switch (checkedSortItem) {
            case 0:
                tools.sort(Comparator.comparing(Tool::getId));
                break;
            case 1:
                tools.sort(Comparator.comparing(Tool::getName));
                break;
            case 2:
                tools.sort(Comparator.comparing(Tool::getCount).reversed());
                break;
        }
    }

    // データベースにデータを書き込む
    private void writeDB(){
        executor.execute(() -> {
            AppDatabase db = Room.databaseBuilder(getApplication(),
                    AppDatabase.class, "TOOL_DATA").build();
            ToolDao dao = db.toolDao();
            dao.deleteAll();
            tools.sort(Comparator.comparing(Tool::getId));
            for (Tool tool: tools){
                ToolEntity entity = new ToolEntity(tool.getId(), tool.getCount(), tool.isFavorite());
                dao.insert(entity);
            }
        });
    }

    // データベースからデータを読み込む
    private void loadDB(){
        executor.execute(() -> {
            AppDatabase db = Room.databaseBuilder(getApplication(),
                    AppDatabase.class, "TOOL_DATA").build();
            ToolDao dao = db.toolDao();
            List<ToolEntity> toolData = dao.getAll();
            tools.sort(Comparator.comparing(Tool::getId));
            for(int i = 0; i < toolData.size(); i++){
                tools.get(i).setCount(toolData.get(i).getCount());
                tools.get(i).setFavorite(toolData.get(i).isFavorite());
            }

            handler.post(() -> prepareList());
        });
    }
}