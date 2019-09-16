package com.example.myapplication;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity  {


    private Button btnLogin;

    private Button btnLinkToRigister;
    private EditText inputAccount;
    private EditText inputPassword;
    private RequestQueue requestQueue;
    private final  static String URL="http://163.25.101.33:80/loginapp/user_control.php";
    private StringRequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* AppEventsLogger.activateApp(this);
        //init facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        // init LoginManager & CallbackManager
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();*/
/*
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
        });

*/





        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLinkToRigister=(Button)findViewById(R.id.btnLinkToRegisterScreen);
        inputAccount=(EditText)findViewById(R.id.account);
        inputPassword=(EditText)findViewById(R.id.password);
        requestQueue= Volley.newRequestQueue(this);


/*

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        //loginButton.setFragment(this);

        // Callback registration
        /*loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });*//*
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Facebook Login
                loginFB();
            }
        });*/





        btnLinkToRigister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                request=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("tagconvertstr", "["+response+"]");
                            JSONObject jsonObject=new JSONObject(response);

                            if(jsonObject.names().get(0).equals("success")){
                                Toast.makeText(getApplicationContext(),"SUCCESS!!"+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();

                                Intent i=new Intent(getApplicationContext(),MenuActivity.class);
                                i.putExtra("user",inputAccount.getText().toString());

                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"Error!!"+jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("error",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error2",error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> Map=new  HashMap<String,String>();
                        Map.put("shelter_name",inputAccount.getText().toString());
                        Map.put("user_password",inputPassword.getText().toString());
                        Log.i("test", "account:"+inputAccount.getText().toString()+"&password:"+inputPassword.getText().toString());
                        Log.i("test", "hashmap:"+Map.toString());
                        return Map;
                    }
                };
                Log.i("test", "request:"+requestQueue.toString());
                requestQueue.add(request);

            }
        });
    }





}

