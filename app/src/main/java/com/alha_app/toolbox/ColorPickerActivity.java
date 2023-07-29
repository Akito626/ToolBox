package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerView;

import java.util.Objects;
import java.util.regex.Pattern;

public class ColorPickerActivity extends AppCompatActivity {
    private int r;
    private int g;
    private int b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        EditText rgbText = findViewById(R.id.rgb_text);
        EditText hexText = findViewById(R.id.hex_text);
        EditText cmykText = findViewById(R.id.cmyk_text);
        EditText hsvText = findViewById(R.id.hsv_text);
        EditText hslText = findViewById(R.id.hsl_text);
        View nowColor = findViewById(R.id.now_color);
        ColorPickerView colorPicker = findViewById(R.id.color_picker);

        // rgbを入力した時のイベント
        rgbText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(rgbText.hasFocus()){
                    String[] rgbStr = s.toString().replaceAll(" ", "").split(",");
                    if(rgbStr.length != 3) return;
                    Pattern pattern = Pattern.compile("^[0-9]+$|-[0-9]+$");
                    if(pattern.matcher(rgbStr[0]).matches()){
                        r = Integer.parseInt(rgbStr[0]);
                    } else {
                        r = 0;
                    }
                    if(pattern.matcher(rgbStr[1]).matches()){
                        g = Integer.parseInt(rgbStr[1]);
                    } else {
                        g = 0;
                    }
                    if(pattern.matcher(rgbStr[2]).matches()){
                        b = Integer.parseInt(rgbStr[2]);
                    } else {
                        b = 0;
                    }
                    int color = Color.rgb(r, g, b);
                    colorPicker.setColor(color);
                    nowColor.setBackgroundColor(color);
                    rgbToHEX();
                    rgbToCMYK();
                    rgbToHSV();
                    rgbToHSL();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // hexを入力した時のイベント
        hexText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(hexText.hasFocus()){
                    String hexStr = s.toString().replaceAll("#", "");
                    if(hexStr.length() != 6) return;
                    String hr = hexStr.substring(0, 2);
                    String hg = hexStr.substring(2, 4);
                    String hb = hexStr.substring(4, 6);
                    Pattern pattern = Pattern.compile("[0-9a-fA-F]+");
                    if(!pattern.matcher(hr).matches()) return;
                    if(!pattern.matcher(hg).matches()) return;
                    if(!pattern.matcher(hb).matches()) return;

                    hexToRGB(hr, hg, hb);
                    int color = Color.rgb(r, g, b);
                    colorPicker.setColor(color);
                    nowColor.setBackgroundColor(color);
                    rgbToCMYK();
                    rgbToHSV();
                    rgbToHSL();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // cmykを入力した時のイベント
        cmykText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cmykText.hasFocus()){
                    double c = 0;
                    double m = 0;
                    double y = 0;
                    double k = 0;
                    String[] cmykStr = s.toString().replaceAll(" ", "").replaceAll("%", "").split(",");
                    if(cmykStr.length != 4) return;
                    Pattern pattern = Pattern.compile("^[0-9]+$|-[0-9]+$");
                    if(pattern.matcher(cmykStr[0]).matches()){
                        if(Integer.parseInt(cmykStr[0]) > 100 || Integer.parseInt(cmykStr[0]) < 0) return;
                        c = Integer.parseInt(cmykStr[0]);
                    }
                    if(pattern.matcher(cmykStr[1]).matches()){
                        if(Integer.parseInt(cmykStr[1]) > 100 || Integer.parseInt(cmykStr[1]) < 0) return;
                        m = Integer.parseInt(cmykStr[1]);
                    }
                    if(pattern.matcher(cmykStr[2]).matches()){
                        if(Integer.parseInt(cmykStr[2]) > 100 || Integer.parseInt(cmykStr[2]) < 0) return;
                        y = Integer.parseInt(cmykStr[2]);
                    }
                    if(pattern.matcher(cmykStr[3]).matches()){
                        if(Integer.parseInt(cmykStr[3]) > 100 || Integer.parseInt(cmykStr[3]) < 0) return;
                        k = Integer.parseInt(cmykStr[3]);
                    }
                    cmykToRGB(c, m, y, k);
                    int color = Color.rgb(r, g, b);
                    colorPicker.setColor(color);
                    nowColor.setBackgroundColor(color);
                    rgbToHEX();
                    rgbToHSV();
                    rgbToHSL();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // hsvを入力したときのイベント
        hsvText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                if(hsvText.hasFocus()){
                    double h = 0;
                    double s = 0;
                    double v = 0;
                    String[] hsvStr = str.toString().replaceAll(" ", "").replaceAll("%", "").replaceAll("°", "").split(",");
                    if(hsvStr.length != 3) return;
                    Pattern pattern = Pattern.compile("^[0-9]+$|-[0-9]+$");
                    if(pattern.matcher(hsvStr[0]).matches()){
                        if(Integer.parseInt(hsvStr[0]) > 100 || Integer.parseInt(hsvStr[0]) < 0) return;
                        h = Integer.parseInt(hsvStr[0]);
                    }
                    if(pattern.matcher(hsvStr[1]).matches()){
                        if(Integer.parseInt(hsvStr[1]) > 100 || Integer.parseInt(hsvStr[1]) < 0) return;
                        s = Integer.parseInt(hsvStr[1]);
                    }
                    if(pattern.matcher(hsvStr[2]).matches()){
                        if(Integer.parseInt(hsvStr[2]) > 100 || Integer.parseInt(hsvStr[2]) < 0) return;
                        v = Integer.parseInt(hsvStr[2]);
                    }
                    hsvToRGB(h, s, v);
                    int color = Color.rgb(r, g, b);
                    colorPicker.setColor(color);
                    nowColor.setBackgroundColor(color);
                    rgbToHEX();
                    rgbToCMYK();
                    rgbToHSL();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        hslText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                if(hslText.hasFocus()){
                    double h = 0;
                    double s = 0;
                    double l = 0;
                    String[] hslStr = str.toString().replaceAll(" ", "").replaceAll("%", "").replaceAll("°", "").split(",");
                    if(hslStr.length != 3) return;
                    Pattern pattern = Pattern.compile("^[0-9]+$|-[0-9]+$");
                    if(pattern.matcher(hslStr[0]).matches()){
                        if(Integer.parseInt(hslStr[0]) > 100 || Integer.parseInt(hslStr[0]) < 0) return;
                        h = Integer.parseInt(hslStr[0]);
                    }
                    if(pattern.matcher(hslStr[1]).matches()){
                        if(Integer.parseInt(hslStr[1]) > 100 || Integer.parseInt(hslStr[1]) < 0) return;
                        s = Integer.parseInt(hslStr[1]);
                    }
                    if(pattern.matcher(hslStr[2]).matches()){
                        if(Integer.parseInt(hslStr[2]) > 100 || Integer.parseInt(hslStr[2]) < 0) return;
                        l = Integer.parseInt(hslStr[2]);
                    }
                    hslToRGB(h, s, l);
                    int color = Color.rgb(r, g, b);
                    colorPicker.setColor(color);
                    nowColor.setBackgroundColor(color);
                    rgbToHEX();
                    rgbToCMYK();
                    rgbToHSV();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // カラーピッカーの準備
        colorPicker.setColor(Color.rgb(255, 0, 0));     // デフォルトカラー
        colorPicker.setOnColorChangedListener(newColor -> {
            nowColor.setBackgroundColor(newColor);
            r = (newColor >> 16) & 0xff;
            g = (newColor >>  8) & 0xff;
            b = (newColor      ) & 0xff;
            rgbText.setText(String.format("%3d, %3d, %3d", r, g, b));
            rgbToHEX();
            rgbToCMYK();
            rgbToHSV();
            rgbToHSL();
        });
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

    // rgbからhexに変換して表示
    private void rgbToHEX(){
        String hr = Integer.toHexString(r);
        String hg = Integer.toHexString(g);
        String hb = Integer.toHexString(b);
        if(hr.length() == 1) hr = 0+hr;
        if(hg.length() == 1) hg = 0+hg;
        if(hb.length() == 1) hb = 0+hb;

        EditText hexText = findViewById(R.id.hex_text);
        hexText.setText("#" + hr + hg + hb);
    }

    private void hexToRGB(String hr, String hg, String hb){
        r = Integer.parseInt(hr, 16);
        g = Integer.parseInt(hg, 16);
        b = Integer.parseInt(hb, 16);

        EditText rgbText = findViewById(R.id.rgb_text);
        rgbText.setText(String.format("%3d, %3d, %3d", r, g, b));
    }

    // rgbからcmykに変換して表示
    private void rgbToCMYK(){
        double cmykR = (double) r / 255;
        double cmykG = (double) g / 255;
        double cmykB = (double) b / 255;

        double max = max3(cmykR, cmykG, cmykB);
        double k = 1 - max;
        double c = (1 - cmykR - k) / (1 - k) * 100;
        double m = (1 - cmykG - k) / (1 - k) * 100;
        double y = (1 - cmykB - k) / (1 - k) * 100;
        k *= 100;

        EditText cmykText = findViewById(R.id.cmyk_text);
        cmykText.setText((int) Math.round(c) + "%, " + (int) Math.round(m) + "%, " + (int) Math.round(y) + "%, " + (int) Math.round(k) + "%");
    }

    // cmykからrgbに変換して色を返す
    private void cmykToRGB(double c, double m, double y, double k){
        k /= 100;
        double cmykR = -(c / 100 * (1 - k)) + 1 - k;
        double cmykG = -(m / 100 * (1 - k)) + 1 - k;
        double cmykB = -(y / 100 * (1 - k)) + 1 - k;

        r = (int) Math.round(cmykR * 255);
        g = (int) Math.round(cmykG * 255);
        b = (int) Math.round(cmykB * 255);

        EditText rgbText = findViewById(R.id.rgb_text);
        rgbText.setText(String.format("%3d, %3d, %3d", r, g, b));
    }

    // rgbからhsvに変換してhsvを表示
    private void rgbToHSV(){
        double max = max3(r, g, b);
        double min = min3(r, g, b);

        double h = 0;
        if(r == g && g == b){
            h = 0;
        } else if(r == max){
            h = 60 * ((g - b) / (max - min));
            if(h < 0){
                h = 360 + h;
            }
        } else if(g == max){
            h = 60 * ((b - r) / (max - min)) + 120;
        } else if(b == max){
            h = 60 * ((r - g) / (max - min)) + 240;
        }
        double s = (max - min) / max * 100;
        double v = max / 255 * 100;

        EditText hsvText = findViewById(R.id.hsv_text);
        hsvText.setText((int) Math.round(h) + "°, " + (int) Math.round(s) + "%, " + (int) Math.round(v) + "%");
    }

    private void hsvToRGB(double h, double s, double v){
        double max = v / 100 * 255;
        double min = max - ((s / 100) * max);

        if(h <= 60){
            r = (int) Math.round(max);
            g = (int) Math.round((h / 60) * (max - min) + min);
            b = (int) Math.round(min);
        } else if(h <= 120){
            r = (int) Math.round(((120 - h) / 60) * (max - min) + min);
            g = (int) Math.round(max);
            b = (int) Math.round(min);
        } else if(h <= 180){
            r = (int) Math.round(min);
            g = (int) Math.round(max);
            b = (int) Math.round((h - 120) / 60 * (max - min) + min);
        } else if(h <= 240){
            r = (int) Math.round(min);
            g = (int) Math.round((240 - h) / 60 * (max - min) + min);
            b = (int) Math.round(max);
        } else if(h <= 300){
            r = (int) Math.round((h - 240) / 60 * (max - min) + min);
            g = (int) Math.round(min);
            b = (int) Math.round(max);
        } else {
            r = (int) Math.round(max);
            g = (int) Math.round(min);
            b = (int) Math.round((360 - h) / 60 * (max - min) + min);
        }

        EditText rgbText = findViewById(R.id.rgb_text);
        rgbText.setText(String.format("%3d, %3d, %3d", r, g, b));
    }

    // rgbからhslに変換して表示
    private void rgbToHSL(){
        double max = max3(r, g, b);
        double min = min3(r, g, b);

        double h = 0;
        if(r == g && g == b){
            h = 0;
        } else if(r == max){
            h = 60 * ((g - b) / (max - min));
            if(h < 0){
                h = 360 + h;
            }
        } else if(g == max){
            h = 60 * ((b - r) / (max - min)) + 120;
        } else if(b == max){
            h = 60 * ((r - g) / (max - min)) + 240;
        }

        // 収束値を求め、彩度を求める
        double cnt = (max + min) / 2;
        double s;
        if(cnt <= 127){
            s = (cnt - min) / cnt;
        } else {
            cnt = 255 - cnt;
            s = (max - cnt) / (255 - cnt);
        }
        s *= 100;

        // 輝度を求める
        double l = (max + min) / 2 / 255 * 100;

        EditText hslText = findViewById(R.id.hsl_text);
        hslText.setText((int) Math.round(h) + "°, " + (int) Math.round(s) + "%, " + (int) Math.round(l) + "%");
    }

    private void hslToRGB(double h, double s, double l){
        double max;
        double min;
        if(l <= 49){
            max = 2.55 * (l + l * (s / 100));
            min = 2.55 * (l - l * (s / 100));
        } else {
            max = 2.55 * (l + (100 - l) * (s / 100));
            min = 2.55 * (l - (100 - l) * (s / 100));
        }

        if(h <= 60){
            r = (int) Math.round(max);
            g = (int) Math.round((h / 60) * (max - min) + min);
            b = (int) Math.round(min);
        } else if(h <= 120){
            r = (int) Math.round(((120 - h) / 60) * (max - min) + min);
            g = (int) Math.round(max);
            b = (int) Math.round(min);
        } else if(h <= 180){
            r = (int) Math.round(min);
            g = (int) Math.round(max);
            b = (int) Math.round((h - 120) / 60 * (max - min) + min);
        } else if(h <= 240){
            r = (int) Math.round(min);
            g = (int) Math.round((240 - h) / 60 * (max - min) + min);
            b = (int) Math.round(max);
        } else if(h <= 300){
            r = (int) Math.round((h - 240) / 60 * (max - min) + min);
            g = (int) Math.round(min);
            b = (int) Math.round(max);
        } else {
            r = (int) Math.round(max);
            g = (int) Math.round(min);
            b = (int) Math.round((360 - h) / 60 * (max - min) + min);
        }

        EditText rgbText = findViewById(R.id.rgb_text);
        rgbText.setText(String.format("%3d, %3d, %3d", r, g, b));
    }

    private double max3(double num1, double num2, double num3){
        double max = Math.max(num1, num2);
        max = Math.max(max, num3);
        return max;
    }

    private double min3(double num1, double num2, double num3){
        double min = Math.min(num1, num2);
        min = Math.min(min, num3);
        return min;
    }
}