package com.alha_app.toolbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] tools = getResources().getStringArray(R.array.tools);
        int [] images = {R.drawable.calculator};

        ArrayList<Map<String, Object>> listData = new ArrayList<>();
        for (int i=0; i < tools.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", tools[i]);
            item.put("image", images[i]);
            listData.add(item);
        }

        // ListViewにデータをセットする
        ListView list = findViewById(R.id.list);
        list.setAdapter(new SimpleAdapter(
                this,
                listData,
                R.layout.list_item,
                new String[] {"name", "image"},
                new int[] {R.id.name, R.id.image}
        ));

        // クリックイベント
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(getApplication(), CalcActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}