package com.timalperamune.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SecondActivity extends AppCompatActivity {
    Spinner spinner;
    public static String TEXT = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SecondActivity.this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);


        Button nextB = (Button) findViewById(R.id.next);



        nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TEXT = spinner.getSelectedItem().toString();
                Intent newIntent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(newIntent);
            }
        });



    }


}