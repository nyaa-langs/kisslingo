package com.minapikke.kisslingo;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        registerButtons();
    }

    private void registerButtons(){
        findViewById(R.id.btn_Study).setOnClickListener((View)-> {
            GlobalApplication.LoadActivity(MainActivity.class);
        });
    }
}
