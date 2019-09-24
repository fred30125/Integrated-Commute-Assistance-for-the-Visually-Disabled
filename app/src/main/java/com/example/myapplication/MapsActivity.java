package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;


import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.bean.CardBean;
import com.example.myapplication.bean.JsonBean;
import com.example.myapplication.common.logger.LogWrapper;
import com.example.myapplication.common.logger.MessageOnlyLogFilter;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myapplication.Constants.BUS_URL;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener ,BluetoothChatFragment.OnMyListener{

    private GoogleMap mMap;
    private int test=0;
    private Button setMarker,btnRouter,btnUpload,btn_CustomOptions,downloadRouter;
    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    // 取得裝置目前最新的位置
    private LatLng currentLocation;
    // 目前位置對應的marker
    private Marker currentMarker;
    // 畫線
    ArrayList<LatLng> points;
    ArrayList<Integer> points_state;//0=false 1=true
    ArrayList<ArrayList<LatLng>> arrayPoints;
    ArrayList<ArrayList<ArrayList<LatLng>>> arraySteps;

    PolylineOptions lineOptions;
    Polyline polyline;
    String dirPolyline;
    private BluetoothChatFragment bluetoothChatFragment;
    Boolean setMarkerStatus=false;
    int markerTotal;

    ArrayList<MarkerOptions> markerArrayList;
    boolean moveTimes;
    ArrayList<busData> busDataArrayList;
    ArrayList<Bitmap> bmpArray=new ArrayList<>();
    Bitmap bmp;
    String google_maps_key;
    MapWrapperLayout mapWrapperLayout;
    //---------------指南針---------------------
    private SensorManager manager;
    private SensorListener listener = new SensorListener();
    private float lastRotateDegree;
    private long lastUpdatetime;
    //--------------info window test--------------
    private Button infoButton1,infoButton2,infoButton3;
    private TextView routerID;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private ViewGroup infoWindow;
    //------------direction---------------
    int []nowPoint;

    //------------upload to server --> made by jsonobject--------------
    JSONObject jsonObjectToServer;
    private RequestQueue requestQueue;
    private StringRequest request;
    ArrayList<ArrayList<ArrayList<LatLng>>> download_router;

    //------------google 會員資料-----------------
    GoogleSignInClient mGoogleSignInClient;
    String personName;
    String personGivenName;
    String personFamilyName;
    String personEmail;
    String personId;
    private final  static String URL="http://163.25.101.33:80/loginapp/upload_router.php";
    private final  static String downLoad_URL="http://163.25.101.33:80/loginapp/router_download.php";
    //------------picker view-------------------
    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private static boolean isLoaded = false;
    private ArrayList<CardBean> cardItem = new ArrayList<>();
    private OptionsPickerView pvCustomOptions;

    //-----------download----------
    JsonObject objFile;
    JsonArray arrFile;
    String strFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentLocation=null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //-------------volley request-------------------------
        requestQueue= Volley.newRequestQueue(this);


        //-----------------google測試--------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MapsActivity.this);
        if (acct != null) {
             personName = acct.getDisplayName();
             personGivenName = acct.getGivenName();
             personFamilyName = acct.getFamilyName();
             personEmail = acct.getEmail();
             personId = acct.getId();
        }
        //-----------------google測試 end--------------------------
        dirPolyline=new String();
        points= new ArrayList<>(); // 所有點集合
        points_state=new ArrayList<>();
        markerArrayList=new ArrayList<>();
        lineOptions = new PolylineOptions(); // 多邊形
        arrayPoints = new ArrayList<>();
        arraySteps=new ArrayList<>();


        bluetoothChatFragment=new BluetoothChatFragment();
        ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        configGoogleApiClient();
        configLocationRequest();
        getSupportFragmentManager().beginTransaction()
        .replace(R.id.sample_content_fragment, bluetoothChatFragment)
        .commit();
        google_maps_key=getResources().getString(R.string.google_maps_key);
        setMarker=findViewById(R.id.setMarker);
        btnRouter=findViewById(R.id.getRouter);
        btnUpload=findViewById(R.id.btnUpload);
        downloadRouter=findViewById(R.id.downloadRouter);
        btn_CustomOptions=findViewById(R.id.btn_CustomOptions);


//-----------picker view
        getCardData();
        initCustomOptionPicker();
        btn_CustomOptions.setOnClickListener(new View.OnClickListener() { //設定and選擇路線
            @Override
            public void onClick(View v) {
                pvCustomOptions.show();
            }
        });
//----------end


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToServer(arraySteps,personName,personEmail);
            }
        });
        btnRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(points!=null){
                    nowPoint=new int[3];
                    nowPoint[0]=0;
                    nowPoint[1]=0;
                    nowPoint[2]=0;
                }
            }
        });
        setMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setMarkerStatus==false){
                    setMarkerStatus=true;
                    setMarker.setText("set:ON");
                }else{
                    setMarkerStatus=false;
                    setMarker.setText("set:OFF");
                }
            }
        });
        downloadRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadToPhone();
            }
        });

        markerTotal=0;
        moveTimes=false;
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
        //--------info window設定------------
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.custom_infowindow, null);
        this.infoButton1 = (Button)infoWindow.findViewById(R.id.btnOne);
        this.infoButton2 = (Button)infoWindow.findViewById(R.id.btnTwo);
        this.infoButton3 = (Button)infoWindow.findViewById(R.id.btnThree);
        this.routerID=infoWindow.findViewById(R.id.RouterID);
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton1, getResources().getDrawable(R.drawable.btn_bg), getResources().getDrawable(R.drawable.btn_bg)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(MapsActivity.this, routerID.getText(), Toast.LENGTH_SHORT).show();
                // Here we can perform some action triggered after clicking the button
                //Toast.makeText(MapsActivity.this, "click on button 1", Toast.LENGTH_SHORT).show();
                if(routerID.getText().equals("0")){
                    Toast.makeText(MapsActivity.this, "沒辦法往前", Toast.LENGTH_SHORT).show();
                }else if(markerTotal<2){
                    Toast.makeText(MapsActivity.this, "沒辦法往前", Toast.LENGTH_SHORT).show();
                }else{
                    int ID=Integer.parseInt(routerID.getText().toString());
                    LatLng tmp;
                    tmp=points.get(ID);
                    points.set(ID,points.get(ID-1));
                    points.set(ID-1,tmp);

                    tmp= markerArrayList.get(ID).getPosition();
                    markerArrayList.get(ID).position(markerArrayList.get(ID-1).getPosition());
                    markerArrayList.get(ID-1).position(tmp);
                    getDirection(currentLocation,points.get(points.size()-1));
                }
            }
        };
        this.infoButton1.setOnTouchListener(infoButtonListener);

        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton2, getResources().getDrawable(R.drawable.btn_bg),getResources().getDrawable(R.drawable.btn_bg)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(MapsActivity.this, routerID.getText(), Toast.LENGTH_SHORT).show();
                if(routerID.getText().equals(markerArrayList.size()-1+"")){
                    Toast.makeText(MapsActivity.this, "沒辦法往後", Toast.LENGTH_SHORT).show();
                }else if(markerTotal<2){
                    Toast.makeText(MapsActivity.this, "沒辦法往後", Toast.LENGTH_SHORT).show();
                }else{
                    int ID=Integer.parseInt(routerID.getText().toString());
                    LatLng tmp;
                    tmp=points.get(ID);
                    points.set(ID,points.get(ID+1));
                    points.set(ID+1,tmp);

                    tmp= markerArrayList.get(ID).getPosition();
                    markerArrayList.get(ID).position(markerArrayList.get(ID+1).getPosition());
                    markerArrayList.get(ID+1).position(tmp);
//-----------------draw
                    getDirection(currentLocation,points.get(points.size()-1));
                }
        }
        };
        infoButton2.setOnTouchListener(infoButtonListener);


        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton3, getResources().getDrawable(R.drawable.btn_bg),getResources().getDrawable(R.drawable.btn_bg)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                int ID=Integer.parseInt(routerID.getText().toString());
                if(points_state.get(ID)==0){
                    points_state.set(ID,1);
                    getDirection(currentLocation,points.get(points.size()-1));
                }else{
                    points_state.set(ID,0);
                    drawAll();
                }

            }
        };
        infoButton3.setOnTouchListener(infoButtonListener);



        //picker view




    }
    //地圖creat好的時候執行
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39 + 20));//39+20
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                if(marker.getSnippet().equals("bus")){
                    if (isLoaded) {
                        showPickerView();
                    } else {
                        Toast.makeText(MapsActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
                    }
                    return null;
                }else if(marker.getSnippet().equals("now")){
                    return null;
                }else {
                    // Setting up the infoWindow with current's marker info
                    infoButtonListener.setMarker(marker);
                    // We must call this to set the current marker and infoWindow references
                    // to the MapWrapperLayout
                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                    routerID.setText(marker.getTitle());
                    Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                    return infoWindow;
                }
            }
        });
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentLocation != null) {
                    Toast.makeText(MapsActivity.this, latLng.latitude + "," + latLng.longitude, Toast.LENGTH_LONG).show();
                    if(setMarkerStatus) {
                        points.add(latLng);
                        points_state.add(1);

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

                            MarkerOptions tmpMarker=new MarkerOptions()
                                .title(markerTotal+"")
                                .snippet("router")
                                .position(latLng)
                                .draggable(true);
                            mMap.addMarker(tmpMarker);
                            markerArrayList.add(tmpMarker);
                            markerTotal++;
                            //mMap.addPolyline(lineOptions); // 畫出多邊形
                        } else {
                            Log.d("onPostExecute", "draw line error!");
                        }
                        getDirection(currentLocation,points.get(points.size()-1));
                    }
                }
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {   //拖曳內容設定
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                points.set(Integer.parseInt(marker.getTitle()),marker.getPosition());
                markerArrayList.get(Integer.parseInt(marker.getTitle())).position(marker.getPosition());
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
                    Log.d("onPostExecute", "draw line error!");
                }
                if(points_state.get(Integer.parseInt(marker.getTitle()))==0){
                    drawAll();
                }else{
                    getDirection(currentLocation,points.get(points.size()-1));
                }

            }
        });

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
        locationRequest.setInterval(500);
        // 設定從API讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(500);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MapsActivity.this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("GPS", "requestCode=" + requestCode);
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
       // mMap.clear();
        // 設定目前位置的marker
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here...").snippet("now"));
           // points.set(0,latLng);
        } else {
            currentMarker.setPosition(latLng);
        }
        // 移動地圖到目前的位置
        //bluetoothChatFragment.updateMessage("Q");
        if(moveTimes==false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            moveTimes=true;
            requestBusStation();
        }


        if(points.size()>0){
            MyLatLng tmp = new MyLatLng(points.get(0).longitude,points.get(0).latitude);
            MyLatLng current = new MyLatLng(currentLocation.longitude,currentLocation.latitude);
            getDistance(currentLocation,points.get(0));
            Toast.makeText(this,turnTo(current,tmp),Toast.LENGTH_SHORT).show();
        }
        router_action();

    }
    @Override
    protected void onResume() {
        if (mMap == null) { //取得地圖
            ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.map)).getMapAsync(this);
        }
        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            Log.i("GPS", "onResume");
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
    //兩個fragment傳訊息
    @Override
    public void onInputSent(CharSequence input) {
        //bluetoothChatFragment.updateMessage("123");
    }
    public void requestBusStation(){
        busDataArrayList=new ArrayList<>();
        StringBuilder busStationURL=new StringBuilder(BUS_URL);
        busStationURL.append("location="+currentLocation.latitude+","+currentLocation.longitude );
        busStationURL.append("&radius=5000");
        busStationURL.append("&types=bus_station");
        busStationURL.append("&language=zh-TW");
        busStationURL.append("&key="+google_maps_key);
        Log.i("bus url",busStationURL.toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(busStationURL.toString())
                .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    busData busData;
                    JSONObject busObject = new JSONObject(response.body().string());
                    JSONArray busArray=new JSONArray(busObject.getString("results"));
                    String tmpName;
                    URL tmpIcon;
                    LatLng tmpLatlng;
                    busDataArrayList=new ArrayList<>();
                    bmpArray=new ArrayList<>();
                    for(int i = 0;i<busArray.length();i++){
                        JSONObject object=new JSONObject(busArray.get(i).toString());
                        //Log.i("results name", object.getString("name"));
                        tmpName=object.getString("name");
                        tmpIcon=new URL(object.getString("icon"));
                        JSONObject objectGeomestry=new JSONObject(object.getString("geometry"));
                        JSONObject objectLocation=new JSONObject(objectGeomestry.getString("location"));
                        tmpLatlng=new LatLng(objectLocation.getDouble("lat"),objectLocation.getDouble("lng"));
                        busData=new busData(tmpLatlng,tmpName,tmpIcon);
                        busDataArrayList.add(busData);
                    }
                    for(int i = 0;i<busDataArrayList.size();i++){
                        Log.i("results name",i+":"+busDataArrayList.get(i).getName());
                        Log.i("results location",i+":"+busDataArrayList.get(i).getLatLng().latitude+"&"+busDataArrayList.get(i).getLatLng().longitude);
                        Log.i("results icon",i+":"+busDataArrayList.get(i).getIcon().toString());
                        URL url=busDataArrayList.get(i).getIcon();
                        try {
                            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            bmpArray.add(bmp);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("results image","image lose:"+i);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            for(int i = 0;i<busDataArrayList.size();i++){
                                    mMap.addMarker(new MarkerOptions()
                                            .title(busDataArrayList.get(i).getName())
                                            .snippet("bus")
                                            .position(busDataArrayList.get(i).getLatLng())
                                            .icon(BitmapDescriptorFactory.fromBitmap(bmpArray.get(i))));
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("results name","fail");
                }
            }
        });
    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
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
            Log.i("12345",roteteDegree+"");
            if (roteteDegree < 0) roteteDegree = 360 + roteteDegree;
            if (roteteDegree < 0 || roteteDegree > 360) return;
            float offset = roteteDegree - lastRotateDegree;
            if (Math.abs(offset) < 0.5f) return;
            Log.i("1234",roteteDegree+"");
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
    private String getDirectionsUrl(LatLng origin,LatLng dest){
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        for(int i=0;i<points.size()-1;i++){
            LatLng point  = (LatLng) points.get(i);
            if(i==0)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&language=zh-TW&sensor=true&mode=walking&key="+google_maps_key;
        return url;
    }
    private void getDirection(LatLng origin,LatLng dest){
        String mapAPI = getDirectionsUrl(origin,dest);
        String url = MessageFormat.format(mapAPI, origin, dest);
        Log.i("direction test url",url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.i("direction test -1",jsonObject.toString());
                    JSONArray routeObject = jsonObject.getJSONArray("routes");
                    Log.i("direction test -2",routeObject.toString());

                    objFile=new JsonObject();
                    arrFile=new JsonArray();


                    JSONArray insObject = routeObject.getJSONObject(0).getJSONArray("legs");
                    Log.i("html測試",insObject.length()+"");
                    arraySteps=new ArrayList<>();

                    for(int i=0;i<insObject.length();i++){
                        JsonObject jsonObject3=new JsonObject();
                        JsonArray arrUpload1 = new JsonArray();
                        JSONArray insArray;
                        insArray=insObject.getJSONObject(i).getJSONArray("steps");
                        arrayPoints=new ArrayList<>();
                        for(int j=0;j<insArray.length();j++){
                            JsonObject jsonObject2=new JsonObject();
                            String html_ins=new String();
                            html_ins=insArray.getJSONObject(j).getString("html_instructions");
                            Log.i("html測試", Html.fromHtml(html_ins).toString());
                            JSONObject tmp;
                            tmp=insArray.getJSONObject(j);
                            Log.i("html測試123",tmp.getJSONObject("polyline").getString("points"));
                            arrayPoints.add(decodePoly(tmp.getJSONObject("polyline").getString("points")));

                            JsonArray arrfile=new JsonArray();

                            for(int k=0;k<arrayPoints.get(j).size();k++){
                                JsonObject jsonObject1=new JsonObject();
                                double latitude = arrayPoints.get(j).get(k).latitude;
                                double longitude = arrayPoints.get(j).get(k).longitude;
                                jsonObject1.addProperty("lat",latitude);
                                jsonObject1.addProperty("lon",longitude);
                                arrfile.add(jsonObject1);
                            }
                            jsonObject2.add("steps",arrfile);
                            arrUpload1.add(jsonObject2);
                        }
                        //jsonObject3.add("legs",arrUpload1);
                        jsonObject3.add("legs",arrUpload1);
                        arrFile.add(jsonObject3);
                        arraySteps.add(arrayPoints);
                    }

                    objFile.add("router",arrFile);
                    //strFile=objFile+"";
                   // strFile=strFile.replaceAll("\\\\","");

                    Log.d("router!!",objFile+"");




                    for(int i=0;i<arraySteps.size();i++){
                        Log.i("arraytest",i+"");
                        for(int j=0;j<arraySteps.get(i).size();j++){
                            Log.i("arraytest",arraySteps.get(i).get(j)+"");
                        }

                    }
                    Log.i("arraytest123",arraySteps.toString()+"");
                   /* dirPolyline=new String();
                    dirPolyline= routeObject.getJSONObject(0).getJSONObject("overview_polyline").getString("points");*/
                    drawAll();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private  void drawPath(final ArrayList<LatLng>points){
        mMap.addPolyline(new PolylineOptions().
        addAll(points).
        width(5).
        color(Color.BLUE));

    }
    private ArrayList<LatLng> decodePoly(String encoded){
        ArrayList<LatLng>poly = new ArrayList<>();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while (index<len){
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b &0x1f)<<shift;
                shift+=5;
            }while(b>=0x20);
            int dlat = ((result&1)!=0?~(result>>1):(result>>1));
            lat+=dlat;
            shift=0;
            result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b &0x1f)<<shift;
                shift+=5;
            }while(b>=0x20);
            int dlng = ((result&1)!=0?~(result>>1):(result>>1));
            lng+=dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }


    private void drawAll() {
        if (mMap != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("I'm here...").snippet("now"));
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
                        Log.d("onPostExecute", "draw line error!");
                    }
                    if (busDataArrayList != null) {
                        for (int i = 0; i < busDataArrayList.size(); i++) {
                            mMap.addMarker(new MarkerOptions()
                                    .title(busDataArrayList.get(i).getName())
                                    .snippet("bus")
                                    .position(busDataArrayList.get(i).getLatLng())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmpArray.get(i))));
                        }
                    }

                    if(arraySteps!=null){
                        for(int i=0;i<arraySteps.size();i++){
                            Log.i("arraytest",i+"");
                            if(points_state.get(i)==1){
                                for(int j=0;j<arraySteps.get(i).size();j++){
                                    Log.i("arraytest",arraySteps.get(i).get(j)+"");
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



    public static MyLatLng getMyLatLng(MyLatLng A,double distance,double angle){
        double dx = distance*1000*Math.sin(Math.toRadians(angle));
        double dy= distance*1000*Math.cos(Math.toRadians(angle));

        double bjd=(dx/A.Ed+A.m_RadLo)*180./Math.PI;
        double bwd=(dy/A.Ec+A.m_RadLa)*180./Math.PI;
        return new MyLatLng(bjd, bwd);
    }

    public  static double getAngle(MyLatLng A,MyLatLng B){
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

    public String turnTo(MyLatLng A,MyLatLng B){
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

    public float getDistance(LatLng point_one,LatLng point_two){
        float[] results=new float[1];
        Location.distanceBetween(point_one.latitude,point_one.longitude,point_two.latitude,point_two.longitude,results);
        Log.i("距離",results[0]+"");
        return results[0];
    }
    public void router_action(){
        if(nowPoint!=null){
            float distance=getDistance(currentLocation,arraySteps.get(nowPoint[0]).get(nowPoint[1]).get(nowPoint[2]));
            if(distance<5){
                if(arraySteps.get(nowPoint[0]).get(nowPoint[1]).size()==nowPoint[2]+1){
                    nowPoint[2]=0;
                    if(arraySteps.get(nowPoint[0]).size()==nowPoint[1]+1){
                        if(points.size()==nowPoint[0]+1){
                            Toast.makeText(MapsActivity.this,"結束導航", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MapsActivity.this,"下個路徑點(大)", Toast.LENGTH_SHORT).show();
                            nowPoint[0]++;
                        }

                    }else{
                        Toast.makeText(MapsActivity.this,"下個路徑點(小)", Toast.LENGTH_SHORT).show();
                        nowPoint[1]++;
                    }
                }else {
                    Toast.makeText(MapsActivity.this,"下個點", Toast.LENGTH_SHORT).show();
                    nowPoint[2]++;
                }
                Toast.makeText(MapsActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();
            }else if(distance<10){
                Toast.makeText(MapsActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();
            }else if(distance<15){
                Toast.makeText(MapsActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MapsActivity.this,"點距"+distance, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void uploadToServer(ArrayList<ArrayList<ArrayList<LatLng>>> router,String ID,String mail){
        jsonObjectToServer=new JSONObject();
        try{
            jsonObjectToServer.put("id",ID);
        } catch (Exception e) {
            Log.e("error on upload ID",e.toString());
        }
        try{
            //test upload
           /* JsonObject objSteps=new JsonObject();
            JSONArray arrStepsLat=new JSONArray();
            JSONArray arrStepsLon=new JSONArray();

            for(int i=0;i<router.size();i++){


                for(int j=0;j<router.get(i).size();j++){

                    JsonObject objLegs=new JsonObject();

                    for(int k=0;k<router.get(i).get(j).size();k++){
                        arrStepsLat=new JSONArray();
                        arrStepsLon=new JSONArray();
                        arrStepsLat.put(router.get(i).get(j).get(k).latitude);
                        arrStepsLon.put(router.get(i).get(j).get(k).longitude);

                    }


                }

            }*/







            jsonObjectToServer.put("Router",router.toString());
        } catch (Exception e) {
            Log.e("error on upload Router",e.toString());
        }
        Log.i("check upload messenger",jsonObjectToServer.toString());


        //--------------upload start---------------------------
        request = new StringRequest(com.android.volley.Request.Method.POST, URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),"SUCCESS!!"+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(),"Error!!"+jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("error",e.toString());
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
                //Map.put("router", jsonObjectToServer.toString());
                if(objFile!=null){
                    Map.put("router", objFile.toString());
                }

                Map.put("ID",personName);
                Map.put("email",personEmail);
                Map.put("router_name","testfile");
                return Map;
            }
        };
        requestQueue.add(request);
    }
    //download to cellphone
    public void downloadToPhone(){
        jsonObjectToServer=new JSONObject();
       /* try{
            jsonObjectToServer.put("id",ID);
        } catch (Exception e) {
            Log.e("error on upload ID",e.toString());
        }
        Log.i("check upload messenger",jsonObjectToServer.toString());*/


        //--------------upload start---------------------------
        request = new StringRequest(com.android.volley.Request.Method.POST, downLoad_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.names().get(0).equals("id")){
                        //Toast.makeText(getApplicationContext(),"SUCCESS!!"+jsonObject.getString("Router"),Toast.LENGTH_SHORT).show();
                        Log.i("getRouter",jsonObject.getString("Router"));
                        download_router.clear();
                        //jsonObject.getJSONArray("Router");



                    }else{
                        Toast.makeText(getApplicationContext(),"Error!!"+jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("error",e.toString());
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
                Map.put("router_name","testfile");
                return Map;
            }
        };
        requestQueue.add(request);
    }
    //picker view
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        Toast.makeText(MapsActivity.this, "Begin Parse Data", Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    Toast.makeText(MapsActivity.this, "Parse Succeed", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(MapsActivity.this, "Parse Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据!!!!!這裡很重要

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }
    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
    private void showPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + opt2tx + opt3tx;
                Toast.makeText(MapsActivity.this, tx, Toast.LENGTH_SHORT).show();
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    //--------------set router picker view-----------
    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = cardItem.get(options1).getPickerViewText();
                btn_CustomOptions.setText(tx);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        final TextView tvAdd = (TextView) v.findViewById(R.id.tv_add);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });

                        tvAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getCardData();
                                pvCustomOptions.setPicker(cardItem);
                            }
                        });

                    }
                })
                .isDialog(true)
                .setOutSideCancelable(false)
                .build();

        pvCustomOptions.setPicker(cardItem);//添加数据


    }

    private void getCardData() {
        for (int i = 0; i < 5; i++) {
            cardItem.add(new CardBean(i, "No.ABC12345 " + i));
        }

        for (int i = 0; i < cardItem.size(); i++) {
            if (cardItem.get(i).getCardNo().length() > 6) {
                String str_item = cardItem.get(i).getCardNo().substring(0, 6) + "...";
                cardItem.get(i).setCardNo(str_item);
            }
        }
    }











}