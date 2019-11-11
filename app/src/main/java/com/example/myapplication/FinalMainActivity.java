package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.myapplication.MapsActivity.getPixelsFromDp;

public class FinalMainActivity  extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener ,OnInitListener  {
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    FloatingActionButton fab;

    //--------給個資-----------
    TextView textView_name;
    ImageView photo;
    GoogleSignInClient mGoogleSignInClient;
    View hView;
    String personName;
    Uri personPhoto;



    //--------找既有路線---------
    JSONObject jsonObjectToServer;
    private StringRequest request;
    private final  static String ROUTER_NAME_URL="http://163.25.101.33:80/loginapp/router_name.php";
    private final  static String downLoad_URL="http://163.25.101.33:80/loginapp/router_download.php";
    ArrayList<String> getRouterNameArr;
    private RequestQueue requestQueue;
    ArrayList<ArrayList<ArrayList<LatLng>>> arraySteps;
    ArrayList<Integer> points_state;//0=false 1=true
    ArrayList<LatLng> points;


    //-------recycleview-------
    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;
    private List<RouterData> data_list;
    LatLng routerPoints;
    String tmpTX;

    //-------map------------
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng currentLocation;
    private Marker currentMarker;
    private GoogleMap mMap;
    private float lastRotateDegree;
    int []nowPoint;
    boolean moveTimes;
    MapWrapperLayout mapWrapperLayout;
    ArrayList<MarkerOptions> markerArrayList;
    PolylineOptions lineOptions;
    Polyline polyline;
    String dirPolyline;
    ArrayList<ArrayList<LatLng>> arrayPoints;
    ArrayList<busData> busDataArrayList;
    ArrayList<Bitmap> bmpArray=new ArrayList<>();
    Bitmap bmp;
    String google_maps_key;
    //---------------指南針---------------------
    private SensorManager manager;
    private SensorListener listener = new SensorListener();

    TextToSpeech tts;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_main_test);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigation_view=findViewById(R.id.navigation_view);
        hView =  navigation_view.getHeaderView(0);
        textView_name=hView.findViewById(R.id.txtHeader);
        photo=hView.findViewById(R.id.imgHeader);
        fab=findViewById(R.id.fab);
        //-------------volley request-------------------------
        requestQueue= Volley.newRequestQueue(this);
        //-----------------google測試--------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(FinalMainActivity.this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeechInput();
            }
        });
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.TAIWAN);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE){
                        Toast.makeText(FinalMainActivity.this, "暫時不支援TTS！",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        if (acct != null) {
            personName = acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();
            Log.i("personname",personName);

            //loginCheck(personEmail,personId,personName);
            navigation_view.getHeaderView(0).getId();
            textView_name.setText(personName);
            Glide.with(this).load(personPhoto).into(photo);
        }
        //-----------------google測試 end--------------------------
        dirPolyline=new String();
        points= new ArrayList<>(); // 所有點集合
        points_state=new ArrayList<>();
        markerArrayList=new ArrayList<>();
        lineOptions = new PolylineOptions(); // 多邊形
        arrayPoints = new ArrayList<>();
        arraySteps=new ArrayList<>();
        routerPoints=null;
        moveTimes=false;
        lastRotateDegree=0;
        currentLocation=null;

        ActivityCompat.requestPermissions(FinalMainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        configGoogleApiClient();
        configLocationRequest();

        google_maps_key=getResources().getString(R.string.google_maps_key);


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
        //--------指南針--------------------
        lastRotateDegree=0;
        manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor magnetic = null;
        if (manager != null) {
            magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if (manager != null) {
            manager.registerListener(listener, magnetic, SensorManager.SENSOR_DELAY_GAME);
        }



        getRouter_name();

        //--------info window設定------------
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);







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
                    Intent intent = new Intent();
                    intent.setClass(FinalMainActivity.this,FinalSetRouterActivity.class);
                    startActivity(intent);
                    Toast.makeText(FinalMainActivity.this, "點此新增", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    Toast.makeText(FinalMainActivity.this, menuItem.getTitle()+"", Toast.LENGTH_SHORT).show();

                    downloadToPhone(menuItem.getTitle().toString());
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
    public void downloadToPhone(final String selectTitle){
        jsonObjectToServer=new JSONObject();
        //--------------download start---------------------------
        request = new StringRequest(com.android.volley.Request.Method.POST, downLoad_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.names().get(0).equals("router")){
                        //Toast.makeText(getApplicationContext(),"SUCCESS!!"+jsonObject.getString("Router"),Toast.LENGTH_SHORT).show();
                        android.util.Log.i("getRouter",jsonObject.getJSONArray("router")+"");
                        //download_router.clear();
                        //jsonObject.getJSONArray("Router");
                        JSONArray jsonArray=new JSONArray();
                        jsonArray=jsonObject.getJSONArray("router");
                        arraySteps=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=new JSONObject();
                            jsonObject1=jsonArray.getJSONObject(i);
                            JSONArray jsonArray1=jsonObject1.getJSONArray("legs");
                            ArrayList<ArrayList<LatLng>> tmpArrayList=new ArrayList<>();
                            for(int j=0;j<jsonArray1.length();j++){

                                JSONObject jsonObject2=jsonArray1.getJSONObject(j);
                                JSONArray jsonArray2=jsonObject2.getJSONArray("steps");
                                ArrayList <LatLng> tmpLatlng=new ArrayList<>();
                                for (int k=0;k<jsonArray2.length();k++){

                                    JSONObject jsonObject3=jsonArray2.getJSONObject(k);
                                    LatLng latLng=new LatLng(jsonObject3.getDouble("lat"),jsonObject3.getDouble("lon"));
                                    tmpLatlng.add(latLng);
                                    android.util.Log.i("getRouter latlng",j+" "+latLng);
                                }
                                tmpArrayList.add(tmpLatlng);
                            }
                            arraySteps.add(tmpArrayList);
                        }

                        //get point state
                        String arr=jsonObject.getString("point_state");
                        arr=arr.substring(1,arr.length()-1);
                        String[] resplit = arr.split( ", ");
                        points_state=new ArrayList<>();
                        for ( String s : resplit ) {
                            android.util.Log.i("getRouter state",s);
                            points_state.add( Integer.parseInt(s));

                        }
                        for(int i=0;i<points_state.size();i++){
                            android.util.Log.i("getRouter state test",points_state.get(i)+"");
                        }

                        //get points
                        points=new ArrayList<>();
                        JSONArray jsonArray_points=new JSONArray();
                        jsonArray_points=jsonObject.getJSONArray("points");
                        for(int i=0;i<jsonArray_points.length();i++){
                            JSONObject jsonObject3=jsonArray_points.getJSONObject(i);
                            LatLng latLng=new LatLng(jsonObject3.getDouble("Lat"),jsonObject3.getDouble("Lon"));
                            points.add(latLng);
                        }


                        data_list.clear(); ;


                        JSONArray jsonArray_datalist=new JSONArray();
                        jsonArray_datalist=jsonObject.getJSONArray("data_list");
                        for(int i=0;i<jsonArray_datalist.length();i++){
                            JSONObject jsonObject3=jsonArray_datalist.getJSONObject(i);
                            LatLng latLng=new LatLng(jsonObject3.getDouble("lat"),jsonObject3.getDouble("lon"));
                            String des=jsonObject3.getString("des");

                            RouterData data = new RouterData();
                            data.setDescription(des);
                            data.setLatLng(latLng);

                            data_list.add(data);
                        }
                        routeAdapter.notifyDataSetChanged();
                        drawAll();





                    }else{
                        Toast.makeText(getApplicationContext(),"Error!!",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error!!",Toast.LENGTH_SHORT).show();
                    android.util.Log.e("error",e.toString());
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> Map = new HashMap<String, String>();
                Map.put("ID",personName);
                Map.put("router_name",selectTitle);
                return Map;
            }
        };
        requestQueue.add(request);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FinalMainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, FinalMainActivity.this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Log.i("GPS", "requestCode=" + requestCode);
        switch (requestCode) {
            case 0: { // location
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                } else {
                    // permission denied, boo! Disable the
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, "裝置沒有安裝Google Play服務",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("GPS", "onLocationChanged");
        // 取得目前位置
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocation=latLng;
        Log.v("currentLocation data",currentLocation.toString());
        // mMap.clear();
        // 設定目前位置的marker
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here...").snippet("now").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("standing_up_man",100,100))));
            // points.set(0,latLng);
        } else {
            currentMarker.setPosition(latLng);
        }
        // 移動地圖到目前的位置
        //bluetoothChatFragment.updateMessage("Q");
        if(moveTimes==false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            moveTimes=true;
            //requestBusStation();
        }
        if(points.size()>0){
            MyLatLng tmp = new MyLatLng(points.get(0).longitude,points.get(0).latitude);
            MyLatLng current = new MyLatLng(currentLocation.longitude,currentLocation.latitude);
            getDistance(currentLocation,points.get(0));
         //   Toast.makeText(this,turnTo(current,tmp),Toast.LENGTH_SHORT).show();
        }
       // router_action();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39 + 20));//39+20
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

    }
    //連API
    private synchronized void configGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    // 建立Location request物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊更新的間隔時間為一秒（1000ms）
        locationRequest.setInterval(1000);
        // 設定從API讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        if (mMap == null) { //取得地圖
            ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.fragment_map)).getMapAsync(this);
        }
        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            android.util.Log.i("GPS", "onResume");
            googleApiClient.connect();
        }

        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        manager.registerListener(listener, sensor,
                SensorManager.SENSOR_DELAY_GAME);

        super.onResume();
    }
    @Override
    protected void onPause() {
        manager.unregisterListener(listener);
        super.onPause();
        // 取消位置請求服務
  /*      if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }*/
    }
    @Override
    protected void onStop() {
        super.onStop();
        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    public float getDistance(LatLng point_one,LatLng point_two){
        float[] results=new float[1];
        Location.distanceBetween(point_one.latitude,point_one.longitude,point_two.latitude,point_two.longitude,results);
        android.util.Log.i("距離",results[0]+"");
        return results[0];
    }
    public String turnTo(MyLatLng A, MyLatLng B){
        double turnDrgree=lastRotateDegree-getAngle(A,B);
        if(turnDrgree<15&&turnDrgree>-15){
            return "STRAIGHT";
        }else if(turnDrgree>=15&&turnDrgree<=60){
            return "DEV_LEFT";
        }else if(turnDrgree>60&&turnDrgree<110){
            return "LEFT";
        }else if(turnDrgree>=110&&turnDrgree<=150){
            return "BACK_LEFT";
        }else if(turnDrgree<=-15&&turnDrgree>=-60){
            return "DEV_RIGHT";
        }else if(turnDrgree<-60&&turnDrgree>-110){
            return "RIGHT";
        }else if(turnDrgree<=-110&&turnDrgree>=-150){
            return "BACK_RIGHT";
        }else if(turnDrgree>150||turnDrgree<-150){
            return "BACK";
        }
        return null;
    }
    public  static double getAngle(MyLatLng A, MyLatLng B){
        double dx=(B.m_RadLo-A.m_RadLo)*A.Ed;
        double dy=(B.m_RadLa-A.m_RadLa)*A.Ec;
        double angle=0.0;
        angle=Math.atan(Math.abs(dx/dy))*180./Math.PI;
        double dLo=B.m_Longitude-A.m_Longitude;
        double dLa=B.m_Latitude-A.m_Latitude;
        if(dLo>0&&dLa<=0){
            angle=(90.-angle)+90;
        }
        else if(dLo<=0&&dLa<0){
            angle=angle+180.;
        }else if(dLo<0&&dLa>=0){
            angle= (90.-angle)+270;
        }
        return angle;
    }
    public void router_action(){
        if(nowPoint!=null){
            float distance=getDistance(currentLocation,arraySteps.get(nowPoint[0]).get(nowPoint[1]).get(nowPoint[2]));
            if(distance<5){
                if(arraySteps.get(nowPoint[0]).get(nowPoint[1]).size()==nowPoint[2]+1){
                    nowPoint[2]=0;
                    if(arraySteps.get(nowPoint[0]).size()==nowPoint[1]+1){
                        if(points.size()==nowPoint[0]+1){
                            Toast.makeText(FinalMainActivity.this,"結束導航", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(FinalMainActivity.this,"下個路徑點(大)", Toast.LENGTH_SHORT).show();
                            nowPoint[0]++;
                        }

                    }else{
                        Toast.makeText(FinalMainActivity.this,"下個路徑點(小)", Toast.LENGTH_SHORT).show();
                        nowPoint[1]++;
                    }
                }else {
                    Toast.makeText(FinalMainActivity.this,"下個點", Toast.LENGTH_SHORT).show();
                    nowPoint[2]++;
                }
                Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();
            }else if(distance<10){
                Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();
            }else if(distance<15){
                Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();


            }
        }
    }
    private void drawAll() {
        if (mMap != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                    if(currentLocation!=null){
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("I'm here...").snippet("now").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("standing_up_man",100,100))));
                    }

                    for (int i = 0; i < markerArrayList.size(); i++) {
                        markerArrayList.get(i).title(i + "");
                        mMap.addMarker(markerArrayList.get(i));
                    }
                    lineOptions = new PolylineOptions();
                    lineOptions.add(currentLocation); // 加入所有座標點到多邊形
                    lineOptions.addAll(points); // 加入所有座標點到多邊形
                    lineOptions.width(1);
                    lineOptions.color(Color.RED);
                    if (lineOptions != null) {
                        if (polyline != null) {
                            polyline.remove();
                        }
                        polyline = mMap.addPolyline(lineOptions);
                    } else {
                        android.util.Log.d("onPostExecute", "draw line error!");
                    }
                    if (busDataArrayList != null) {
                        try{
                            for (int i = 0; i < busDataArrayList.size(); i++) {
                                mMap.addMarker(new MarkerOptions()
                                        .title(busDataArrayList.get(i).getName())
                                        .snippet("bus")
                                        .position(busDataArrayList.get(i).getLatLng())
                                        .icon(BitmapDescriptorFactory.fromBitmap(bmpArray.get(i))));
                            }
                        } catch (Exception e) {

                        }
                    }
                    if(routerPoints!=null){
                        mMap.addMarker(new MarkerOptions()
                                .title("路徑播報")
                                .snippet("router")
                                .position(routerPoints)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.to_down)));

                    }

                    if(arraySteps!=null){
                        for(int i=0;i<arraySteps.size();i++){
                            android.util.Log.i("arraytest",i+"");
                            if(points_state.get(i)==1){ //一般路徑
                                for(int j=0;j<arraySteps.get(i).size();j++){
                                    android.util.Log.i("arraytest",arraySteps.get(i).get(j)+"");
                                    drawPath(arraySteps.get(i).get(j));
                                }
                            }else{
                                if(i==0){
                                    ArrayList<LatLng>tmp=new ArrayList<>();
                                    tmp.add(currentLocation);
                                    tmp.add(points.get(i));
                                    drawPath(tmp);
                                }else{
                                    ArrayList<LatLng>tmp=new ArrayList<>();
                                    tmp.add(points.get(i-1));
                                    tmp.add(points.get(i));
                                    drawPath(tmp);
                                }
                            }

                        }
                    }

                }
            });

        }
    }
    private  void drawPath(final ArrayList<LatLng>points){
        mMap.addPolyline(new PolylineOptions().
                addAll(points).
                width(5).
                color(Color.BLUE));

    }

    @Override
    public void onInit(int status) {

    }

    static class MyLatLng {
        final static double Rc=6378137;
        final static double Rj=6356725;
        double m_LoDeg,m_LoMin,m_LoSec;
        double m_LaDeg,m_LaMin,m_LaSec;
        double m_Longitude,m_Latitude;
        double m_RadLo,m_RadLa;
        double Ec;
        double Ed;
        public MyLatLng(double longitude,double latitude){
            m_LoDeg=(int)longitude;
            m_LoMin=(int)((longitude-m_LoDeg)*60);
            m_LoSec=(longitude-m_LoDeg-m_LoMin/60.)*3600;

            m_LaDeg=(int)latitude;
            m_LaMin=(int)((latitude-m_LaDeg)*60);
            m_LaSec=(latitude-m_LaDeg-m_LaMin/60.)*3600;

            m_Longitude=longitude;
            m_Latitude=latitude;
            m_RadLo=longitude*Math.PI/180.;
            m_RadLa=latitude*Math.PI/180.;
            Ec=Rj+(Rc-Rj)*(90.-m_Latitude)/90.;
            Ed=Ec*Math.cos(m_RadLa);
        }
    }
    private  class SensorListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {
            float[] magneticValues = new float[3];
            float[] acceleromterValues = new float[3];
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acceleromterValues = event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticValues = event.values.clone();
            }
            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(R, null, acceleromterValues, magneticValues);
            SensorManager.getOrientation(R, values);
            float roteteDegree = (float) Math.round(event.values[0]);
            //Log.i("12345",roteteDegree+"");
            if (roteteDegree < 0) roteteDegree = 360 + roteteDegree;
            if (roteteDegree < 0 || roteteDegree > 360) return;
            float offset = roteteDegree - lastRotateDegree;
            if (Math.abs(offset) < 0.5f) return;
            //Log.i("1234",roteteDegree+"");
            updateCamera(roteteDegree);
            lastRotateDegree=roteteDegree;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }
    private void updateCamera(float bearing) {
        if(mMap!=null) {
            CameraPosition oldPos = mMap.getCameraPosition();
            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        }
    }

    //---------語音部分------------
    public void getSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(this,result.get(0),Toast.LENGTH_LONG).show();
                    if(result.get(0).equals("東方哈佛")){
                        do_tts("長庚大學");
                    }else{
                        do_tts(result.get(0));
                    }


                }
                break;
        }
    }
    void do_tts(String tts_txt){
        tts.speak(tts_txt, TextToSpeech.QUEUE_ADD, null);
    }
    @Override
    protected void onDestroy() {
        if (tts != null)
            tts.shutdown();
        super.onDestroy();
    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }





}
