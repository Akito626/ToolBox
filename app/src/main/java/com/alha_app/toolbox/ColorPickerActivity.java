package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerView;

import java.util.Objects;

public class ColorPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        TextView rgbText = findViewById(R.id.rgb_text);
        TextView hexText = findViewById(R.id.hex_text);
        View nowColor = findViewById(R.id.now_color);
        ColorPickerView colorPicker = findViewById(R.id.color_picker);
        colorPicker.setOnColorChangedListener(newColor -> {
            nowColor.setBackgroundColor(newColor);
            int r = (newColor >> 16) & 0xff;
            int g = (newColor >>  8) & 0xff;
            int b = (newColor      ) & 0xff;
            rgbText.setText(String.format("%3d, %3d, %3d", r, g, b));
            String hr = Integer.toHexString(r);
            String hg = Integer.toHexString(g);
            String hb = Integer.toHexString(b);
            System.out.println(hr.length());
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

        TextView cmykText = findViewById(R.id.cmyk_text);
        cmykText.setText((int)c + "%, " + (int)m + "%, " + (int)y + "%, " + (int)k + "%");
    }
}