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
                        if(Integer.parseInt(cmykStr[0]) > 100) return;
                        c = Integer.parseInt(cmykStr[0]);
                    }
                    if(pattern.matcher(cmykStr[1]).matches()){
                        if(Integer.parseInt(cmykStr[1]) > 100) return;
                        m = Integer.parseInt(cmykStr[1]);
                    }
                    if(pattern.matcher(cmykStr[2]).matches()){
                        if(Integer.parseInt(cmykStr[2]) > 100) return;
                        y = Integer.parseInt(cmykStr[2]);
                    }
                    if(pattern.matcher(cmykStr[3]).matches()){
                        if(Integer.parseInt(cmykStr[3]) > 100) return;
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

        double max;
        max = Math.max(cmykR, cmykG);
        max = Math.max(max, cmykB);
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
}