package com.example.myapplication;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class RegisterActivity extends Activity{
    private Button btnLinkLogin;
    private EditText inputAccount;
    private EditText inputPassword;
    private EditText inputConfirm;
    private Button btnRegister;
    private RequestQueue requestQueue;
    private final  static String URL="http://120.126.16.29:80/loginapp/register.php";
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnLinkLogin=(Button)findViewById(R.id.btnLinkToLoginScreen);
        inputAccount=(EditText)findViewById(R.id.account);
        inputPassword=(EditText)findViewById(R.id.password);
        inputConfirm=(EditText)findViewById(R.id.confirm);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        requestQueue= Volley.newRequestQueue(this);

        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputPassword.getText().toString().equals(inputConfirm.getText().toString())){

                    request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                if(jsonObject.names().get(0).equals("success")){
                                    Toast.makeText(getApplicationContext(),"SUCCESS!!"+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                                    startActivity( new Intent(getApplicationContext(),LoginActivity.class));
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

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> Map = new HashMap<String, String>();
                            Map.put("shelter_name", inputAccount.getText().toString());
                            Map.put("user_password", inputPassword.getText().toString());
                            return Map;
                        }
                    };
                    requestQueue.add(request);
                }else{
                    Toast.makeText(getApplicationContext(),"Wrong password and confirm password!!",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}
