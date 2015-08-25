package com.example.ricardoviteri.beerconverter;

import java.math.BigInteger;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner toSpinner, sexSpinner;
    String toUnits;
    Button btnConvert, btnAddUnit, btnAlcoholMode;
    ArrayAdapter<CharSequence> unitsAdapter, sexAdapter;
    ArrayList<View> fromViews = new ArrayList<View>();
    String[] mlValues, units;
    Double toMlValue;
    LinearLayout fromLayout;
    TextView txtSex, txtAlcohol;
    boolean isAlcoholMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromLayout = (LinearLayout) findViewById(R.id.from_layout);
        toSpinner = (Spinner) findViewById(R.id.spinner2);
        btnConvert = (Button) findViewById(R.id.button2);
        btnAddUnit = (Button) findViewById(R.id.button3);
        btnAlcoholMode = (Button) findViewById(R.id.button4);
        txtSex = (TextView) findViewById(R.id.textView8);
        txtAlcohol = (TextView) findViewById(R.id.textView9);
        sexSpinner = (Spinner) findViewById(R.id.spinner3);

        units = getResources().getStringArray(R.array.units_array);
        mlValues = getResources().getStringArray(R.array.values_array);

        unitsAdapter = ArrayAdapter.createFromResource(this, R.array.units_array, android.R.layout.simple_spinner_item);
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexAdapter = ArrayAdapter.createFromResource(this, R.array.sex_array, android.R.layout.simple_spinner_item);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        btnConvert.setOnClickListener(this);
        btnAddUnit.setOnClickListener(this);
        btnAlcoholMode.setOnClickListener(this);

        toMlValue = Double.parseDouble(mlValues[0]);
        toUnits = units[0];

        toSpinner.setAdapter(unitsAdapter);
        toSpinner.setOnItemSelectedListener(this);
        sexSpinner.setAdapter(sexAdapter);

        changeStringToInteger();
        initFroms();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(toSpinner.getId() == parent.getId()){
            toMlValue = Double.parseDouble(mlValues[position]);
            toUnits = units[position];
            Log.v("Selected from list!", "To: "+toMlValue);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button2:
                Double newValue = 0.0;
                Double mlPureAlcohol = 0.0;
                String strOldValues = "", strOldUnits = "";

                Intent intent = new Intent(this, ResultActivity.class);
                Bundle mBundle = new Bundle();

                for(int i = 0; i<fromViews.size(); i++){
                    View tempView = fromViews.get(i);
                    Double fromQty;
                    if(((EditText)tempView.findViewById(R.id.editText)).getText().toString().length() == 0){
                        fromQty = 0.0;
                    }else{
                        fromQty = Double.parseDouble(((EditText)tempView.findViewById(R.id.editText)).getText().toString());
                    }
                    Double fromMlValue = Double.parseDouble(mlValues[((Spinner)tempView.findViewById(R.id.spinner)).getSelectedItemPosition()]);

                    newValue += fromQty*fromMlValue;
                    if(isAlcoholMode){
                        Double alcoholPctg;
                        if(((EditText)tempView.findViewById(R.id.editText2)).getText().toString().length() == 0){
                            alcoholPctg = 0.0;
                        }else{
                            alcoholPctg = Double.parseDouble(((EditText)tempView.findViewById(R.id.editText2)).getText().toString())/100;
                        }
                        mlPureAlcohol += fromQty*fromMlValue*alcoholPctg;
                        Log.v("Alcohol percentage!", "%alc. = "+mlPureAlcohol);
                    }
                    strOldUnits = strOldUnits+""+units[((Spinner)tempView.findViewById(R.id.spinner)).getSelectedItemPosition()]+",";
                    strOldValues = strOldValues+""+fromQty.intValue()+",";
                }
                Double unitsForValue = newValue/toMlValue;
                Log.v("Conversion", "Conversion! = "+unitsForValue);

                if(isAlcoholMode){
                    mBundle.putDouble("mlPureAlcohol", mlPureAlcohol);
                    mBundle.putInt("sex", ((Spinner) findViewById(R.id.spinner3)).getSelectedItemPosition());
                }

                mBundle.putBoolean("isAlcoholMode", isAlcoholMode);
                mBundle.putString("oldValue", strOldValues);
                mBundle.putDouble("newValue", unitsForValue);
                mBundle.putString("oldUnits", strOldUnits);
                mBundle.putString("newUnits", toUnits);
                intent.putExtras(mBundle);
                startActivity(intent);
            break;
            case R.id.button3:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View fLayout = inflater.inflate(R.layout.from_block, null);
                Spinner spinner = (Spinner) fLayout.findViewById(R.id.spinner);
                spinner.setAdapter(unitsAdapter);
                spinner.setOnItemSelectedListener(this);
                if(isAlcoholMode){
                    EditText editText = (EditText) fLayout.findViewById(R.id.editText2);
                    editText.setVisibility(View.VISIBLE);
                }
                fromLayout.addView(fLayout, fromViews.size());
                fromViews.add(fLayout);
            break;
            case  R.id.button4:
                changeAlcoholMode();
            break;
        }

    }

    public void initFroms() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View fLayout = inflater.inflate(R.layout.from_block, null);
        Spinner spinner = (Spinner) fLayout.findViewById(R.id.spinner);
        spinner.setAdapter(unitsAdapter);
        spinner.setOnItemSelectedListener(this);
        fromLayout.addView(fLayout, 0);
        fromViews.add(fLayout);
    }

    public void changeAlcoholMode(){
        if(isAlcoholMode){
            txtAlcohol.setVisibility(View.GONE);
            //cosa
            txtSex.setVisibility(View.GONE);
            sexSpinner.setVisibility(View.GONE);
            for(int i = 0; i<fromViews.size(); i++){
                View tempView = fromViews.get(i);
                EditText editText = (EditText) tempView.findViewById(R.id.editText2);
                editText.setVisibility(View.GONE);
            }
        }else{
            txtAlcohol.setVisibility(View.VISIBLE);
            txtSex.setVisibility(View.VISIBLE);
            sexSpinner.setVisibility(View.VISIBLE);
            for(int i = 0; i<fromViews.size(); i++){
                View tempView = fromViews.get(i);
                EditText editText = (EditText) tempView.findViewById(R.id.editText2);
                editText.setVisibility(View.VISIBLE);
            }
        }
        isAlcoholMode = !isAlcoholMode;
    }

    public void changeStringToInteger(){
       String message = "Hodoooooor!!";
        int numMessage;
        try {
            System.out.println("Message: "+message+" is: "+message.getBytes()+" in bytes");
        } catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

    }
}
