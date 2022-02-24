package com.single.code.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.single.code.annotation.Parameter;
import com.single.code.annotation.SRouter;
import com.single.code.router.api.ParameterManager;
import com.single.code.router.api.Request;
import com.single.code.router.api.ResourceRequest;
import com.single.code.router.api.RouterApi;

@SRouter(path = "/home/HomeActivity")
public class HomeActivity extends AppCompatActivity {

    @Parameter(name = "id")
    String userId;
    @Parameter
    int count;
    @Parameter(name = "/login/LoginDrawableRequest")
    ResourceRequest resourceRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ParameterManager.getManager().loadParameter(this);
        Log.e("houqing","HomeActivity userid="+userId+" count="+count);
        Button btn_main = findViewById(R.id.btn_main);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterApi.getManager().build("/app/MainActivity2")
                        .withInt("count",8)
                        .withString("id","12321341")
                        .navigation(HomeActivity.this);
            }
        });
        ImageView home_iv = findViewById(R.id.home_iv);
        home_iv.setImageDrawable(getDrawable(resourceRequest.getResource(Request.RequestType.DRAWABLE,this,"login_icon")));
    }

}