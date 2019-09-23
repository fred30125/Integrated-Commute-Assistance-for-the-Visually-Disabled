package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    private MapView mapView;


    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    // 取得裝置目前最新的位置
    private Location currentLocation;
    // 目前位置對應的marker
    private Marker currentMarker;
    // 畫線
    ArrayList<LatLng> points;
    PolylineOptions lineOptions;
    Polyline polyline;
    private BluetoothChatFragment bluetoothChatFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        points= new ArrayList<>(); // 所有點集合
        lineOptions = new PolylineOptions(); // 多邊形
        bluetoothChatFragment=new BluetoothChatFragment();
        FragmentActivity activity = getActivity();
        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);


           /* configGoogleApiClient();
            configLocationRequest();*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng latLng = new LatLng(25.027567, 121.386571);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //放大地圖16倍
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

    }

    private synchronized void configGoogleApiClient(){
        FragmentActivity activity = getActivity();
        googleApiClient=new GoogleApiClient.Builder(activity)
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
        FragmentActivity activity = getActivity();
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

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
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "裝置沒有安裝Google Play服務",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        Log.i("GPS", "onLocationChanged");
        // 取得目前位置
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // mMap.clear();
        // 設定目前位置的marker
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here..."));

            // points.set(0,latLng);
        } else {
            currentMarker.setPosition(latLng);
        }
        // 移動地圖到目前的位置
        //       bluetoothChatFragment.updateMessage(latLng.latitude+","+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        points= new ArrayList<>();
        LatLng tmp1 = new LatLng(25.027567, 121.386571);
        LatLng tmp=new LatLng(25.032379,121.389918);
        points.add(latLng);
        points.add(tmp);
        points.add(tmp1);
        lineOptions= new PolylineOptions();
        lineOptions.addAll(points); // 加入所有座標點到多邊形
        lineOptions.width(10);
        lineOptions.color(Color.RED);
        if(lineOptions != null) {
            if(polyline != null){
                polyline.remove();
            }
            polyline = this.mMap.addPolyline(lineOptions);
            //mMap.addPolyline(lineOptions); // 畫出多邊形
        }
        else {
            Log.d("onPostExecute","draw line error!");
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) { //取得地圖
            FragmentActivity activity = getActivity();
            ((SupportMapFragment) activity.getSupportFragmentManager().
                    findFragmentById(R.id.map)).getMapAsync(this);
        }
        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            Log.i("GPS", "onResume");
            googleApiClient.connect();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        // 取消位置請求服務
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }











}
