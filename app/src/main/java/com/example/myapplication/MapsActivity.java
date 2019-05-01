package com.example.myapplication;

import android.Manifest;
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
import android.location.Location;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.common.logger.LogWrapper;
import com.example.myapplication.common.logger.MessageOnlyLogFilter;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

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
    private Button setMarker;
    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    // 取得裝置目前最新的位置
    private LatLng currentLocation;
    // 目前位置對應的marker
    private Marker currentMarker;
    // 畫線
    ArrayList<LatLng> points;
    PolylineOptions lineOptions;
    Polyline polyline;
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


    //--------------info window test--------------
    private Button infoButton1, infoButton2;
    private TextView routerID;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private ViewGroup infoWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentLocation=null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        points= new ArrayList<>(); // 所有點集合
        markerArrayList=new ArrayList<>();
        lineOptions = new PolylineOptions(); // 多邊形
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
        setMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setMarkerStatus==false){
                    setMarkerStatus=true;
                    setMarker.setText("setMarker:ON");
                }else{
                    setMarkerStatus=false;
                    setMarker.setText("setMarker:OFF");
                }
            }
        });
        points= new ArrayList<>();
        markerTotal=0;
        moveTimes=false;

        //--------info window設定------------
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.custom_infowindow, null);
        this.infoButton1 = (Button)infoWindow.findViewById(R.id.btnOne);
        this.infoButton2 = (Button)infoWindow.findViewById(R.id.btnTwo);
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
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("I'm here...").snippet("now"));
                    for(int i =0;i<markerArrayList.size();i++){
                        markerArrayList.get(i).title(i+"");
                        mMap.addMarker(markerArrayList.get(i));
                    }
                    lineOptions = new PolylineOptions();
                    lineOptions.add(currentLocation); // 加入所有座標點到多邊形
                    lineOptions.addAll(points); // 加入所有座標點到多邊形
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);
                    if (lineOptions != null) {
                        if (polyline != null) {
                            polyline.remove();
                        }
                        polyline = mMap.addPolyline(lineOptions);
                    } else {
                        Log.d("onPostExecute", "draw line error!");
                    }
                }
            }
        };
        this.infoButton1.setOnTouchListener(infoButtonListener);

        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton2, getResources().getDrawable(R.drawable.btn_bg),getResources().getDrawable(R.drawable.btn_bg)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(MapsActivity.this, routerID.getText(), Toast.LENGTH_SHORT).show();
                // Here we can perform some action triggered after clicking the button
                //Toast.makeText(MapsActivity.this, "click on button 1", Toast.LENGTH_SHORT).show();
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
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("I'm here...").snippet("now"));
                    for(int i =0;i<markerArrayList.size();i++){
                        markerArrayList.get(i).title(i+"");
                        mMap.addMarker(markerArrayList.get(i));
                    }
                    lineOptions = new PolylineOptions();
                    lineOptions.add(currentLocation); // 加入所有座標點到多邊形
                    lineOptions.addAll(points); // 加入所有座標點到多邊形
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);
                    if (lineOptions != null) {
                        if (polyline != null) {
                            polyline.remove();
                        }
                        polyline = mMap.addPolyline(lineOptions);
                    } else {
                        Log.d("onPostExecute", "draw line error!");
                    }
                }
            }
        };
        infoButton2.setOnTouchListener(infoButtonListener);

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
                if(marker.getSnippet().equals("bus")||marker.getSnippet().equals("now")){
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
                        lineOptions = new PolylineOptions();
                        lineOptions.add(currentLocation); // 加入所有座標點到多邊形
                        lineOptions.addAll(points); // 加入所有座標點到多邊形
                        lineOptions.width(10);
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
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                if (lineOptions != null) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                    polyline = mMap.addPolyline(lineOptions);
                } else {
                    Log.d("onPostExecute", "draw line error!");
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



        //float results[]=new float[1];
        //現在緯度,現在經度,目標緯度,目標經度,
        //Location.distanceBetween(latLng.latitude, latLng.longitude, tmp.latitude, tmp.longitude, results);
        //String distance = NumberFormat.getInstance().format(results[0]);
        /*if(results[0]<100){
            bluetoothChatFragment.updateMessage("Q");
        }*/
        //Toast.makeText(this, "與現在距離"+distance+"公尺",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null) { //取得地圖
            ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.map)).getMapAsync(this);
        }
        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            Log.i("GPS", "onResume");
            googleApiClient.connect();
        }
    }
    @Override
    protected void onPause() {
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
        busStationURL.append("&key="+google_maps_key);
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


}