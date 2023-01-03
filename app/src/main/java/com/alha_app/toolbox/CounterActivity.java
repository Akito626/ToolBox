package com.alha_app.toolbox;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CounterActivity extends AppCompatActivity {
    private EditText countText;
    private boolean isChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        countText = findViewById(R.id.counttext);
        countText.setText("0");

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 0;
                Button btn = (Button) v;
                String str = "";

                if(countText.getText().toString().matches("[+ -]*?\\d+")){
                    num = Integer.parseInt(countText.getText().toString());
                }

                str = ((Button) v).getText().toString();
                if(str.equals("-")){
                    if(num == -99999){
                        num = 999999;
                    }else {
                        num--;
                    }
                } else if(str.equals("+")){
                    if(num == 999999){
                        num = -99999;
                    }else {
                        num++;
                    }
                } else {
                    num = 0;
                }

                countText.setText(String.valueOf(num));
            }
        };

        findViewById(R.id.plusbutton).setOnClickListener(btnListener);
        findViewById(R.id.minusbutton).setOnClickListener(btnListener);
        findViewById(R.id.resetbutton).setOnClickListener(btnListener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        countText.setTextSize(countText.getWidth()/7);
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
}
