package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alha_app.toolbox.database.CurrencyDao;
import com.alha_app.toolbox.database.CurrencyDatabase;
import com.alha_app.toolbox.database.CurrencyEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverterActivity extends AppCompatActivity {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler;
    private List<CurrencyEntity> currencyData = new ArrayList<>();
    private List<String> spinnerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        handler = new Handler();

        EditText beforeText = findViewById(R.id.before_currency_text);
        beforeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prepareList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadRates();
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

    // rateが更新されていれば取得、そうでなければ保存しているデータを読み込む
    private void loadRates(){
        SharedPreferences preferences = getSharedPreferences("prefData", MODE_PRIVATE);
        long updateTime = preferences.getLong("updateTime", 0);

        if(updateTime < (System.currentTimeMillis() / 1000)) {
            executor.execute(() -> getCurrencyRates());
        } else {
            loadDB();
        }
    }

    private void getCurrencyRates(){
        String urlString = "https://v6.exchangerate-api.com/v6/" + BuildConfig.CURRENCYKEY +"/latest/USD";
        String json;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResult;

        Request request = new Request.Builder()
                .url(urlString)
                .build();

        try {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Response response = client.newCall(request).execute();

            if(!response.isSuccessful()){
                handler.post(() -> Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_SHORT).show());
                return;
            }
            json = response.body().string();
            jsonResult = mapper.readTree(json);

            // javaで利用できる通貨を取得し、APIで利用できる通貨のデータのみ保存
            for(Currency c : Currency.getAvailableCurrencies().stream()
                    .sorted(Comparator.comparing(Currency::getCurrencyCode)).collect(Collectors.toList())) {
                if(jsonResult.get("conversion_rates").get(c.getCurrencyCode()) != null) {
                    CurrencyEntity entity = new CurrencyEntity(c.getCurrencyCode(), c.getDisplayName(), jsonResult.get("conversion_rates").get(c.getCurrencyCode()).asDouble());
                    currencyData.add(entity);
                    spinnerItems.add(c.getCurrencyCode() + "　" + c.getDisplayName());
                }
            }

            // 次にRateが更新される時間を保存
            SharedPreferences preferences = getSharedPreferences("prefData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("updateTime", jsonResult.get("time_next_update_unix").asLong());
            editor.commit();

            writeDB();

            handler.post(() -> prepareSpinner());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void prepareSpinner(){
        // spinner用のAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                spinnerItems
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);

        Spinner beforeSpinner = findViewById(R.id.before_currency_spinner);
        Spinner afterSpinner = findViewById(R.id.after_currency_spinner);
        beforeSpinner.setAdapter(adapter);
        afterSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prepareList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        beforeSpinner.setOnItemSelectedListener(listener);
        afterSpinner.setOnItemSelectedListener(listener);
    }

    private void prepareList(){
        Spinner beforeSpinner = findViewById(R.id.before_currency_spinner);
        Spinner afterSpinner = findViewById(R.id.after_currency_spinner);
        EditText beforeText = findViewById(R.id.before_currency_text);
        TextView afterText = findViewById(R.id.after_currency_text);
        ListView currencyList = findViewById(R.id.currency_list);

        if(beforeText.getText().toString().equals("")) {
            afterText.setText("");
            return;
        }

        double beforeNum = Double.parseDouble(beforeText.getText().toString());
        double beforeRate = currencyData.get(beforeSpinner.getSelectedItemPosition()).getCurrencyRate();
        double afterRate = currencyData.get(afterSpinner.getSelectedItemPosition()).getCurrencyRate();

        // ベースの通貨の割合を1にする
        beforeNum /= beforeRate;

        String afterStr = String.format("%.4f", beforeNum * afterRate);
        afterText.setText(afterStr);

        List<Map<String, Object>> listData = new ArrayList<>();

        // ListにCode, Name, Rateを表示
        for(CurrencyEntity entity : currencyData){
            Map<String, Object> item = new HashMap<>();
            item.put("currency_code", entity.getCurrencyCode());
            item.put("currency_name", entity.getCurrencyName());
            item.put("currency_rate", String.format("%.4f", beforeNum * entity.getCurrencyRate()));
            listData.add(item);
        }

        currencyList.setAdapter(new SimpleAdapter(
                this,
                listData,
                R.layout.currency_list_item,
                new String[] {"currency_code", "currency_name", "currency_rate"},
                new int[] {R.id.currency_code, R.id.currency_name, R.id.currency_rate}
        ));
    }

    // データベースにデータを書き込む
    private void writeDB(){
        executor.execute(() -> {
            CurrencyDatabase db = Room.databaseBuilder(getApplication(),
                    CurrencyDatabase.class, "CURRENCY_DATA").build();
            CurrencyDao dao = db.currencyDao();
            dao.deleteAll();
            for(CurrencyEntity entity: currencyData){
                dao.insert(entity);
            }
        });
    }

    // データベースからデータを読み込む
    private void loadDB(){
        executor.execute(() -> {
            CurrencyDatabase db = Room.databaseBuilder(getApplication(),
                    CurrencyDatabase.class, "CURRENCY_DATA").build();
            CurrencyDao dao = db.currencyDao();
            currencyData = dao.getAll();

            handler.post(() -> {
                for(CurrencyEntity entity : currencyData){
                    spinnerItems.add(entity.getCurrencyCode() + "　" + entity.getCurrencyName());
                }

                prepareSpinner();
            });
        });
    }
}