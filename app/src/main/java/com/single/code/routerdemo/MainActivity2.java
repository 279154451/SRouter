package com.single.code.routerdemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.single.code.annotation.Parameter;
import com.single.code.annotation.SRouter;
import com.single.code.router.api.ParameterManager;
import com.single.code.router.api.Request;
import com.single.code.router.api.ResourceRequest;

@SRouter(path = "/app/MainActivity2")
public class MainActivity2 extends AppCompatActivity {

    @Parameter(name = "id")
   public String userId;
    @Parameter
    public int count;
    @Parameter(name = "/login/LoginDrawableRequest")//这里的name对应路由的Path
    ResourceRequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ParameterManager.getManager().loadParameter(this);
        Log.e("houqing","MainActivity2 userid="+userId+" count="+count);
        ImageView iv_main = findViewById(R.id.iv_main);
        iv_main.setImageDrawable(getDrawable(request.getResource(Request.RequestType.DRAWABLE,this,"login_icon")));
    }


}