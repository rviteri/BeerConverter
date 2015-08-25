package com.example.ricardoviteri.beerconverter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by ricardoviteri on 3/11/15.
 */
public class ResultActivity extends Activity implements View.OnClickListener{

    TextView resNumber, resUnits, oldUnitsMsg, txtUnitsPureAlcohol, txtRecom;
    LinearLayout alcModeLayout;
    double newValue, unitsOfAlcohol, mlPerUnit = 10.0;
    String oldUnits, newUnits, oldValues;
    String[] arrValues, arrUnits, arrRecom;
    Button btnBack;
    boolean isAlcoholMode = false;
    int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
        isAlcoholMode = getIntent().getExtras().getBoolean("isAlcoholMode");
        oldValues = getIntent().getExtras().getString("oldValue");
        newValue = getIntent().getExtras().getDouble("newValue");
        oldUnits = getIntent().getExtras().getString("oldUnits");
        newUnits = getIntent().getExtras().getString("newUnits");

        arrValues = oldValues.split(",");
        arrUnits = oldUnits.split(",");

        resNumber = (TextView) findViewById(R.id.textView5);
        resUnits = (TextView) findViewById(R.id.textView6);
        oldUnitsMsg = (TextView) findViewById(R.id.textView7);
        txtUnitsPureAlcohol = (TextView) findViewById(R.id.textView10);
        txtRecom = (TextView) findViewById(R.id.textView12);
        btnBack = (Button) findViewById(R.id.button);
        alcModeLayout = (LinearLayout) findViewById(R.id.linearLayout2);

        btnBack.setOnClickListener(this);

        resNumber.setText(""+String.format("%.2f", newValue));
        resUnits.setText(""+newUnits);

        if(isAlcoholMode){

            alcModeLayout.setVisibility(View.VISIBLE);
//
            unitsOfAlcohol = (getIntent().getExtras().getDouble("mlPureAlcohol"))/mlPerUnit;
            sex = getIntent().getExtras().getInt("sex");

            if(sex == 0){
                arrRecom = getResources().getStringArray(R.array.male_recommendations);
            }else{
                arrRecom = getResources().getStringArray(R.array.female_recommendations);
            }

            txtUnitsPureAlcohol.setText(""+String.format("%.1f", unitsOfAlcohol));

            txtRecom.setText(arrRecom[(int)(unitsOfAlcohol > 7.0 ? 6.0 : unitsOfAlcohol)]);
        }


        String oldUnitsStr = "Converted from:\n";

        for(int i = 0; i<arrValues.length; i++){
            oldUnitsStr = oldUnitsStr+"- "+arrValues[i]+"    "+arrUnits[i]+"\n";
        }

        oldUnitsMsg.setText(oldUnitsStr);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
