package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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
import com.example.myapplication.bean.CardBean;
import com.example.myapplication.setting.AppClientManager;
import com.example.myapplication.setting.EstimateTime.EsimateTime;
import com.example.myapplication.setting.GetEstimateTime;
import com.example.myapplication.setting.GetRealTimeNearStop;
import com.example.myapplication.setting.GetStopOfRoute;
import com.example.myapplication.setting.RealTimeNearStop.RealTimeNearStop;
import com.example.myapplication.setting.StopOfRoute.StopOfRoute;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import static com.example.myapplication.MapsActivity.getPixelsFromDp;
import static java.lang.Thread.sleep;

public class FinalMainActivity  extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener ,OnInitListener  {
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    FloatingActionButton fab;
    Button testButton;

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
    private final  static String RESERVATION_URL="http://163.25.101.33:80/loginapp/reservation.php";
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
    ArrayList<Integer> state_bus; //0是還沒 1做了
    ArrayList<MarkerOptions> busMarkerArrayList;
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
    boolean isStartingRouter=false;
    int routerPromptNum;
    //---------------指南針---------------------
    private SensorManager manager;
    private SensorListener listener = new SensorListener();
    //------------tts---------------------
    TextToSpeech tts;
    boolean speakingEnd ;
    int tts_state;
    final int DO_SELECT_ROUTER=10;
    final int DO_START_ROUTER=11;
    final int DO_ROUTING=12;
    //----------筱淇----------
    String Data = "";
    String City_a = "Taipei";
    String Query = "";
    String Start = "";
    String Dir = "";
    String Route = "";
    List<String> routeIDList = new ArrayList<String>();
    private LinkedList<HashMap<String, String>> EstimatedTimeList = new LinkedList<>();
    String StopNamehex = "";
    int StopName1Squence;
    String busStopstart;
    List<String> routeOrder = new ArrayList<String>();
    String departure = "";
    String departure_encode="";
    int theFastBusNum = 0;
    String theclostBus = "";
    String[] tokens;
    int nowBusStop;

    boolean isReservation=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_main_test);
        testButton=findViewById(R.id.btn_test);
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

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Route=encode("265區");
                departure="光復橋";
                departure_encode=encode(departure);
                EstimateTime(departure_encode,"1");
            }
        });

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
                    nowPoint=new int[3];
                    nowPoint[0]=0;
                    nowPoint[1]=0;
                    nowPoint[2]=0;

                    //開始導航
                    return true;
                }
                // 略..

            }
        });


        tts_state=DO_SELECT_ROUTER;



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
                        //get busMarker

                        busMarkerArrayList=new ArrayList<>();
                        state_bus=new ArrayList<>();

                        JSONArray jsonArray_bus=new JSONArray();
                        jsonArray_bus=jsonObject.getJSONArray("bus_marker");
                        for(int i=0;i<jsonArray_bus.length();i++){
                            nowBusStop=0;
                            JSONObject jsonObject3=jsonArray_bus.getJSONObject(i);
                            LatLng latLng=new LatLng(jsonObject3.getDouble("Lat"),jsonObject3.getDouble("Lon"));
                            MarkerOptions tmpMarker = new MarkerOptions()
                                    .title(jsonObject3.getString("title"))
                                    .snippet("bus_stop")
                                    .position(latLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            busMarkerArrayList.add(tmpMarker);
                            state_bus.add(0);
                            mMap.addMarker(tmpMarker);
                        }
                        //---------------
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
                        routeAdapter.notifyDataSetChanged();//         find_prompt();
              //          findNearest();
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
       // Toast.makeText(this,"hello",Toast.LENGTH_LONG).show();
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
        if(isStartingRouter){
            router_action();
        }
    /*    if(points.size()>0){
            MyLatLng tmp = new MyLatLng(points.get(0).longitude,points.get(0).latitude);
            MyLatLng current = new MyLatLng(currentLocation.longitude,currentLocation.latitude);
            getDistance(currentLocation,points.get(0));
         //   Toast.makeText(this,turnTo(current,tmp),Toast.LENGTH_SHORT).show();
        }*/
        router_action();

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
            if(points_state.get(nowPoint[0])==1){ //一般路徑
                float distance=getDistance(currentLocation,arraySteps.get(nowPoint[0]).get(nowPoint[1]).get(nowPoint[2]));
                if(distance<30){
                    if(arraySteps.get(nowPoint[0]).get(nowPoint[1]).size()==nowPoint[2]+1){
                        nowPoint[2]=0;
                        if(arraySteps.get(nowPoint[0]).size()==nowPoint[1]+1){
                            if(points.size()==nowPoint[0]+1){
                                do_tts("抵達目的地 導航結束");
                            }else {
                                //  Toast.makeText(FinalMainActivity.this,"下個路徑點(大)", Toast.LENGTH_LONG).show();
                                nowPoint[0]++;
                            }
                        }else{
                            //Toast.makeText(FinalMainActivity.this,"下個路徑點(小)", Toast.LENGTH_LONG).show();
                            nowPoint[1]++;
                            //    drawAll();
                        }
                    }else {
                        //Toast.makeText(FinalMainActivity.this,"下個點", Toast.LENGTH_LONG).show();
                        nowPoint[2]++;
                        // drawAll();
                    }
                    //Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }else if(distance<40){
                    //Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }else if(distance<50){
                    //  Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }else{
                    // Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }
            }else{
                float distance=getDistance(currentLocation,points.get(nowPoint[0]));
                if(distance<30){
                    if(points.size()==nowPoint[0]+1){
                        do_tts("抵達目的地 導航結束");
                    }else {
                        //  Toast.makeText(FinalMainActivity.this,"下個路徑點(大)", Toast.LENGTH_LONG).show();
                        nowPoint[0]++;
                    }
                    //Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }else if(distance<40){
                    //Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }else if(distance<50){
                    //  Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }else{
                    // Toast.makeText(FinalMainActivity.this,"點距"+distance, Toast.LENGTH_LONG).show();
                }

            }
            //如果與路線提示點<30m DO:提示和刪除
            if(data_list.size()>0){
                float promptDistance=getDistance(currentLocation,data_list.get(0).getLatLng());
                if(promptDistance<30){
                    do_tts(data_list.get(0).getDescription());
                    removeItem(0);
                }
            }
            //做預約公車
            if(busMarkerArrayList.size()>0){
                float promptDistance=getDistance(currentLocation,busMarkerArrayList.get(nowBusStop).getPosition());
                if(promptDistance<30){
                    do_tts("請問是否要幫你預約公車");
                    tokens = busMarkerArrayList.get(0).getTitle().split("_");
                    Route=encode(tokens[1]);
                    departure=tokens[0];
                    departure_encode=encode(departure);
                    nowBusStop++;
                    EstimateTime(departure_encode,"1");
                    //do_tts("最快公車"+tokens[1]+"到站時間為"+EstimatedTimeList.get(theFastBusNum).get("time"));
                }
            }

            drawAll();
        }
    }
    private void drawAll() {
        if (mMap != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                    currentMarker=null;


                    for (int i = 0; i < markerArrayList.size(); i++) {
                        markerArrayList.get(i).title(i + "");
                        mMap.addMarker(markerArrayList.get(i));
                    }
                  /*  lineOptions = new PolylineOptions();
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
                    }*/
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
                        if(nowPoint!=null){
                            for(int i=nowPoint[0];i<arraySteps.size();i++){
                                android.util.Log.i("arraytest",i+"");
                                if(points_state.get(i)==1){ //一般路徑
                                    if(nowPoint[0]==i){
                                        for(int j=nowPoint[1];j<arraySteps.get(i).size();j++){
                                            // android.util.Log.i("arraytest",arraySteps.get(i).get(j)+"");
                                            if(nowPoint[1]==j){
                                                ArrayList<LatLng>points=new ArrayList<>();
                                                for(int k=nowPoint[2];k<arraySteps.get(i).get(j).size();k++){
                                                    points.add(arraySteps.get(i).get(j).get(k));
                                                }
                                                drawPath(points);
                                            }else{
                                                drawPath(arraySteps.get(i).get(j));
                                            }
                                        }
                                    }else{
                                        for(int j=0;j<arraySteps.get(i).size();j++){
                                            // android.util.Log.i("arraytest",arraySteps.get(i).get(j)+"");
                                            drawPath(arraySteps.get(i).get(j));
                                        }
                                    }

                                }else{
                                    if(i==0){
                                        ArrayList<LatLng>tmp=new ArrayList<>();
                                        tmp.add(currentLocation);
                                        tmp.add(points.get(i));
                                        drawPath(tmp);
                                    }else if(nowPoint[0]==points.size()-1){
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
                        }else{
                            for(int i=0;i<arraySteps.size();i++){
                                android.util.Log.i("arraytest",i+"");
                                if(points_state.get(i)==1){ //一般路徑
                                    for(int j=0;j<arraySteps.get(i).size();j++){
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
                    if(busMarkerArrayList!=null){
                        for (int i = 0; i < busMarkerArrayList.size(); i++) {
                            mMap.addMarker(busMarkerArrayList.get(i));
                        }
                    }

                    if (nowPoint != null) {
                        if(points_state.get(nowPoint[0])==1) { //一般路徑
                            mMap.addMarker(new MarkerOptions()
                                    .title("路徑播報")
                                    .snippet("router")
                                    .position(arraySteps.get(nowPoint[0]).get(nowPoint[1]).get(nowPoint[2]))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.to_down)));

                            ArrayList<LatLng>tmp=new ArrayList<>();
                            tmp.add(currentLocation);
                            tmp.add(arraySteps.get(nowPoint[0]).get(nowPoint[1]).get(nowPoint[2]));
                            drawPath(tmp);
                        }else{
                            mMap.addMarker(new MarkerOptions()
                                    .title("路徑播報")
                                    .snippet("router")
                                    .position(points.get(nowPoint[0]))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.to_down)));

                            ArrayList<LatLng>tmp=new ArrayList<>();
                            tmp.add(currentLocation);
                            tmp.add(points.get(nowPoint[0]));
                            drawPath(tmp);
                        }









                    }

                    if (currentMarker == null) {
                        currentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("I'm here...").snippet("now").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("standing_up_man",100,100))));
                        // points.set(0,latLng);
                    } else {
                        currentMarker.setPosition(currentLocation);
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
        wait_TTS_End();
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
                    //Toast.makeText(this,result.get(0),Toast.LENGTH_LONG).show();
                    speak_recognize(result.get(0),tts_state);

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
    void speak_recognize(String result,int tts_state)  {
        switch (tts_state){
            case DO_SELECT_ROUTER: //設定路線
                boolean isFindRouter=false;
                for(int i=0;i<getRouterNameArr.size();i++){
                    if(result.contains(getRouterNameArr.get(i))){
                        nowPoint=new int[3];
                        nowPoint[0]=0;
                        nowPoint[1]=0;
                        nowPoint[2]=0;
                        downloadToPhone(getRouterNameArr.get(i));

                        do_tts("正幫你導航到"+getRouterNameArr.get(i));
                        this.tts_state=DO_ROUTING;
                        isFindRouter=true;
                        isStartingRouter=true;
                    }
                }
                if (!isFindRouter){
                    do_tts("辨識不正確 請再說一次");
                    getSpeechInput();
                }
                break;
            case DO_ROUTING:  //導航中
                //do something
                //do_tts("正幫你導航到"+getRouterNameArr.get(i));
                break;
        }

    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
    public  void wait_TTS_End(){
        speakingEnd = tts.isSpeaking();
        do{
            speakingEnd = tts.isSpeaking();
        } while (speakingEnd);
    }
    public void findNearest(){
        float min=999999999;
        int tmpA=0,tmpB=0;
        Log.i("起始點",arraySteps.size()+"size"+arraySteps.get(0).size());
        for(int i=0;i<arraySteps.size();i++){
            for(int j=0;j<arraySteps.get(i).size();j++){
                Log.i("起始點",getDistance(currentLocation,arraySteps.get(i).get(j).get(0))+"");
                if(min>getDistance(currentLocation,arraySteps.get(i).get(j).get(0))){
                    min=getDistance(currentLocation,arraySteps.get(i).get(j).get(0));
                    tmpA=i;
                    tmpB=j;
                }
            }
        }
        nowPoint[0]=tmpA;
        nowPoint[1]=tmpB;
        Log.i("起始點",nowPoint[0]+","+nowPoint[1]);
    }

    public void find_prompt(){
        int minNUM=0;
        float min=99999999;
        for(int i=0;i<data_list.size();i++){
            if(min>getDistance(currentLocation,data_list.get(i).getLatLng())){
                min=getDistance(currentLocation,data_list.get(i).getLatLng());
                minNUM=i;
            }
        }
        Log.i("min",minNUM+"");
        routerPromptNum=minNUM;
        for(int i=0;i<routerPromptNum;i++){
            removeItem(0);

        }

    }

    //recycle adapter func
    public void insertItem(int position, RouterData routerData) {
        data_list.add(position, routerData);
        //  routeAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        data_list.remove(position);
        routeAdapter.notifyItemRemoved(position);
        routeAdapter.notifyItemRangeChanged(position, data_list.size());
    }

    //--------預約公車-----------
    //------------------------------------------------------------------------------
    //填入上車站牌(DespatureStop),填上方向(dir)//dir="0"表示去程 ; dir="1"表示返程
    //找出公車預估到站時間
    //使用前設定 Route -> 上車公車號碼

    private void EstimateTime(final String DespatureStop, final String dir) {//Des : XX站   dir : 方向 0.1
        Log.e("loc", "現在執行的函式是= EstimateTime");
        Data = "EstimatedTimeOfArrival";
        City_a = "Taipei";
        Query = "?$select=EstimateTime&$filter=StopName%2FZh_tw%20eq%20%27" + DespatureStop + "%27%20and%20Direction%20eq%20" + dir + "&$format=JSON";
        Log.e("Query", Query);
        Log.d("Route", "路線:" + Route);

        GetEstimateTime GetEstimateTime = AppClientManager.getClient().create(GetEstimateTime.class);
        GetEstimateTime.GetEstimateTime(Data, City_a, Route, Query).enqueue(new retrofit2.Callback<List<EsimateTime>>() {
            @Override
            public void onResponse(retrofit2.Call<List<EsimateTime>> call, retrofit2.Response<List<EsimateTime>> response) {
                Log.e("OkHttp", "這條線路的公車還有多久到這一站 response = " + response.body().toString());
                List<EsimateTime> list = response.body();
                HashMap<String, String> EstimatedTime = new HashMap<>();

                if (!list.isEmpty()) {
                    for (EsimateTime p : list) {
                        EstimatedTime.put("route", Route);
                        int min;
                        String mins;
                        if (p.getEstimateTime() != null) {
                            min = p.getEstimateTime() / 60;
                            mins = min + "分";
                            Log.e("time", mins);
                            EstimatedTime.put("time", min + "");
                        } else {
                            //Log.e("time","未發車");
                            EstimatedTime.put("time", 0 + "");
                        }
                        EstimatedTimeList.add(EstimatedTime); //key:公車號碼
                    }
                }
                else{
                    Log.e("OkHttp", "u connect");
                }
                theFastBus(dir);
                //StopOfRoute(Route, dir, 2);
                //Resevation(dir);
            }

            @Override
            public void onFailure(retrofit2.Call<List<EsimateTime>> call, Throwable t) {
                Log.e("OkHttp", "目前哪一台公車最快來的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= EstimateTime");

    }

    //------------------------------------------------------------------------------
    //用來找出哪一台公車最快來
    private void theFastBus(final String dir) {
        theFastBusNum = 0;
        int time;
        Log.e("size", EstimatedTimeList.size() + "");
        if (EstimatedTimeList.size() != 0) {

            for (int i = 1; i < EstimatedTimeList.size(); i++) {
                Log.e("EstimatedTimeList", EstimatedTimeList.get(i).get("route"));
                time = Integer.parseInt(EstimatedTimeList.get(i).get("time"));
                if (time < Integer.parseInt(EstimatedTimeList.get(theFastBusNum).get("time"))) {
                    theFastBusNum = i;
                }
            }
            Route = EstimatedTimeList.get(theFastBusNum).get("route");
            Log.e("最快到站的公車是", EstimatedTimeList.get(theFastBusNum).get("route"));
            Log.e("再幾分鐘來", EstimatedTimeList.get(theFastBusNum).get("time"));
            do_tts("最快公車"+tokens[1]+"號到站時間為"+EstimatedTimeList.get(theFastBusNum).get("time")+"分鐘");






            StopOfRoute(Route, dir, 2);//找出StopName1Squence
        }
    }
    //------------------------------------------------------------------------------
    //用來預約公車司機

    private void Reservation(final String dir) {
        Log.e("loc", "現在執行的函式是= RealTimeNearStop");
        Data = "RealTimeNearStop";
        City_a = "Taipei";
        Query = "?$filter=Direction%20eq%20" + dir + "&$orderby=StopSequence%20%20asc&$top=10&$format=JSON";
        Log.e("Query", Query);

        GetRealTimeNearStop GetRealTimeNearStop = AppClientManager.getClient().create(GetRealTimeNearStop.class);
        GetRealTimeNearStop.GetRealTimeNearStop(Data, City_a, Route, Query).enqueue(new retrofit2.Callback<List<RealTimeNearStop>>() {
            @Override
            public void onResponse(retrofit2.Call<List<RealTimeNearStop>> call, retrofit2.Response<List<RealTimeNearStop>> response) {
                Log.e("OkHttp", "RealTimeNearStop response = " + response.body().toString());
                List<RealTimeNearStop> list = response.body();
                theclostBus = "";
                if (!list.isEmpty()) {
                    for (RealTimeNearStop p : list) {
                        if (p.getStopSequence() < StopName1Squence) {
                            theclostBus = p.getPlateNumb();

                        }
                    }
                }
                Log.e("final", theclostBus);

                //--------------VVVVVVVVVVVVVVVVVVV----------------
                //做上傳預約資訊動作 theclostBus->公車車牌
                if(!isReservation){
                    String[] token0 = busMarkerArrayList.get(0).getTitle().split("_");
                    String[] token1 = busMarkerArrayList.get(1).getTitle().split("_");
                    reservationToDatabase(theclostBus,token0[0],token1[0]);
                    isReservation=true;
                }else{
                    do_tts("抵達下車站");
                    isReservation=false;
                }



            }

            @Override
            public void onFailure(retrofit2.Call<List<RealTimeNearStop>> call, Throwable t) {
                Log.e("OkHttp", "目前哪一台公車最快來的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= RealTimeNearStop");
    }
    //------------------------------------------------------------------------------
    //點選公車站中某一路公車，抓出這台公車的站牌順序資料，並且分為往返方向
    //站序資料存在routeOrder
    //這路公車往?方向的站序資料//dir="0"表示去程 ; dir="1"表示返程
    //ex RouteNumb=1211
    //列德func都填0
    // func=1:用來看下車車站中的路線那些有經過上車車站
    // func=2:用來查詢stopsequence
    public void StopOfRoute(final String RouteNumb, final String dir, final int func) {

        routeOrder = new ArrayList<>();
        Log.e("loc", "現在執行的函式是= StopOfRoute");
        Data = "StopOfRoute";
        City_a = "Taipei";
        Query = "?$select=Stops&$top=30&$format=JSON";
        GetStopOfRoute GetStopOfRoute = AppClientManager.getClient().create(GetStopOfRoute.class);
        GetStopOfRoute.GetStopOfRoute(Data, City_a, RouteNumb, Query).enqueue(new retrofit2.Callback<List<StopOfRoute>>() {
            @Override
            public void onResponse(retrofit2.Call<List<StopOfRoute>> call, retrofit2.Response<List<StopOfRoute>> response) {
                Log.e("OkHttp", "車牌順序成功了啦 response = " + response.body().toString());
                List<StopOfRoute> list = response.body();
                for (StopOfRoute p : list) {
                    Log.e("func<!1>列出相同上車站牌的路線:", RouteNumb);
                    if (p.getDirection().toString().equals(dir)) {//確定方向
                        Log.e("func<!2>列出相同上車站牌的路線:", RouteNumb);
                        for (int i = 0; i < p.getStops().size(); i++) {
                            if (func == 1) {
                                Log.e("func<!3>列出相同上車站牌的路線:", RouteNumb);
                                Log.e("func testname ", i + ":" + p.getStops().get(i).getStopName().getZhTw() + ",  " + Start);
                                if (p.getStops().get(i).getStopName().getZhTw().equals(Start)) {
                                    Log.e("func<4>列出相同上車站牌的路線:", RouteNumb);
                                }
                            } else if (func == 2) {
                                //StopName1放上車站牌
                                if (p.getStops().get(i).getStopName().getZhTw().equals(departure)) {
                                    StopName1Squence = p.getStops().get(i).getStopSequence();
                                    Log.e("這臺公車的stop sequence", StopName1Squence + "");

                                    //---------------判斷預約--------------------
                                    Reservation(dir);
                                }
                            } else {
                                routeOrder.add(i, p.getStops().get(i).getStopName().getZhTw());
                                Log.e("列出下車站牌", p.getStops().get(i).getStopName().getZhTw());
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<List<StopOfRoute>> call, Throwable t) {
                Log.e("OkHttp", "車牌順序的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= StopOfRoute");
    }
    //依照交通部轉換格式，把字串轉成16進位
    private String encode(String str) {
        //根據預設編碼獲取位元組陣列
        String hexString = "0123456789ABCDEF";
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //將位元組陣列中每個位元組拆解成2位16進位制整數
        for (int i = 0; i < bytes.length; i++) {
            sb.append("%");
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        Log.e("encode", sb.toString());
        //%E6%8D%B7%E9%81%8B%E8%A5%BF%E9%96%80%E7%AB%99
        return sb.toString();
    }

    //-----------------------------------------------------
    public void reservationToDatabase(final String license,final String start_stop,final String end_stop){
        request=new StringRequest(Request.Method.POST, RESERVATION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "["+response+"]");
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.names().get(0).equals("success")){
                       // Toast.makeText(getApplicationContext(),"SUCCESS!!"+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        do_tts("已幫你預約公車司機");
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
                Map.put("license",license);
                Map.put("start_stop",start_stop);
                Map.put("end_stop",end_stop);
                Log.i("test stop",license+":"+start_stop+":"+end_stop);
                return Map;
            }
        };
        Log.i("test", "request:"+requestQueue.toString());
        requestQueue.add(request);
    }





}
