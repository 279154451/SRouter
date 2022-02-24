# SRouter
手撸组件化框架
使用规则：
1、在各自组件中依赖注解和注解处理器模块
  implementation project(path: ':router-annotation')
  implementation project(path: ':router-api')
  annotationProcessor project(path: ':router-compiler') //依赖注解处理器才能让注解处理器工作
  并在组件gradle中添加如下代码
      defaultConfig {
       .......
        javaCompileOptions{
            annotationProcessorOptions{//传递参数给注解处理器
                arguments=[moduleName:project.getName()]
            }
        }
    }
2、组件Activity之间跳转
@SRouter(path = "/login/LoginActivity")//通过注解定义路由Path
public class LoginActivity extends AppCompatActivity {

    @Parameter(name = "id")
    String userId;
    @Parameter
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ParameterManager.getManager().loadParameter(this);//配合注解Parameter，参数自动绑定框架
        Button btn_test = findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterApi.getManager().build("/home/HomeActivity")//通过路由框架进行组件间Activity跳转
                        .withInt("count",8)
                        .withString("id","12321341")//Activity之间传递参数
                        .navigation(LoginActivity.this);
            }
        });
    }
}
3、跨组获取资源
  @SRouter(path = "/home/HomeActivity")
public class HomeActivity extends AppCompatActivity {

    @Parameter(name = "id")
    String userId;
    @Parameter
    int count;
    @Parameter(name = "/login/LoginDrawableRequest")//name为资源提供者路由
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
资源提供者实现
@SRouter(path = "/login/LoginDrawableRequest")
public class LoginDrawableRequest implements ResourceRequest {
    @Override
    public int getResource(RequestType type, Context context,String sourceName) {
        switch (type){
            case DRAWABLE:
                int drawableId = context.getResources().getIdentifier(sourceName, "drawable", context.getPackageName());
                return drawableId;
        }
        return 0;
    }
}
