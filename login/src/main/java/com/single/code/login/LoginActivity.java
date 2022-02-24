package com.single.code.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.single.code.annotation.Parameter;
import com.single.code.annotation.SRouter;
import com.single.code.router.api.ParameterManager;
import com.single.code.router.api.RouterApi;

@SRouter(path = "/login/LoginActivity")
public class LoginActivity extends AppCompatActivity {

    @Parameter(name = "id")
    String userId;
    @Parameter
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ParameterManager.getManager().loadParameter(this);
        Log.e("houqing","LoginActivity userid="+userId+" count="+count);
        Button btn_test = findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterApi.getManager().build("/home/HomeActivity")
                        .withInt("count",8)
                        .withString("id","12321341")
                        .navigation(LoginActivity.this);
            }
        });
    }

}