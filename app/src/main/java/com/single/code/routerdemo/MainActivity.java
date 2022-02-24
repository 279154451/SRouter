package com.single.code.routerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.single.code.annotation.SRouter;
import com.single.code.router.api.RouterApi;

@SRouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterApi.getManager().build("/login/LoginActivity")
                        .withInt("count",8)
                        .withString("id","12321341")
                        .navigation(MainActivity.this);
            }
        });
    }

}