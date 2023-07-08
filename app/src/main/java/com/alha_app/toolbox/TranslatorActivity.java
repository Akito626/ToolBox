package com.alha_app.toolbox;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class TranslatorActivity extends AppCompatActivity {
    private static final int MAX_CONNECTIONS = 200;
    private static final int CONNECTION_TIMEOUT = 2 * 60 * 1000;
    private static final int SO_TIMEOUT = 10 * 60 * 1000;

    private static final String USERNAME = "";
    private static final String KEY = "";
    private static final String SECRET = "";
    private static final String BASE_URL = "";

    private static final String KEY_PARAM = "key";
    private static final String NAME_PARAM = "name";

    private static final String ENGINE = "patent_en_ja";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        // 戻るボタンを追加
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        Button translateButton = findViewById(R.id.translate_button);
        translateButton.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    //translate();
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

//    private void translate() throws Exception {
//        // configurations
//        int socketTimeout = 3;
//        int connectionTimeout = 3;
//        String userAgent = "My Http Client 0.1";
//        // request configuration
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectTimeout(CONNECTION_TIMEOUT)
//                .setSocketTimeout(SO_TIMEOUT)
//                .build();
//        // headers
//        List<Header> headers = new ArrayList<Header>();
//        headers.add(new BasicHeader("Accept-Charset", "utf-8"));
//        headers.add(new BasicHeader("Accept-Language", "ja, en;q=0.8"));
//        headers.add(new BasicHeader("User-Agent", userAgent));
//        // create client
//        HttpClient httpClient = HttpClientBuilder.create()
//                .setDefaultRequestConfig(requestConfig)
//                .setDefaultHeaders(headers).build();
//
//        try {
//            call(httpClient);
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }
//
//    private void call(HttpClient httpClient) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException {
//        String url = BASE_URL + ENGINE + "/";
//        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(KEY, SECRET);
//
//        HttpPost httpPost = new HttpPost(url);
//        List<NameValuePair> requestParams = new ArrayList<>();
//        requestParams.add(new BasicNameValuePair(KEY_PARAM, KEY));
//        requestParams.add(new BasicNameValuePair(NAME_PARAM, USERNAME));
//        requestParams.add(new BasicNameValuePair("text", "こんにちは"));
//
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(requestParams);
//        httpPost.setEntity(entity);
//
//        consumer.sign(httpPost);
//        try {
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            int respStatus = httpResponse.getStatusLine().getStatusCode();
//            String body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//            System.out.println("code : " + respStatus);
//            System.out.println(body);
//        } catch (Exception e){
//            System.out.println(e);
//        }
//    }
}
