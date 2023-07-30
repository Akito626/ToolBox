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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Spinner unitSpinner = findViewById(R.id.unit_spinner);

        // リセットボタンを準備
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(v -> {
            unitNumText.clearFocus();
            unitNumText.setText("");
        });


        Button calcButton = findViewById(R.id.calc_button);
        calcButton.setOnClickListener(v -> {
            if(unitNumText.getText().toString().equals("")) return;
            prepareList(unitTypeSpinner.getSelectedItemPosition(), unitSpinner.getSelectedItem().toString(), Integer.parseInt(unitNumText.getText().toString()));
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

    private void prepareList(int type, String unit, int num){
        List<Map<String, Object>> listData = new ArrayList<>();

        System.out.println(type);
        switch (type){
            case 0:
                listData = calcLength(unit, num);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }

        ListView unitList = findViewById(R.id.unit_list);
        unitList.setAdapter(new SimpleAdapter(
                this,
                listData,
                R.layout.unit_list_item,
                new String[]{"unit_num", "unit"},
                new int[]{R.id.unit_num, R.id.unit}
        ));
    }

    private List<Map<String, Object>> calcLength(String unit, double num){
        List<Map<String, Object>> listData = new ArrayList<>();
        Map<String, Object> item;
        switch (unit){
            case "μm":
                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num).toPlainString());
                item.put("unit", "μm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 1000).toPlainString());
                item.put("unit", "mm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 10000).toPlainString());
                item.put("unit", "cm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 1000000).toPlainString());
                item.put("unit", "m");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 1000000000).toPlainString());
                item.put("unit", "km");
                listData.add(item);
                break;
            case "mm":
                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 1000).toPlainString());
                item.put("unit", "μm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num).toPlainString());
                item.put("unit", "mm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 10).toPlainString());
                item.put("unit", "cm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 1000).toPlainString());
                item.put("unit", "m");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 1000000).toPlainString());
                item.put("unit", "km");
                listData.add(item);
                break;
            case "cm":
                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 10000).toPlainString());
                item.put("unit", "μm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 10).toPlainString());
                item.put("unit", "mm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num).toPlainString());
                item.put("unit", "cm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 100).toPlainString());
                item.put("unit", "m");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 100000).toPlainString());
                item.put("unit", "km");
                listData.add(item);
                break;
            case "m":
                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 1000000).toPlainString());
                item.put("unit", "μm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 1000).toPlainString());
                item.put("unit", "mm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 100).toPlainString());
                item.put("unit", "cm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num).toPlainString());
                item.put("unit", "m");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num / 1000).toPlainString());
                item.put("unit", "km");
                listData.add(item);
                break;
            case "km":
                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 1000000000).toPlainString());
                item.put("unit", "μm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 1000000).toPlainString());
                item.put("unit", "mm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 100000).toPlainString());
                item.put("unit", "cm");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num * 1000).toPlainString());
                item.put("unit", "m");
                listData.add(item);

                item = new HashMap<>();
                item.put("unit_num", BigDecimal.valueOf(num).toPlainString());
                item.put("unit", "km");
                listData.add(item);
                break;
        }
        return listData;
    }
}