package com.alha_app.toolbox;

import android.icu.text.Edits;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;

public class CalcActivity extends AppCompatActivity {
    private boolean isdecimal;
    private String prev;
    private int lpcount; // 左括弧の数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        TextView textView = findViewById(R.id.text);
        TextView textView2 = findViewById(R.id.text2);
        prev = "";
        isdecimal = false;
        lpcount = 0;


        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String str = "";        // ボタンのテキスト用
                String text = textView.getText().toString();

                str = ((Button) v).getText().toString();
                if(!text.equals("")){
                    prev = text.substring(text.length() - 1);
                } else {
                    prev = "";
                }

                if(str.matches("[0-9]")){
                    if(prev.equals(0)){
                        text = text.substring(0, text.length()-1);
                        text = text + str;
                        textView.setText(text);
                    } else if(prev.equals(")")){
                        text = text + "×" + str;
                        textView.setText(text);
                    } else {
                        text = text + str;
                        textView.setText(text);
                    }
                } else if(str.matches("[+ \\- × ÷]")){
                    if(prev.matches("[\\. (]") || prev.equals("")){
                        Toast.makeText(CalcActivity.this, "無効な式です", Toast.LENGTH_SHORT).show();
                    } else if(prev.matches("[+ \\- × ÷]")){
                        text = text.substring(0, text.length()-1);
                        text = text + str;
                        textView.setText(text);
                    } else {
                        text = text + str;
                        textView.setText(text);
                        isdecimal = false;
                    }
                } else if(str.equals("AC")){
                    text = "";
                    textView.setText(text);
                    isdecimal = false;
                    lpcount = 0;
                } else if(str.equals("C")){
                    if(!text.equals("")) {
                        text = text.substring(0, text.length() - 1);
                        textView.setText(text);
                        if(prev.equals(".")){
                            isdecimal = false;
                        } else if(prev.matches("[+ \\- × ÷ %]")){
                            if(text.indexOf(".", serchLeftOpt(text, text.length())) != -1){
                                isdecimal = true;
                            }else {
                                isdecimal = false;
                            }
                        } else if(prev.equals("(")){
                            if(text.indexOf(".", serchLeftOpt(text, text.length())) != -1){
                                isdecimal = true;
                            }else {
                                isdecimal = false;
                            }
                            lpcount--;
                        } else if(prev.equals(")")){
                            if(text.indexOf(".", serchLeftOpt(text, text.length())) != -1){
                                isdecimal = true;
                            }else {
                                isdecimal = false;
                            }
                            lpcount++;
                        }
                    }
                } else if(str.equals("%")){
                    if(prev.matches("[+ \\- × ÷ % \\.]")){
                        Toast.makeText(CalcActivity.this, "無効な式です", Toast.LENGTH_SHORT).show();
                    } else {
                        text = text + str;
                        textView.setText(text);
                    }
                } else if(str.equals(".")){
                    if(prev.matches("[+ \\- × ÷ % \\.]") || prev.equals("") || isdecimal) {
                        Toast.makeText(CalcActivity.this, "無効な式です", Toast.LENGTH_SHORT).show();
                    } else {
                        text = text + str;
                        textView.setText(text);
                        isdecimal = true;
                    }
                } else if(str.equals("( )")){
                    if(prev.equals("") || prev.equals("(")){
                        text = text + "(";
                        lpcount++;
                        textView.setText(text);
                    } else if(prev.matches("[0-9 % )]")){
                        if(lpcount > 0){
                            text = text + ")";
                            lpcount--;
                            textView.setText(text);
                        } else{
                            text = text + "×" + "(";
                            lpcount++;
                            textView.setText(text);
                        }
                    } else if(prev.matches("[+ \\- × ÷]")){
                        text = text + "(";
                        lpcount++;
                        textView.setText(text);
                    }
                } else if(str.equals("=")){
                    if(prev.matches("[0-9 ) %]")){
                        textView.setText(calc(text));
                    }else {
                        Toast.makeText(CalcActivity.this, "無効な式です", Toast.LENGTH_SHORT).show();
                    }
                }

                textView2.setText(calc(text));
            }
        };

        findViewById(R.id.button0).setOnClickListener(btnListener);
        findViewById(R.id.button1).setOnClickListener(btnListener);
        findViewById(R.id.button2).setOnClickListener(btnListener);
        findViewById(R.id.button3).setOnClickListener(btnListener);
        findViewById(R.id.button4).setOnClickListener(btnListener);
        findViewById(R.id.button5).setOnClickListener(btnListener);
        findViewById(R.id.button6).setOnClickListener(btnListener);
        findViewById(R.id.button7).setOnClickListener(btnListener);
        findViewById(R.id.button8).setOnClickListener(btnListener);
        findViewById(R.id.button9).setOnClickListener(btnListener);
        findViewById(R.id.buttonPlus).setOnClickListener(btnListener);
        findViewById(R.id.buttonMinus).setOnClickListener(btnListener);
        findViewById(R.id.buttonMul).setOnClickListener(btnListener);
        findViewById(R.id.buttonDiv).setOnClickListener(btnListener);
        findViewById(R.id.buttonEq).setOnClickListener(btnListener);
        findViewById(R.id.buttonPeriod).setOnClickListener(btnListener);
        findViewById(R.id.buttonparentheses).setOnClickListener(btnListener);
        findViewById(R.id.buttonPercent).setOnClickListener(btnListener);
        findViewById(R.id.buttonAC).setOnClickListener(btnListener);
        findViewById(R.id.buttonClear).setOnClickListener(btnListener);
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

    public String calc(String str){
        String text = str;
        StringBuilder sb = new StringBuilder();
        String result = "";
        int itemp;
        double dtemp, percent;
        int beginindex, endindex, centerindex;

        text = text.replaceAll("×", "*");
        text = text.replaceAll("÷", "/");

        while(countString(text, '(') != countString(text, ')')){
            text = text + ")";
        }

        sb.append(text);

        endindex = sb.indexOf(")");
        while(endindex != -1) {
            beginindex = sb.lastIndexOf("(", endindex);
            sb.replace(beginindex, endindex+1, calc(sb.substring(beginindex+1, endindex)));
            endindex = sb.indexOf(")");
        }

        endindex = sb.indexOf("%");
        while(endindex != -1){
            beginindex = serchLeftOpt(sb.toString(), endindex)-1;
            if(beginindex == -1){
                sb.replace(beginindex+1, endindex+1,
                        String.valueOf(Double.parseDouble(calc(sb.substring(beginindex+1, endindex)))/100));
            } else {
                if(sb.charAt(beginindex) == '+'){
                    centerindex = beginindex;
                    beginindex = serchLeftOpt(sb.toString(), centerindex - 1);
                    dtemp = Double.parseDouble(sb.substring(beginindex, centerindex));
                    percent = Double.parseDouble(sb.substring(centerindex+1, endindex))/100;
                    dtemp = dtemp + dtemp * percent;
                    sb.replace(beginindex, endindex+1, String.valueOf(dtemp));
                }else if(sb.charAt(beginindex) == '-'){
                    centerindex = beginindex;
                    beginindex = serchLeftOpt(sb.toString(), centerindex - 1);
                    dtemp = Double.parseDouble(sb.substring(beginindex, centerindex));
                    percent = Double.parseDouble(sb.substring(centerindex+1, endindex))/100;
                    dtemp = dtemp - dtemp*percent;
                    sb.replace(beginindex, endindex+1, String.valueOf(dtemp));
                }else if(sb.charAt(beginindex) == '*' || sb.charAt(beginindex) == '/'){
                    sb.replace(beginindex, endindex+1,
                            String.valueOf(Double.parseDouble(calc(sb.substring(beginindex+1, endindex)))/100));
                }
            }
            endindex = sb.indexOf("%");
        }
        text = sb.toString();

        try {
            Calculable calc = new ExpressionBuilder(text).build();
            dtemp = calc.calculate();
        } catch (Exception e){
            return "";
        }

        if(dtemp % 1 == 0) {
            itemp = (int) dtemp;
            result = String.valueOf(itemp);
        } else {
            result = String.valueOf(dtemp);
        }

        return result;
    }

    // fromから左を検索 (戻り値は位置+1)
    public int serchLeftOpt(String str, int f){
        String text = str;
        int from = f;
        String [] optlist = {"÷", "+", "-"};
        int result = 0;

        result = text.lastIndexOf("×", from);
        for(int i = 0; i < optlist.length; i++){
            result = Math.max(result, text.lastIndexOf(optlist[i], from));
        }
        result++;

        return result;
    }

    // fromから右を検索
    public int serchRightOpt(String str, int f){
        String text = str;
        int from = f;
        String [] optlist = {"÷", "+", "-"};
        int result = 0;

        result = text.indexOf("×", from);
        if(result == -1) result = 100;
        for(int i = 0; i < optlist.length; i++){
            if(text.indexOf(optlist[i], from) > 0) {
                result = Math.min(result, text.indexOf(optlist[i], from));
            }
        }
        if(result == -1) result = text.length();

        return result;
    }

    public int countString(String str, char ch){
        int count = 0;
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == ch){
                count++;
            }
        }

        return count;
    }
}
