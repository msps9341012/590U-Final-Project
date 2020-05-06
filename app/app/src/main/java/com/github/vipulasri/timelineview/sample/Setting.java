package com.github.vipulasri.timelineview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Button button01 = (Button)findViewById(R.id.save);
        button01.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Setting.this, MenuActivity.class);
                Toast.makeText(Setting.this,
                        "Save!", Toast.LENGTH_LONG).show();
                startActivity(intent);
                Setting.this.finish();

            }
        });
    }
}
