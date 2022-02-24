package com.single.code.routerdemo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.single.code.annotation.Parameter;
import com.single.code.annotation.SRouter;
import com.single.code.router.api.ParameterManager;

@SRouter(path = "/app/MainActivity2")
public class MainActivity2 extends AppCompatActivity {

    @Parameter(name = "id")
   public String userId;
    @Parameter
    public int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ParameterManager.getManager().loadParameter(this);
        Log.e("houqing","MainActivity2 userid="+userId+" count="+count);
    }


}