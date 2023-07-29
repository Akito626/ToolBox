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
        TextView hexText = findViewById(R.id.hex_text);
        EditText cmykText = findViewById(R.id.cmyk_text);
        EditText hsvText = findViewById(R.id.hsv_text);
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
                    colorPicker.setColor(Color.rgb(r, g, b));
                    nowColor.setBackgroundColor(Color.rgb(r, g, b));
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
                    String[] cmykStr = s.toString().replaceAll(" ", "").replaceAll(",", "").split("%");
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
                    int color = cmykToRGB(c, m, y, k);
                    colorPicker.setColor(color);
                    nowColor.setBackgroundColor(color);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        hsvText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(hsvText.hasFocus()){
                    String[] hsvStr = s.toString().replaceAll(" ", "").replaceAll("%", "").replaceAll("°", "").split(",");
                    if(hsvStr.length != 3) return;
                    Pattern pattern = Pattern.compile("^[0-9]+$|-[0-9]+$");
                    if(pattern.matcher(hsvStr[0]).matches()){
                        if(Integer.parseInt(hsvStr[0]) > 100 || Integer.parseInt(hsvStr[0]) < 0) return;
                        r = Integer.parseInt(hsvStr[0]);
                    }
                    if(pattern.matcher(hsvStr[1]).matches()){
                        if(Integer.parseInt(hsvStr[1]) > 100 || Integer.parseInt(hsvStr[1]) < 0) return;
                        g = Integer.parseInt(hsvStr[1]);
                    }
                    if(pattern.matcher(hsvStr[2]).matches()){
                        if(Integer.parseInt(hsvStr[2]) > 100 || Integer.parseInt(hsvStr[2]) < 0) return;
                        b = Integer.parseInt(hsvStr[2]);
                    }
                    int color = hsvToRGB(r, g, b);
                    colorPicker.setColor(color);
                    nowColor.setBackgroundColor(color);
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
            String hr = Integer.toHexString(r);
            String hg = Integer.toHexString(g);
            String hb = Integer.toHexString(b);
            if(hr.length() == 1) hr = 0+hr;
            if(hg.length() == 1) hg = 0+hg;
            if(hb.length() == 1) hb = 0+hb;
            hexText.setText("#" + hr + hg + hb);

            rgbToCMYK(r, g, b);
            rgbToHSV(r, g, b);
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

    // rgbからcmykに変換して表示
    private void rgbToCMYK(int r, int g, int b){
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
        cmykText.setText((int)c + "%, " + (int)m + "%, " + (int)y + "%, " + (int)k + "%");
    }

    // cmykからrgbに変換して色を返す
    private int cmykToRGB(double c, double m, double y, double k){
        k /= 100;
        double cmykR = -(c / 100 * (1 - k)) + 1 - k;
        double cmykG = -(m / 100 * (1 - k)) + 1 - k;
        double cmykB = -(y / 100 * (1 - k)) + 1 - k;

        double r = cmykR * 255;
        double g = cmykG * 255;
        double b = cmykB * 255;

        int color = Color.rgb((int) Math.round(r), (int) Math.round(g), (int) Math.round(b));

        EditText rgbText = findViewById(R.id.rgb_text);
        rgbText.setText(String.format("%3d, %3d, %3d", (int) Math.round(r), (int) Math.round(g), (int) Math.round(b)));

        return color;
    }

    // rgbからhsvに変換してhsvを表示
    private void rgbToHSV(int r, int g, int b){
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
        hsvText.setText((int) h + "°, " + (int) s + "%, " + (int) v + "%");
    }

    private int hsvToRGB(double h, double s, double v){
        double max = v / 100 * 255;
        double min = max - ((s / 100) * max);

        int r;
        int g;
        int b;

        if(h <= 60){
            r = (int) Math.round(max);
            g = (int) Math.round(((h / 100 * 255 / 60) * (max - min) + min));
            b = (int) Math.round(min);
        } else if(h <= 120){
            r = (int) Math.round((((120 - h / 100 * 255) * (max - min) + min)));
            g = (int) Math.round(max);
            b = (int) Math.round(min);
        } else if(h <= 180){
            r = (int) Math.round(min);
            g = (int) Math.round(max);
            b = (int) Math.round((((h / 100 * 255 - 120) / 60 * (max - min) + min)));
        } else if(h <= 240){
            r = (int) Math.round(min);
            g = (int) Math.round(((240 - h / 100 * 255) / 60 * (max - min) + min));
            b = (int) Math.round(max);
        } else if(h <= 300){
            r = (int) Math.round(((h / 100 * 255 - 240) / 60 * (max - min) + min));
            g = (int) Math.round(min);
            b = (int) Math.round(max);
        } else {
            r = (int) Math.round(max);
            g = (int) Math.round(min);
            b = (int) Math.round(((360 - h / 100 * 255) / 60 * (max - min) + min));
        }

        EditText rgbText = findViewById(R.id.rgb_text);
        rgbText.setText(String.format("%3d, %3d, %3d", r, g, b));

        int color = Color.rgb(r, g, b);
        return color;
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