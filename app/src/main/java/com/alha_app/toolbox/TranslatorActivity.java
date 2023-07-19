package com.alha_app.toolbox;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alha_app.toolbox.entities.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.BHttpConnectionBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

public class TranslatorActivity extends AppCompatActivity {
    private static final int MAX_CONNECTIONS = 200;
    private static final int CONNECTION_TIMEOUT = 2 * 60 * 1000;
    private static final int SO_TIMEOUT = 10 * 60 * 1000;

    private static final String USERNAME = BuildConfig.USERNAME;
    private static final String KEY = BuildConfig.TTKEY;
    private static final String SECRET = BuildConfig.TTKEYSECRET;
    private static final String KEY_PARAM = "key";
    private static final String NAME_PARAM = "name";

    private EditText originalText;
    private TextView translatedText;

    private Handler handler;

    private String originalLang;
    private String translatedLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        // 戻るボタンを追加
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        handler = new Handler();
        originalText = findViewById(R.id.original_text);
        translatedText = findViewById(R.id.translated_text);

        Spinner originalSpinner = findViewById(R.id.original_language);
        Spinner translatedSpinner = findViewById(R.id.translated_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.language)
        );
        adapter.setDropDownViewResource(R.layout.translate_spinner_item);
        originalSpinner.setAdapter(adapter);
        translatedSpinner.setAdapter(adapter);

        String[] langList = getResources().getStringArray(R.array.language_url);

        originalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                originalLang = langList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        translatedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                translatedLang = langList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button translateButton = findViewById(R.id.translate_button);
        translateButton.setOnClickListener(view -> {
            if(originalLang.equals(translatedLang)){
                Toast.makeText(this, "同じ言語です", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> {
                try {
                    translate();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuButton) {
        boolean result = true;
        int buttonId = menuButton.getItemId();
        switch (buttonId) {
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

    private void translate() {
        String urlString = "https://mt-auto-minhon-mlt.ucri.jgn-x.jp/api/mt/generalNT_" + originalLang + "_" + translatedLang + "/";

        String json = "";
        JsonNode jsonResult = null;
        ObjectMapper mapper = new ObjectMapper();

        String originalStr = originalText.getText().toString();

        if(originalStr.equals("")) return;

        // Okhttp
        Map<String, String> formParamMap = new HashMap<>();
        formParamMap.put(KEY_PARAM, KEY);
        formParamMap.put(NAME_PARAM, USERNAME);
        formParamMap.put("type", "json");
        formParamMap.put("text", originalStr);

        OAuthConsumer consumer =  new OkHttpOAuthConsumer(KEY, SECRET);

        final FormBody.Builder formBuilder = new FormBody.Builder();
        formParamMap.forEach(formBuilder::add);
        RequestBody requestBody = formBuilder.build();

        try {
            Request request = (Request) consumer.sign(new Request.Builder()
                    .url(urlString)
                    .post(requestBody)
                    .build()).unwrap();

            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            Response response = okHttpClient.newCall(request).execute();
            System.out.println(response.code());
            json = response.body().string();

            jsonResult = mapper.readTree(json);
            String result = jsonResult.get("resultset").get("result").get("text").toString();
            handler.post(() -> {
                translatedText.setText(result.substring(1, result.length() - 1));
            });
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
