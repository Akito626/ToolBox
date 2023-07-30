package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class UnitConverterActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_converter);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        // 単位の種類を選択するスピナーの準備
        Spinner unitTypeSpinner = findViewById(R.id.unit_type_spinner);
        adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                getResources().getStringArray(R.array.unit_type)
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        unitTypeSpinner.setAdapter(adapter);
        unitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                switch (position){
                    case 0:
                        prepareAdapter(getResources().getStringArray(R.array.unit_length));
                        break;
                    case 1:
                        prepareAdapter(getResources().getStringArray(R.array.unit_weight));
                        break;
                    case 2:
                        prepareAdapter(getResources().getStringArray(R.array.unit_area));
                        break;
                    case 3:
                        prepareAdapter(getResources().getStringArray(R.array.unit_volume));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        EditText unitNumText = findViewById(R.id.unit_number_text);

        // リセットボタンを準備
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            unitNumText.clearFocus();
            unitNumText.setText("");
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

    // 単位を選択するスピナーを準備
    private void prepareAdapter(String[] units){
        Spinner unitSpinner = findViewById(R.id.unit_spinner);

        adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                units
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        unitSpinner.setAdapter(adapter);
    }
}