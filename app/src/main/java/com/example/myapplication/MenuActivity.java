package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MenuActivity extends Activity {
    Button btnBluetooth;
    Button btnMap;
    Button btnFinalMain;
    Button btnTest;

    Button sign_out;
    GoogleSignInClient mGoogleSignInClient;
    TextView textView_name;
    TextView textView_mail;
    TextView textView_id;
    ImageView photo;

    //-------------login test----------------
    private RequestQueue requestQueue;
    private final  static String URL="http://163.25.101.33:80/loginapp/google_login.php";
    private StringRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnBluetooth=findViewById(R.id.select_bluetooth);
        btnMap=findViewById(R.id.select_map);
        btnFinalMain=findViewById(R.id.select_final_main);
        btnTest=findViewById(R.id.select_test_menu);
        sign_out = findViewById(R.id.log_out);
        textView_name=findViewById(R.id.textView_name);
        textView_mail=findViewById(R.id.textView_mail);
        textView_id=findViewById(R.id.textView_id);
        photo=findViewById(R.id.photo);
        requestQueue= Volley.newRequestQueue(this);

        //-------------------------------GOOGLE LOGIN 資料測試-------------------------------
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MenuActivity.this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            //loginCheck(personEmail,personId,personName);

            textView_name.setText(personName);
            textView_mail.setText(personEmail);
            textView_id.setText(personId);
            loginCheck(personEmail,personId,personName);
            Glide.with(this).load(personPhoto).into(photo);
        }
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        //-------------------------------GOOGLE LOGIN 資料測試-------------------------------


        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        btnFinalMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this,FinalMainActivity.class);
                startActivity(intent);
            }
        });
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MenuActivity.this,ConvertUtil.class);
                startActivity(intent);
            }
        });



    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MenuActivity.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    private void loginCheck(String personEmail,String personId,String personName){
        Log.i("test login",personEmail+personId+personName);
        final String email,id,name;
        email=personEmail;
        id=personId;
        name=personName;
        request=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "["+response+"]");
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(MenuActivity.this,"SUCCESS!!"+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MenuActivity.this,"SUCCESS!!"+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
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
                Map<String,String> Map=new HashMap<String,String>();
                Map.put("account",email);
                Map.put("password",id);
                Map.put("user_name",name);
                Log.i("test", "account:"+email+"&password:"+id+"       "+name);
               // Log.i("test", "hashmap:"+Map.toString());
                return Map;

            }
        };
        Log.i("test", "request:"+requestQueue.toString());
        requestQueue.add(request);


    }

}
