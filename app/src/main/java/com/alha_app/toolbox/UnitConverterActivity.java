package com.alha_app.toolbox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Objects;

public class UnitConverterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_converter);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        String[] data = {"cm", "m", "km"};

        Spinner unitKindsSpinner = findViewById(R.id.unit_kinds_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                data
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        unitKindsSpinner.setAdapter(adapter);

        Spinner unitSpinner = findViewById(R.id.unit_spinner);
        adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                data
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        unitSpinner.setAdapter(adapter);

        EditText unitNumText = findViewById(R.id.unit_number_text);

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
}