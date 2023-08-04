package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
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

    private Map<String, Double> currencyMap = new HashMap<>();
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

            for(Currency c : Currency.getAvailableCurrencies().stream()
                    .sorted(Comparator.comparing(Currency::getCurrencyCode)).collect(Collectors.toList())) {
                if(jsonResult.get("conversion_rates").get(c.getCurrencyCode()) != null) {
                    spinnerItems.add(c.getCurrencyCode() + "　" + c.getDisplayName());
                    currencyMap.put(c.getCurrencyCode(), jsonResult.get("conversion_rates").get(c.getCurrencyCode()).asDouble());
                }
            }

            handler.post(() -> {
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
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}