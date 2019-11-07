package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.bean.CardBean;
import com.example.myapplication.common.logger.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalMainActivity  extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;

    //--------給個資-----------
    TextView textView_name;
    ImageView photo;
    GoogleSignInClient mGoogleSignInClient;
    View hView;
    String personName;
    Uri personPhoto;



    //--------找既有路線---------
    private StringRequest request;
    private final  static String ROUTER_NAME_URL="http://163.25.101.33:80/loginapp/router_name.php";
    ArrayList<String> getRouterNameArr;
    private RequestQueue requestQueue;

    //-------recycleview-------
    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;
    private List<RouterData> data_list;
    LatLng routerPoints;
    String tmpTX;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_main_test);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigation_view=findViewById(R.id.navigation_view);
        hView =  navigation_view.getHeaderView(0);

        textView_name=hView.findViewById(R.id.txtHeader);
        photo=hView.findViewById(R.id.imgHeader);


        //recycleview


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        data_list=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setHasFixedSize(true);
        routeAdapter=new RouteAdapter(data_list);
        recyclerView.setAdapter(routeAdapter);

        routeAdapter.setOnItemClickListener(new RouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                android.util.Log.i("pikachuuuu!!",position+"");
                routerPoints=data_list.get(position).getLatLng();
                //Toast.makeText(MapsActivity.this,data_list.get(position).getLatLng()+"",Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //recycleview


        //navigation_view.getMenu().add(0,1,0,"test1"); //order 為順序weight 預設0 相同照順序擺放
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(FinalMainActivity.this);
        if (acct != null) {
            personName = acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();
            Log.i("personname",personName);

            //loginCheck(personEmail,personId,personName);
            navigation_view.getHeaderView(0).getId();
            textView_name.setText(personName);
            Glide.with(this).load(personPhoto).into(photo);
        }

        Log.i("personname",personName+"123123");

        requestQueue= Volley.newRequestQueue(this);
        getRouter_name();







        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.END);

                // 取得選項id
                int id = menuItem.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.add) {
                    // 按下「首頁」要做的事
                    //getRouter_name();
                    Toast.makeText(FinalMainActivity.this, "點此新增", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    Toast.makeText(FinalMainActivity.this, menuItem.getTitle()+"", Toast.LENGTH_SHORT).show();
                    return true;
                }
                // 略..

            }
        });






    }


    private void setRouterName(){
        if(getRouterNameArr!=null){
            for (int i = 0; i < getRouterNameArr.size(); i++) {
                navigation_view.getMenu().add(0,i,0,getRouterNameArr.get(i));
            }
        }
    }
    private void getRouter_name() {
        request=new StringRequest(com.android.volley.Request.Method.POST, ROUTER_NAME_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    android.util.Log.i("tagconvertstr", "["+response+"]");
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getJSONArray("router")!=null){
                        android.util.Log.i("router name test",jsonObject.getJSONArray("router").toString());
                        JSONArray routerArr=new JSONArray();
                        routerArr=jsonObject.getJSONArray("router");
                        getRouterNameArr=new ArrayList<>();
                        for(int i=0;i<routerArr.length();i++){
                            getRouterNameArr.add(routerArr.get(i).toString().replace(".txt",""));
                        }
                        android.util.Log.i("router name test2",getRouterNameArr+"");
                        setRouterName();
                    }else{
                        Toast.makeText(getApplicationContext(),"NULL!!",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    android.util.Log.e("error",e.toString());
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.e("error2",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> Map=new  HashMap<String,String>();
                Map.put("ID",personName);
                return Map;
            }
        };
        android.util.Log.i("test", "request:"+requestQueue.toString());
        requestQueue.add(request);

    }
//
//    public void downloadToPhone(){
//        jsonObjectToServer=new JSONObject();
//       /* try{
//            jsonObjectToServer.put("id",ID);
//        } catch (Exception e) {
//            Log.e("error on upload ID",e.toString());
//        }
//        Log.i("check upload messenger",jsonObjectToServer.toString());*/
//
//
//        //--------------download start---------------------------
//        request = new StringRequest(com.android.volley.Request.Method.POST, downLoad_URL, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//                    if(jsonObject.names().get(0).equals("router")){
//                        //Toast.makeText(getApplicationContext(),"SUCCESS!!"+jsonObject.getString("Router"),Toast.LENGTH_SHORT).show();
//                        android.util.Log.i("getRouter",jsonObject.getJSONArray("router")+"");
//                        //download_router.clear();
//                        //jsonObject.getJSONArray("Router");
//                        JSONArray jsonArray=new JSONArray();
//                        jsonArray=jsonObject.getJSONArray("router");
//                        arraySteps=new ArrayList<>();
//                        for(int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject1=new JSONObject();
//                            jsonObject1=jsonArray.getJSONObject(i);
//                            JSONArray jsonArray1=jsonObject1.getJSONArray("legs");
//                            ArrayList<ArrayList<LatLng>> tmpArrayList=new ArrayList<>();
//                            for(int j=0;j<jsonArray1.length();j++){
//
//                                JSONObject jsonObject2=jsonArray1.getJSONObject(j);
//                                JSONArray jsonArray2=jsonObject2.getJSONArray("steps");
//                                ArrayList <LatLng> tmpLatlng=new ArrayList<>();
//                                for (int k=0;k<jsonArray2.length();k++){
//
//                                    JSONObject jsonObject3=jsonArray2.getJSONObject(k);
//                                    LatLng latLng=new LatLng(jsonObject3.getDouble("lat"),jsonObject3.getDouble("lon"));
//                                    tmpLatlng.add(latLng);
//                                    android.util.Log.i("getRouter latlng",j+" "+latLng);
//                                }
//                                tmpArrayList.add(tmpLatlng);
//                            }
//                            arraySteps.add(tmpArrayList);
//                        }
//
//                        //get point state
//                        String arr=jsonObject.getString("point_state");
//                        arr=arr.substring(1,arr.length()-1);
//                        String[] resplit = arr.split( ", ");
//                        points_state=new ArrayList<>();
//                        for ( String s : resplit ) {
//                            android.util.Log.i("getRouter state",s);
//                            points_state.add( Integer.parseInt(s));
//
//                        }
//                        for(int i=0;i<points_state.size();i++){
//                            android.util.Log.i("getRouter state test",points_state.get(i)+"");
//                        }
//
//                        //get points
//                        points=new ArrayList<>();
//                        JSONArray jsonArray_points=new JSONArray();
//                        jsonArray_points=jsonObject.getJSONArray("points");
//                        for(int i=0;i<jsonArray_points.length();i++){
//                            JSONObject jsonObject3=jsonArray_points.getJSONObject(i);
//                            LatLng latLng=new LatLng(jsonObject3.getDouble("Lat"),jsonObject3.getDouble("Lon"));
//                            points.add(latLng);
//                        }
//
//
//                        data_list.clear(); ;
//
//
//                        JSONArray jsonArray_datalist=new JSONArray();
//                        jsonArray_datalist=jsonObject.getJSONArray("data_list");
//                        for(int i=0;i<jsonArray_datalist.length();i++){
//                            JSONObject jsonObject3=jsonArray_datalist.getJSONObject(i);
//                            LatLng latLng=new LatLng(jsonObject3.getDouble("lat"),jsonObject3.getDouble("lon"));
//                            String des=jsonObject3.getString("des");
//
//                            RouterData data = new RouterData();
//                            data.setDescription(des);
//                            data.setLatLng(latLng);
//
//                            data_list.add(data);
//                        }
//                        routeAdapter.notifyDataSetChanged();
//
//
//
//
//
//                    }else{
//                        Toast.makeText(getApplicationContext(),"Error!!",Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    Toast.makeText(getApplicationContext(),"Error!!",Toast.LENGTH_SHORT).show();
//                    android.util.Log.e("error",e.toString());
//                    e.printStackTrace();
//                }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> Map = new HashMap<String, String>();
//                Map.put("ID",personName);
//                Map.put("router_name",btn_CustomOptions.getText().toString());
//                return Map;
//            }
//        };
//        requestQueue.add(request);
//    }


}
