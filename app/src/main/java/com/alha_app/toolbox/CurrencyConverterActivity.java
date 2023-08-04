package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
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

    private Map<String, Double> currencyMap = new LinkedHashMap<>();
    private List<String> spinnerItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        handler = new Handler();

        executor.execute(() -> getCurrencyRates());
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

    private void getCurrencyRates(){
        String urlString = "https://v6.exchangerate-api.com/v6/" + BuildConfig.CURRENCYKEY +"/latest/USD";
        String json;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResult;

        List<Map<String, String>> currencyData = new ArrayList<>();

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
                    Map<String, String> data = new HashMap<>();
                    spinnerItems.add(c.getCurrencyCode() + "　" + c.getDisplayName());
                    currencyMap.put(c.getCurrencyCode(), jsonResult.get("conversion_rates").get(c.getCurrencyCode()).asDouble());
                    data.put("currency_code", c.getCurrencyCode());
                    data.put("currency_name", c.getDisplayName());
                    data.put("currency_symbol", c.getSymbol());
                    currencyData.add(data);
                }
            }

            handler.post(() -> {
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
                        EditText beforeText = findViewById(R.id.before_currency_text);
                        TextView afterText = findViewById(R.id.after_currency_text);
                        ListView currencyList = findViewById(R.id.currency_list);

                        if(beforeText.getText().toString().equals("")) return;

                        double beforeNum = Double.parseDouble(beforeText.getText().toString());
                        double beforeRate = currencyMap.get(currencyData.get(beforeSpinner.getSelectedItemPosition()).get("currency_code"));
                        double afterRate = currencyMap.get(currencyData.get(afterSpinner.getSelectedItemPosition()).get("currency_code"));

                        // ベースの通貨の割合を1にする
                        beforeNum /= beforeRate;

                        String afterStr = String.format("%.4f", beforeNum * afterRate);
                        afterText.setText(afterStr);

                        List<Map<String, Object>> listData = new ArrayList<>();

                        // ListにCode, Name, Rateを表示
                        int i = 0;
                        for(Map.Entry<String, Double> entry : currencyMap.entrySet()){
                            System.out.println(currencyData.get(i).get("currency_name") + " " + currencyData.get(i).get("currency_symbol"));
                            Map<String, Object> item = new HashMap<>();
                            item.put("currency_code", entry.getKey());
                            item.put("currency_name", currencyData.get(i).get("currency_name"));
                            item.put("currency_rate", String.format("%.4f", beforeNum * entry.getValue()));
                            listData.add(item);
                            i++;
                        }

                        currencyList.setAdapter(new SimpleAdapter(
                                parent.getContext(),
                                listData,
                                R.layout.currency_list_item,
                                new String[] {"currency_code", "currency_name", "currency_rate"},
                                new int[] {R.id.currency_code, R.id.currency_name, R.id.currency_rate}
                        ));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                };

                beforeSpinner.setOnItemSelectedListener(listener);
                afterSpinner.setOnItemSelectedListener(listener);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}