package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.myapplication.bean.CardBean;
import com.example.myapplication.bean.JsonBean;
import com.example.myapplication.setting.AppClientManager;
import com.example.myapplication.setting.GetRealTimeNearStop;
import com.example.myapplication.setting.GetStation;
import com.example.myapplication.setting.GetStopOfRoute;
import com.example.myapplication.setting.RealTimeNearStop.RealTimeNearStop;
import com.example.myapplication.setting.Station.Station;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myapplication.Constants.BUS_URL;

public class FinalSetRouterActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    // 取得裝置目前最新的位置
    private LatLng currentLocation;
    private LatLng firstLocation;
    private Marker currentMarker;

    private Marker firstMarker;
    // 畫線
    ArrayList<LatLng> points;
    ArrayList<Integer> points_state;//0=false 1=true
    ArrayList<ArrayList<LatLng>> arrayPoints;
    ArrayList<ArrayList<ArrayList<LatLng>>> arraySteps;

    PolylineOptions lineOptions;
    Polyline polyline;
    String dirPolyline;

    Boolean setMarkerStatus = true;
    int markerTotal;

    ArrayList<MarkerOptions> markerArrayList;
    ArrayList<MarkerOptions> busMarkerArrayList;
    boolean moveTimes;
    ArrayList<busData> busDataArrayList;
    ArrayList<Bitmap> bmpArray = new ArrayList<>();
    Bitmap bmp;
    String google_maps_key;
    MapWrapperLayout mapWrapperLayout;
    //---------------指南針---------------------
    private SensorManager manager;
    private SensorListener listener = new SensorListener();
    private float lastRotateDegree;
    private long lastUpdatetime;
    //--------------info window test--------------
    private Button infoButton1, infoButton2, infoButton3;
    private TextView routerID;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private ViewGroup infoWindow;
    //------------direction---------------
    int[] nowPoint;


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
    Geocoder geocoder;
    private final static String URL = "http://163.25.101.33:80/loginapp/upload_router.php";
    private final static String downLoad_URL = "http://163.25.101.33:80/loginapp/router_download.php";
    private final static String ROUTER_NAME_URL = "http://163.25.101.33:80/loginapp/router_name.php";
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
    JSONObject objFile;
    JSONArray arrFile;
    ArrayList<String> getRouterNameArr;
    //------------筱琪--------------
    String Data = "";
    String City_a = "Taipei";
    String Query = "";
    String Start = "";
    String Dir = "";
    String Route = "";
    //LinkedList<HashMap<String, String>> routeList = new LinkedList<>();
    List<String> routeOrder = new ArrayList<String>();
    List<String> routeList = new ArrayList<String>();
    List<String> SameDepartureRouteList = new ArrayList<String>();
    List<String> SameDestationRouteList = new ArrayList<String>();
    List<String> SameStopName = new ArrayList<String>();
    //----------------------------Test Data------------------------------
    String StopName1 = "金陵女中";
    double endLat;
    double endLon;
    String StopNameStart;
    LatLng endLatLng;
    LatLng startLatLng;
    int CARDSTATE = 0;
    final int STATE_BUS_NUM = 0;
    final int STATE_BUS_STOP = 1;
    final int STATE_BUS_SAME = 2;
    final int STATE_DOWNLOAD = 3;
    //--------------bus arrival time----------
    List<String> routeIDList = new ArrayList<String>();
    private LinkedList<HashMap<String, String>> EstimatedTimeList = new LinkedList<>();
    String StopNamehex = "";
    int StopName1Squence;
    String busStopstart;
    //--------------reservation--------------
    //--------------筱淇----------------
    //-------------recycle------------

    private List<RouterData> data_list;
    LatLng routerPoints;
    String tmpTX;

    Button btnUpload;
    Button btnCheckAddress;

    List<Address> addressName;
    List<Address> addressLatLng;

    TextView txtLat,txtLng;
    EditText txtAddress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_set_router);
        txtLat=findViewById(R.id.txt_lat);
        txtLng=findViewById(R.id.txt_lon);
        btnCheckAddress=findViewById(R.id.btn_check_addressname);
        btnUpload = findViewById(R.id.btnUpload);
        txtAddress=findViewById(R.id.txt_addressname);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        currentLocation = null;
        markerTotal=0;
        firstLocation = null;
        firstMarker=null;
        requestQueue = Volley.newRequestQueue(this);
        markerArrayList=new ArrayList<>();
        busMarkerArrayList=new ArrayList<>();
        btnCheckAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtAddress.getText()!=null){
                    markerArrayList=new ArrayList<>();
                    busMarkerArrayList=new ArrayList<>();
                    points=new ArrayList<>();
                    points_state=new ArrayList<>();
                    arrayPoints=new ArrayList<>();
                    arraySteps=new ArrayList<>();
                    markerTotal=0;

                    LatLng tmpLatLng=convertAddressToLanLng(txtAddress.getText().toString());
                    if(tmpLatLng!=null){
                        firstLocation=tmpLatLng;
                        points.add(firstLocation);
                        points_state.add(1);
                        //firstMarker = mMap.addMarker(new MarkerOptions().position(tmpLatLng).title("I'm here...").snippet("now"));
                        MarkerOptions tmpMarker = new MarkerOptions()
                                .title("0")
                                .snippet("起始點")
                                .position(firstLocation)
                                .draggable(true);
                        markerTotal++;
                        mMap.addMarker(tmpMarker);
                        markerArrayList.add(tmpMarker);


                        txtLat.setText(tmpLatLng.latitude+"");
                        txtLng.setText(tmpLatLng.longitude+"");
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(tmpLatLng));
                        requestBusStation(tmpLatLng);
                        drawAll();

                    }
                }
            }
        });
        //-----------------google測試--------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            personId = acct.getId();
        }
        //-----------------google測試 end--------------------------
        dirPolyline = new String();
        points = new ArrayList<>(); // 所有點集合
        points_state = new ArrayList<>();
        markerArrayList = new ArrayList<>();
        lineOptions = new PolylineOptions(); // 多邊形
        arrayPoints = new ArrayList<>();
        arraySteps = new ArrayList<>();
        routerPoints = null;
        configGoogleApiClient();
        configLocationRequest();

        geocoder = new Geocoder(this);
        addressName = null;
        addressLatLng=null;
        //---------------fragment-----------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.set_map);
        mapFragment.getMapAsync(this);
        google_maps_key = getResources().getString(R.string.google_maps_key);
        data_list = new ArrayList<>();


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToServer(arraySteps, personName, personEmail);
            }
        });
        markerTotal = 0;
        moveTimes = false;
        //--------指南針--------------------
        lastRotateDegree = 0;
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magnetic = null;
        if (manager != null) {
            magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if (manager != null) {
            manager.registerListener(listener, magnetic, SensorManager.SENSOR_DELAY_GAME);
        }
        //
        //--------info window設定------------
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_infowindow, null);
        this.infoButton1 = (Button) infoWindow.findViewById(R.id.btnOne);
        this.infoButton2 = (Button) infoWindow.findViewById(R.id.btnTwo);
        this.infoButton3 = (Button) infoWindow.findViewById(R.id.btnThree);
        this.routerID = infoWindow.findViewById(R.id.RouterID);
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton1, getResources().getDrawable(R.drawable.btn_bg), getResources().getDrawable(R.drawable.btn_bg)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(FinalSetRouterActivity.this, routerID.getText(), Toast.LENGTH_SHORT).show();
                // Here we can perform some action triggered after clicking the button
                //Toast.makeText(MapsActivity.this, "click on button 1", Toast.LENGTH_SHORT).show();
                if (routerID.getText().equals("0")) {
                    Toast.makeText(FinalSetRouterActivity.this, "沒辦法往前", Toast.LENGTH_SHORT).show();
                } else if (markerTotal < 2) {
                    Toast.makeText(FinalSetRouterActivity.this, "沒辦法往前", Toast.LENGTH_SHORT).show();
                } else {
                    int ID = Integer.parseInt(routerID.getText().toString());
                    LatLng tmp;
                    tmp = points.get(ID);
                    points.set(ID, points.get(ID - 1));
                    points.set(ID - 1, tmp);

                    tmp = markerArrayList.get(ID).getPosition();
                    markerArrayList.get(ID).position(markerArrayList.get(ID - 1).getPosition());
                    markerArrayList.get(ID - 1).position(tmp);
                    getDirection(firstLocation, points.get(points.size() - 1));
                }
            }
        };
        this.infoButton1.setOnTouchListener(infoButtonListener);

        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton2, getResources().getDrawable(R.drawable.btn_bg), getResources().getDrawable(R.drawable.btn_bg)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(FinalSetRouterActivity.this, routerID.getText(), Toast.LENGTH_SHORT).show();
                if (routerID.getText().equals(markerArrayList.size() - 1 + "")) {
                    Toast.makeText(FinalSetRouterActivity.this, "沒辦法往後", Toast.LENGTH_SHORT).show();
                } else if (markerTotal < 2) {
                    Toast.makeText(FinalSetRouterActivity.this, "沒辦法往後", Toast.LENGTH_SHORT).show();
                } else {
                    int ID = Integer.parseInt(routerID.getText().toString());
                    LatLng tmp;
                    tmp = points.get(ID);
                    points.set(ID, points.get(ID + 1));
                    points.set(ID + 1, tmp);

                    tmp = markerArrayList.get(ID).getPosition();
                    markerArrayList.get(ID).position(markerArrayList.get(ID + 1).getPosition());
                    markerArrayList.get(ID + 1).position(tmp);
//-----------------draw
                    getDirection(firstLocation, points.get(points.size() - 1));
                }
            }
        };
        infoButton2.setOnTouchListener(infoButtonListener);


        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton3, getResources().getDrawable(R.drawable.btn_bg), getResources().getDrawable(R.drawable.btn_bg)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                int ID = Integer.parseInt(routerID.getText().toString());
                if (points_state.get(ID) == 0) {
                    points_state.set(ID, 1);
                    getDirection(firstLocation, points.get(points.size() - 1));
                } else {
                    points_state.set(ID, 0);
                    drawAll();
                }

            }
        };
        infoButton3.setOnTouchListener(infoButtonListener);

        //-----------picker view
        getCardData();
        initCustomOptionPicker();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FinalSetRouterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, FinalSetRouterActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, "裝置沒有安裝Google Play服務", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("GPS", "onLocationChanged");
        // 取得目前位置
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocation = latLng;
        //Log.v("currentLocation data", currentLocation.toString());
        // mMap.clear();
        // 設定目前位置的marker
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("I'm here...").snippet("now").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("standing_up_man",100,100))));;
            // points.set(0,latLng);
        } else {
            currentMarker.setPosition(latLng);
        }
        // 移動地圖到目前的位置
        //bluetoothChatFragment.updateMessage("Q");
        if (moveTimes == false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            moveTimes = true;
        }

        if (points.size() > 0) {
            MapsActivity.MyLatLng tmp = new MapsActivity.MyLatLng(points.get(0).longitude, points.get(0).latitude);
            MapsActivity.MyLatLng current = new MapsActivity.MyLatLng(firstLocation.longitude, firstLocation.latitude);
            //getDistance(firstLocation, points.get(0));
            //Toast.makeText(this, turnTo(current, tmp), Toast.LENGTH_SHORT).show();
        }
        //router_action();
    }

    @Override
    protected void onResume() {
        if (mMap == null) { //取得地圖
            ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.set_map)).getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        mapWrapperLayout.init(googleMap, getPixelsFromDp(this, 39 + 20));//39+20
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override//設定info類型
            public View getInfoContents(Marker marker) {
                if (marker.getSnippet().equals("bus")) {          //點公車
                    if (isLoaded) {
                        CARDSTATE = STATE_BUS_NUM;
                        StopName1 = marker.getTitle();
                        //RouteOrder();
                        //Log.e("HELLO 整合",routeList.size()+"");
                        RouteOfStation(marker.getPosition().latitude, marker.getPosition().longitude, 50, 0); //抓附近公車站的公車號碼
                        startLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                        //StopOfRoute("513","0",0);//抓會到的公車站
                        //SameDestinationRoute("513","1","中興街口","捷運輔大站");
                        //showPickerView();
                    } else {
                        Toast.makeText(FinalSetRouterActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
                    }
                    return null;
                } else if (marker.getSnippet().equals("now")) {
                    return null;
                } else if (marker.getSnippet().equals("bus_stop")) {
                    return null;
                }else {
                    // Setting up the infoWindow with current's marker info
                    infoButtonListener.setMarker(marker);
                    // We must call this to set the current marker and infoWindow references
                    // to the MapWrapperLayout
                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                    routerID.setText(marker.getTitle());
                    Toast.makeText(FinalSetRouterActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();

                    return infoWindow;
                }
            }

        });
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (firstLocation != null) {
                    Toast.makeText(FinalSetRouterActivity.this, latLng.latitude + "," + latLng.longitude, Toast.LENGTH_LONG).show();
                    if (setMarkerStatus) {
                        points.add(latLng);
                        points_state.add(1);

                        lineOptions = new PolylineOptions();
                        lineOptions.add(firstLocation); // 加入所有座標點到多邊形
                        lineOptions.addAll(points); // 加入所有座標點到多邊形
                        lineOptions.width(1);
                        lineOptions.color(Color.RED);
                        if (lineOptions != null) {
                            if (polyline != null) {
                                polyline.remove();
                            }
                            polyline = mMap.addPolyline(lineOptions);

                            MarkerOptions tmpMarker = new MarkerOptions()
                                    .title(markerTotal + "")
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
                        getDirection(firstLocation, points.get(points.size() - 1));
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

                try{
                    points.set(Integer.parseInt(marker.getTitle()), marker.getPosition());
                    markerArrayList.get(Integer.parseInt(marker.getTitle())).position(marker.getPosition());
                } catch (Exception e) {
                    points.set(0, marker.getPosition());
                    markerArrayList.get(0).position(marker.getPosition());
                }
                if(Integer.parseInt(marker.getTitle())==0){
                    if(convertLanLngToAddress(marker.getPosition())!=null){
                        txtAddress.setText(convertLanLngToAddress(marker.getPosition()));
                        txtLat.setText(marker.getPosition().latitude+"");
                        txtLng.setText(marker.getPosition().longitude+"");
                        requestBusStation(points.get(0));

                        MarkerOptions tmpMarker = new MarkerOptions()
                                .title("0")
                                .snippet("這是公車")
                                .position(firstLocation)
                                .draggable(true);
                        markerTotal++;
                    }
                }

                lineOptions = new PolylineOptions();
              //  lineOptions.add(firstLocation); // 加入所有座標點到多邊形
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
                if (points_state.get(Integer.parseInt(marker.getTitle())) == 0) {
                    drawAll();
                } else {
                    if(markerTotal<2){

                    }else{
                        getDirection( points.get(0), points.get(points.size() - 1));
                    }
                    //getDirection(firstLocation, points.get(points.size() - 1));
                }

            }
        });
    }

    private class SensorListener implements SensorEventListener {
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
            lastRotateDegree = roteteDegree;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }

    private void updateCamera(float bearing) {
        if (mMap != null) {
            CameraPosition oldPos = mMap.getCameraPosition();
            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        }
    }

    //連API
    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
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

    public void uploadToServer(ArrayList<ArrayList<ArrayList<LatLng>>> router, String ID, String mail) {
        jsonObjectToServer = new JSONObject();
        try {
            jsonObjectToServer.put("id", ID);
        } catch (Exception e) {
            Log.e("error on upload ID", e.toString());
        }
        try {
            jsonObjectToServer.put("Router", router.toString());
        } catch (Exception e) {
            Log.e("error on upload Router", e.toString());
        }
        Log.i("check upload messenger", jsonObjectToServer.toString());


        //--------------upload start---------------------------
        request = new StringRequest(com.android.volley.Request.Method.POST, URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), "SUCCESS!!" + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error!!" + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("error", e.toString());
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

                JSONArray arrfile = new JSONArray();
                JSONArray arrBusMarker = new JSONArray();

                for (int i = 0; i < data_list.size(); i++) {
                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("lat", data_list.get(i).getLatLng().latitude);
                        jsonObject1.put("lon", data_list.get(i).getLatLng().longitude);
                        jsonObject1.put("des", data_list.get(i).getDescription());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    arrfile.put(jsonObject1);
                }
                //上傳公車merker
                for (int i = 0; i < busMarkerArrayList.size(); i++) {
                    JSONObject jsonObject1 = new JSONObject();
                    try {
                        jsonObject1.put("title", busMarkerArrayList.get(i).getTitle());
                        jsonObject1.put("Lat", busMarkerArrayList.get(i).getPosition().latitude);
                        jsonObject1.put("Lon", busMarkerArrayList.get(i).getPosition().longitude);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    arrBusMarker.put(jsonObject1);
                }
                //---
                if (objFile != null && data_list != null) {
                    try {
                        objFile.put("point_state", points_state);
                        objFile.put("data_list", arrfile);
                        if(arrBusMarker!=null){
                            try {
                                objFile.put("bus_marker",arrBusMarker);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray tmpArr = new JSONArray();
                    for (int i = 0; i < points.size(); i++) {
                        JSONObject objtmp = new JSONObject();
                        try {
                            objtmp.put("Lat", points.get(i).latitude);
                            objtmp.put("Lon", points.get(i).longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tmpArr.put(objtmp);
                    }
                    try {
                        objFile.put("points", tmpArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Map.put("router", objFile.toString());
                }



                Map.put("ID", personName);
                Map.put("email", personEmail);
                Map.put("router_name", "testfile");
                return Map;
            }
        };
        requestQueue.add(request);
    }

    //get 路徑
    private void getDirection(LatLng origin, LatLng dest) {
        // txtRouter.setText("");

        String mapAPI = getDirectionsUrl(origin, dest);
        String url = MessageFormat.format(mapAPI, origin, dest);
        Log.i("direction test url", url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.i("direction test -1", jsonObject.toString());
                    JSONArray routeObject = jsonObject.getJSONArray("routes");
                    Log.i("direction test -2", routeObject.toString());

                    objFile = new JSONObject();
                    arrFile = new JSONArray();

                    data_list.clear();
                    JSONArray insObject = routeObject.getJSONObject(0).getJSONArray("legs");
                    Log.i("html測試", insObject.length() + "");
                    arraySteps = new ArrayList<>();


                    for (int i = 0; i < insObject.length(); i++) {
                        JSONObject jsonObject3 = new JSONObject();
                        JSONArray arrUpload1 = new JSONArray();
                        JSONArray insArray;
                        insArray = insObject.getJSONObject(i).getJSONArray("steps");
                        arrayPoints = new ArrayList<>();
                        for (int j = 0; j < insArray.length(); j++) {
                            JSONObject jsonObject2 = new JSONObject();
                            String html_ins = new String();
                            double tmpLat = insArray.getJSONObject(j).getJSONObject("start_location").getDouble("lat");
                            double tmpLng = insArray.getJSONObject(j).getJSONObject("start_location").getDouble("lng");
                            LatLng tmpLatLng = new LatLng(tmpLat, tmpLng);

                            html_ins = insArray.getJSONObject(j).getString("html_instructions");
                            Log.i("html測試", Html.fromHtml(html_ins).toString());
                            if (points_state.get(i) != 2) {//不等於公車
                                sethtmlTxt(Html.fromHtml(html_ins).toString(), tmpLatLng);
                            } else if (points_state.get(i) == 2 && j == 0) { //假如等於公車、只做一次
                                final int tmp_i_num = i;

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        // txtRouter.append(str+"\n");
                                        RouterData data = new RouterData();
                                        data.setDescription("抵達公車上車車站");
                                        data.setLatLng(points.get(tmp_i_num - 1));
                                        Log.i("adapter", data.getDescription());
                                        data_list.add(data);


                                        data = new RouterData();
                                        data.setDescription("這是公車下車站");
                                        data.setLatLng(points.get(tmp_i_num));
                                        Log.i("adapter", data.getDescription());
                                        data_list.add(data);
                                        //routeAdapter.notifyDataSetChanged();
                                    }
                                });
                            }


                            JSONObject tmp;
                            tmp = insArray.getJSONObject(j);
                            Log.i("html測試123", tmp.getJSONObject("polyline").getString("points"));
                            arrayPoints.add(decodePoly(tmp.getJSONObject("polyline").getString("points")));

                            JSONArray arrfile = new JSONArray();

                            for (int k = 0; k < arrayPoints.get(j).size(); k++) {
                                JSONObject jsonObject1 = new JSONObject();

                                jsonObject1.put("lat", arrayPoints.get(j).get(k).latitude);
                                jsonObject1.put("lon", arrayPoints.get(j).get(k).longitude);
                                arrfile.put(jsonObject1);
                            }
                            jsonObject2.put("steps", arrfile);
                            arrUpload1.put(jsonObject2);
                        }
                        //jsonObject3.add("legs",arrUpload1);
                        jsonObject3.put("legs", arrUpload1);
                        arrFile.put(jsonObject3);
                        arraySteps.add(arrayPoints);
                    }

                    objFile.put("router", arrFile);
                    //strFile=objFile+"";
                    // strFile=strFile.replaceAll("\\\\","");

                    Log.d("router!!", objFile + "");


                    for (int i = 0; i < arraySteps.size(); i++) {
                        Log.i("arraytest", i + "");
                        for (int j = 0; j < arraySteps.get(i).size(); j++) {
                            Log.i("arraytest", arraySteps.get(i).get(j) + "");
                        }

                    }
                    Log.i("arraytest123", arraySteps.toString() + "");
                   /* dirPolyline=new String();
                    dirPolyline= routeObject.getJSONObject(0).getJSONObject("overview_polyline").getString("points");*/
                    routerPoints = null;
                    drawAll();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        for (int i = 0; i < points.size() - 1; i++) {
            LatLng point = (LatLng) points.get(i);
            if (i == 0)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&language=zh-TW&sensor=true&mode=walking&key=" + google_maps_key;
        return url;
    }

    public void sethtmlTxt(final String str, final LatLng latLng) {
        runOnUiThread(new Runnable() {
            public void run() {
                // txtRouter.append(str+"\n");
                RouterData data = new RouterData();
                data.setDescription(str);
                data.setLatLng(latLng);
                Log.i("adapter", data.getDescription());
                data_list.add(data);
                //routeAdapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void drawAll() {
        if (mMap != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                    if (currentLocation != null) {

                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("I'm here...").snippet("now").icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("standing_up_man",100,100))));

                    }

                    for (int i = 0; i < markerArrayList.size(); i++) {
                        markerArrayList.get(i).title(i + "");
                        mMap.addMarker(markerArrayList.get(i));
                    }
                    for (int i = 0; i < busMarkerArrayList.size(); i++) {
                        //busMarkerArrayList.get(i).title(i + "");
                        mMap.addMarker(busMarkerArrayList.get(i));
                    }
                    lineOptions = new PolylineOptions();
                   // lineOptions.add(currentLocation); // 加入所有座標點到多邊形
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
                        try {
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
                    if (routerPoints != null) {
                        mMap.addMarker(new MarkerOptions()
                                .title("路徑播報")
                                .snippet("router")
                                .position(routerPoints)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.to_down)));

                    }

                    if (arraySteps != null) {
                        for (int i = 0; i < arraySteps.size(); i++) {
                            Log.i("arraytest", i + "");
                            if (points_state.get(i) == 1) { //一般路徑
                                for (int j = 0; j < arraySteps.get(i).size(); j++) {
                                    Log.i("arraytest", arraySteps.get(i).get(j) + "");
                                    drawPath(arraySteps.get(i).get(j));
                                }
                            } else {
                                if (i == 0) {
                                 /*   ArrayList<LatLng> tmp = new ArrayList<>();
                                    tmp.add(firstLocation);
                                    tmp.add(points.get(i));
                                    drawPath(tmp);*/
                                } else {
                                    ArrayList<LatLng> tmp = new ArrayList<>();
                                    tmp.add(points.get(i - 1));
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

    private void drawPath(final ArrayList<LatLng> points) {
        mMap.addPolyline(new PolylineOptions().
                addAll(points).
                width(5).
                color(Color.BLUE));

    }

    //----------取得公車資料---------
    public void requestBusStation(LatLng searchLocation) {
        busDataArrayList = new ArrayList<>();
        StringBuilder busStationURL = new StringBuilder(BUS_URL);
        busStationURL.append("location=" + searchLocation.latitude + "," + searchLocation.longitude);
        busStationURL.append("&radius=5000");
        busStationURL.append("&types=bus_station");
        busStationURL.append("&language=zh-TW");
        busStationURL.append("&key=" + google_maps_key);
        Log.i("bus url", busStationURL.toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(busStationURL.toString())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    busData busData;
                    JSONObject busObject = new JSONObject(response.body().string());
                    JSONArray busArray = new JSONArray(busObject.getString("results"));
                    String tmpName;
                    java.net.URL tmpIcon;
                    LatLng tmpLatlng;
                    busDataArrayList = new ArrayList<>();
                    bmpArray = new ArrayList<>();
                    for (int i = 0; i < busArray.length(); i++) {
                        JSONObject object = new JSONObject(busArray.get(i).toString());
                        //Log.i("results name", object.getString("name"));
                        tmpName = object.getString("name");
                        tmpIcon = new URL(object.getString("icon"));
                        JSONObject objectGeomestry = new JSONObject(object.getString("geometry"));
                        JSONObject objectLocation = new JSONObject(objectGeomestry.getString("location"));
                        tmpLatlng = new LatLng(objectLocation.getDouble("lat"), objectLocation.getDouble("lng"));
                        busData = new busData(tmpLatlng, tmpName, tmpIcon);
                        busDataArrayList.add(busData);
                    }
                    for (int i = 0; i < busDataArrayList.size(); i++) {
                        Log.i("results name", i + ":" + busDataArrayList.get(i).getName());
                        Log.i("results location", i + ":" + busDataArrayList.get(i).getLatLng().latitude + "&" + busDataArrayList.get(i).getLatLng().longitude);
                        Log.i("results icon", i + ":" + busDataArrayList.get(i).getIcon().toString());
                        URL url = busDataArrayList.get(i).getIcon();
                        try {
                            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            bmpArray.add(bmp);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("results image", "image lose:" + i);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            for (int i = 0; i < busDataArrayList.size(); i++) {
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
                    Log.i("results name", "fail");
                }
            }
        });
    }
    public float getDistance(LatLng point_one, LatLng point_two) {
        float[] results = new float[1];
        Location.distanceBetween(point_one.latitude, point_one.longitude, point_two.latitude, point_two.longitude, results);
        Log.i("距離", results[0] + "");
        return results[0];
    }

//    public String turnTo(MapsActivity.MyLatLng A, MapsActivity.MyLatLng B) {
//        double turnDrgree = lastRotateDegree - getAngle(A, B);
//        if (turnDrgree < 15 && turnDrgree > -15) {
//            return "STRAIGHT";
//        } else if (turnDrgree >= 15 && turnDrgree <= 60) {
//            return "DEV_LEFT";
//        } else if (turnDrgree > 60 && turnDrgree < 110) {
//            return "LEFT";
//        } else if (turnDrgree >= 110 && turnDrgree <= 150) {
//            return "BACK_LEFT";
//        } else if (turnDrgree <= -15 && turnDrgree >= -60) {
//            return "DEV_RIGHT";
//        } else if (turnDrgree < -60 && turnDrgree > -110) {
//            return "RIGHT";
//        } else if (turnDrgree <= -110 && turnDrgree >= -150) {
//            return "BACK_RIGHT";
//        } else if (turnDrgree > 150 || turnDrgree < -150) {
//            return "BACK";
//        }
//        return null;
//    }

    public static double getAngle(MapsActivity.MyLatLng A, MapsActivity.MyLatLng B) {
        double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
        double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
        double angle = 0.0;
        angle = Math.atan(Math.abs(dx / dy)) * 180. / Math.PI;
        double dLo = B.m_Longitude - A.m_Longitude;
        double dLa = B.m_Latitude - A.m_Latitude;
        if (dLo > 0 && dLa <= 0) {
            angle = (90. - angle) + 90;
        } else if (dLo <= 0 && dLa < 0) {
            angle = angle + 180.;
        } else if (dLo < 0 && dLa >= 0) {
            angle = (90. - angle) + 270;
        }
        return angle;
    }
    //-----------筱淇---------------
    //點擊app上的公車mark可以獲得座標，利用座標找出站牌名稱，並且列出站牌中有哪幾路公車
    //這站有哪幾路公車 資料存在 routeList
    //distance:單位是公尺&抓下來DataType=int
    //列德func都填0 func=1:用來下車車站中有哪幾路有到上車車站的將資料存在SameDestationRouteList
    public void RouteOfStation(double CenterLat, double CenterLon, int distance, final int func) {
        routeList = new ArrayList<>();
        Log.e("loc", "現在執行的函式是= RouteOfStation");
        Data = "Station";
        City_a = "Taipei";
        Query = "?$select=Stops&$top=10&$spatialFilter=nearby(StationPosition%2C" +
                CenterLat + "%2C" + CenterLon + "%2C" + distance + ")&$format=JSON";
        if (func == 1) {
            Query = "?$select=Stops&$top=1&$spatialFilter=nearby(StationPosition%2C" +
                    CenterLat + "%2C" + CenterLon + "%2C" + distance + ")&$format=JSON";
        }
        GetStation GetStation = AppClientManager.getClient().create(GetStation.class);
        GetStation.GetStation(Data, City_a, "", Query).enqueue(new retrofit2.Callback<List<Station>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Station>> call, retrofit2.Response<List<Station>> response) {
                Log.e("OkHttp", "車站的路線載入成功了啦 response = " + response.body().toString());
                List<Station> list = response.body();
                if (func == 1) {
                    SameDestationRouteList.clear();
                    SameStopName.clear();
                }
                for (Station p : list) {
                    StopNameStart = p.getStops().get(0).getStopName().getZhTw();
                    //for(int i=0;i<10;i++){
                    //    StopNameStart=p.getStops().get(i).getStopName().getZhTw();
                    //}
                    for (int i = 0; i < p.getStops().size(); i++) {
                        if (func == 1) {

                            SameDestationRouteList.add(p.getStops().get(i).getRouteName().getZhTw());
                            Log.e("func<2>下車站牌的全部路線:", p.getStops().get(i).getRouteName().getZhTw());
                        } else {
                            routeList.add(i, p.getStops().get(i).getRouteName().getZhTw());
                            Log.e("RouteNumbList", p.getStops().get(i).getRouteName().getZhTw());
                        }
                    }
                    if (func == 1) {
                        for (int i = 0; i < SameDestationRouteList.size(); i++) {
                            Log.e("func<3>要帶入StopOfRoute的", "下車路線=" + SameDestationRouteList.get(i));
                            StopOfRoute(SameDestationRouteList.get(i), Dir, 1);
                        }
                    }
                }
                setBusCard(routeList);
                pvCustomOptions.setPicker(cardItem);
                pvCustomOptions.show();
            }

            @Override
            public void onFailure(retrofit2.Call<List<Station>> call, Throwable t) {
                Log.e("OkHttp", "車站的路線載入，怎麼會失敗");
                CARDSTATE = STATE_BUS_NUM;
            }
        });
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
                                    SameDepartureRouteList.add(RouteNumb);
                                    Log.e("func<4>列出相同上車站牌的路線:", RouteNumb);
                                }
                            } else if (func == 2) {
                                if (p.getStops().get(i).getStopName().getZhTw().equals(StopName1)) {
                                    StopName1Squence = p.getStops().get(i).getStopSequence();
                                    Log.e("這臺公車的stop sequence", StopName1Squence + "");

                                    //---------------判斷預約--------------------
                                    Resevation(dir);
                                }
                            } else {
                                routeOrder.add(i, p.getStops().get(i).getStopName().getZhTw());
                                endLat = p.getStops().get(i).getStopPosition().getPositionLat();
                                endLon= p.getStops().get(i).getStopPosition().getPositionLon();
                                Log.e("列出下車站牌", p.getStops().get(i).getStopName().getZhTw());
                            }
                        }
                    }
                }
                if (func == 1) {
                    CARDSTATE = STATE_BUS_SAME;
                    setBusCard(SameDepartureRouteList);
                    pvCustomOptions.setPicker(cardItem);
                    pvCustomOptions.show();
                } else {
                    CARDSTATE = STATE_BUS_STOP;
                    setBusCard(routeOrder);
                    pvCustomOptions.setPicker(cardItem);
                    pvCustomOptions.show();
                }


            }

            @Override
            public void onFailure(retrofit2.Call<List<StopOfRoute>> call, Throwable t) {
                Log.e("OkHttp", "車牌順序的資料連接，怎麼會失敗");
                CARDSTATE = STATE_BUS_NUM;
            }
        });
        Log.d("loc", "現在執行結束的函式是= StopOfRoute");
    }

    //------------------------------------------------------------------------------
    //在某路公車的站牌序列中，選擇下車地點，利用上下車站牌找都有經過的公車路線
    //(上車地點為點擊app上公車mark所查到的站牌)(方向確定)//dir="0"表示去程 ; dir="1"表示返程
    //找出相同上下車地點的公車路線 資料存在 SameDepartureRouteList
    public void SameDestinationRoute(String RouteNumb, final String dir, final String start, final String end) {
        SameDepartureRouteList = new ArrayList<>();
        Log.e("loc", "現在執行的函式是= SameDestinationRoute");
        Data = "StopOfRoute";
        City_a = "Taipei";
        Query = "?$select=Stops&$top=30&$format=JSON";
        GetStopOfRoute GetStopOfRoute = AppClientManager.getClient().create(GetStopOfRoute.class);
        GetStopOfRoute.GetStopOfRoute(Data, City_a, RouteNumb, Query).enqueue(new retrofit2.Callback<List<StopOfRoute>>() {
            @Override
            public void onResponse(retrofit2.Call<List<StopOfRoute>> call, retrofit2.Response<List<StopOfRoute>> response) {
                Log.e("OkHttp", "相同上下車地點的公車路線成功了啦 response = " + response.body().toString());
                List<StopOfRoute> list = response.body();
                for (StopOfRoute p : list) {
                    if (p.getDirection().toString().equals(dir)) {//確定方向
                        for (int i = 0; i < p.getStops().size(); i++) {
                            if (p.getStops().get(i).getStopName().getZhTw().equals(end)) {
                                endLat = p.getStops().get(i).getStopPosition().getPositionLat();
                                endLon = p.getStops().get(i).getStopPosition().getPositionLon();
                                Log.e("func<1>列出下車站牌的經緯度", p.getStops().get(i).getStopPosition().getPositionLat() + "," + p.getStops().get(i).getStopPosition().getPositionLon());
                            }
                        }
                    }
                }
                Start = start;
                Log.i("func start", start);
                Dir = dir;
                //列出下車站牌有哪些路線的公車
                RouteOfStation(endLat, endLon, 50, 1);
                Log.d("loc", "現在執行結束的函式是= SameDestinationRoute");
            }

            @Override
            public void onFailure(retrofit2.Call<List<StopOfRoute>> call, Throwable t) {
                Log.e("OkHttp", "相同上下車地點的公車路線的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= SameDestinationRoute");
    }


    public void setBusCard(List<String> busArrayList) {
        cardItem.clear();
        if (busArrayList != null) {
            for (int i = 0; i < busArrayList.size(); i++) {
                Log.i("cardRouter", i + " " + busArrayList.get(i));
                cardItem.add(new CardBean(i, busArrayList.get(i)));
            }

            for (int i = 0; i < cardItem.size(); i++) {
                if (cardItem.get(i).getCardNo().length() > 6) {
                    String str_item = cardItem.get(i).getCardNo();
                    cardItem.get(i).setCardNo(str_item);
                }
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        Toast.makeText(FinalSetRouterActivity.this, "Begin Parse Data", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FinalSetRouterActivity.this, "Parse Succeed", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(FinalSetRouterActivity.this, "Parse Failed", Toast.LENGTH_SHORT).show();
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
    //------------------------------------------------------------------------------
    //用來預約公車司機

    private void Resevation(final String dir) {
        Log.e("loc", "現在執行的函式是= RealTimeNearStop");
        Data = "RealTimeNearStop";
        City_a = "Taipei";
        Query = "?$filter=Direction%20eq%20'" + dir + "'&$orderby=StopSequence%20%20asc&$top=30&$format=JSON";
        Log.e("Query", Query);

        GetRealTimeNearStop GetRealTimeNearStop = AppClientManager.getClient().create(GetRealTimeNearStop.class);
        GetRealTimeNearStop.GetRealTimeNearStop(Data, City_a, Route, Query).enqueue(new retrofit2.Callback<List<RealTimeNearStop>>() {
            @Override
            public void onResponse(retrofit2.Call<List<RealTimeNearStop>> call, retrofit2.Response<List<RealTimeNearStop>> response) {
                Log.e("OkHttp", "RealTimeNearStop response = " + response.body().toString());
                List<RealTimeNearStop> list = response.body();
                String theclostBus = "";
                if (!list.isEmpty()) {
                    for (RealTimeNearStop p : list) {
                        if (p.getStopSequence() < StopName1Squence) {
                            theclostBus = p.getPlateNumb();
                        }
                    }
                }
                Log.e("theclostBus", theclostBus);

                //--------------VVVVVVVVVVVVVVVVVVV----------------
                //做上傳預約資訊動作 theclostBus->公車車牌


            }

            @Override
            public void onFailure(retrofit2.Call<List<RealTimeNearStop>> call, Throwable t) {
                Log.e("OkHttp", "目前哪一台公車最快來的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= RealTimeNearStop");
    }

    //-----------------------------------------------------
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
    private void getCardData() {
        getRouter_name();
        cardItem.clear();
        if (getRouterNameArr != null) {
            for (int i = 0; i < getRouterNameArr.size(); i++) {
                Log.i("cardRouter", i + " " + getRouterNameArr.get(i));
                cardItem.add(new CardBean(i, getRouterNameArr.get(i)));
            }

            for (int i = 0; i < cardItem.size(); i++) {
                if (cardItem.get(i).getCardNo().length() > 6) {
                    String str_item = cardItem.get(i).getCardNo();
                    cardItem.get(i).setCardNo(str_item);
                }
            }
        }

    }
    private void getRouter_name() {
        request = new StringRequest(com.android.volley.Request.Method.POST, ROUTER_NAME_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getJSONArray("router") != null) {
                        Log.i("router name test", jsonObject.getJSONArray("router").toString());
                        JSONArray routerArr = new JSONArray();
                        routerArr = jsonObject.getJSONArray("router");
                        getRouterNameArr = new ArrayList<>();
                        for (int i = 0; i < routerArr.length(); i++) {
                            getRouterNameArr.add(routerArr.get(i).toString().replace(".txt", ""));
                        }
                        Log.i("router name test2", getRouterNameArr + "");
                    } else {
                        Toast.makeText(getApplicationContext(), "NULL!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("error", e.toString());
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error2", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> Map = new HashMap<String, String>();
                Map.put("ID", personName);
                return Map;
            }
        };
        Log.i("test", "request:" + requestQueue.toString());
        requestQueue.add(request);

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
                if (CARDSTATE == STATE_DOWNLOAD) {

                    //btn_CustomOptions.setText(tx);
                }


                switch (CARDSTATE) {
                    case STATE_BUS_NUM:
                        tmpTX = tx;
                        StopOfRoute(tx, "0", 0);
                        //pvCustomOptions.setPicker(cardItem);
                        break;
                    case STATE_BUS_STOP:
                        Log.i("test same", "1:" + tmpTX + "name1" + StopNameStart + " name2:" + tx);
                        endLatLng = new LatLng(endLat, endLon);

                        //  SameDestinationRoute(tmpTX, "0", StopNameStart, tx);
                        //vv-----------測試--------------------------
                        Toast.makeText(FinalSetRouterActivity.this, "成功選擇:" + tx, Toast.LENGTH_LONG).show();
                        endLatLng = new LatLng(endLat, endLon);
                        //-------------------新增marker--------------
                        if (points_state.get(points_state.size() - 1) == 2) {
                            points.add(startLatLng);
                            points_state.add(2);
                            points.add(endLatLng);
                            points_state.add(2);

                            MarkerOptions tmpMarker = new MarkerOptions()
                                    .title(StopNameStart +"_"+tmpTX)
                                    .snippet("bus_stop")
                                    .position(startLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);
                            tmpMarker = new MarkerOptions()
                                    .title(tx+"_"+tmpTX)
                                    .snippet("bus_stop")
                                    .position(endLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);
                        } else {
                            points.add(startLatLng);
                            points_state.add(1);
                            points.add(endLatLng);
                            points_state.add(2);

                            MarkerOptions tmpMarker = new MarkerOptions()
                                    .title(StopNameStart +"_"+tmpTX)
                                    .snippet("bus_stop")
                                    .position(startLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);
                            tmpMarker = new MarkerOptions()
                                    .title(tx +"_"+tmpTX)
                                    .snippet("bus_stop")
                                    .position(endLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);

                        }
                        getDirection(firstLocation, points.get(points.size() - 1));
                        //--------------------
                        CARDSTATE = STATE_BUS_NUM;

                        break;
                    case STATE_BUS_SAME:
                        Toast.makeText(FinalSetRouterActivity.this, "成功選擇:" + tx, Toast.LENGTH_LONG).show();
                        endLatLng = new LatLng(endLat, endLon);
                        //-------------------新增marker--------------
                        if (points_state.get(points_state.size() - 1) == 2) {
                            points.add(startLatLng);
                            points_state.add(2);
                            points.add(endLatLng);
                            points_state.add(2);

                            MarkerOptions tmpMarker = new MarkerOptions()
                                    .title(busStopstart +"_"+tx)
                                    .snippet("bus_stop")
                                    .position(startLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);
                            tmpMarker = new MarkerOptions()
                                    .title(StopNameStart+"_"+tx)
                                    .snippet("bus_stop")
                                    .position(endLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);
                        } else {
                            points.add(startLatLng);
                            points_state.add(1);
                            points.add(endLatLng);
                            points_state.add(2);

                            MarkerOptions tmpMarker = new MarkerOptions()
                                    .title(busStopstart +"_"+tx)
                                    .snippet("bus_stop")
                                    .position(startLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);
                            tmpMarker = new MarkerOptions()
                                    .title(StopNameStart +"_"+tx)
                                    .snippet("bus_stop")
                                    .position(endLatLng)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("stophand_round",100,100)));
                            mMap.addMarker(tmpMarker);
                            busMarkerArrayList.add(tmpMarker);





                        }
                        getDirection(firstLocation, points.get(points.size() - 1));
                        //--------------------
                        CARDSTATE = STATE_BUS_NUM;
                        break;
                    case STATE_DOWNLOAD:
                        tmpTX = tx;
                        break;
                    default:
                        CARDSTATE = STATE_BUS_NUM;
                        break;
                }
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
                                CARDSTATE = STATE_BUS_NUM;
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

    private LatLng convertAddressToLanLng(String address){
        LatLng tmpLatLng;
        try {
            addressLatLng = geocoder.getFromLocationName(address, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressLatLng != null && addressLatLng.size() > 0) {
            tmpLatLng= new LatLng((addressLatLng.get(0).getLatitude()),(addressLatLng.get(0).getLongitude()));

            txtLat.setText(tmpLatLng.latitude+"");
            txtLng.setText(tmpLatLng.longitude+"");
            return tmpLatLng;
        }else {
            return null;
        }
    }
    private String convertLanLngToAddress(LatLng tmpLatLng){
        try {
            addressName = geocoder.getFromLocation(tmpLatLng.latitude, tmpLatLng.longitude, 1); //放入座標
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressName != null && addressName.size() > 0) {
            Address address = addressName.get(0);
            String addressText = String.format("%s-%s%s%s%s",
                    address.getCountryName(), //國家
                    address.getAdminArea(), //城市
                    address.getLocality(), //區
                    address.getThoroughfare(), //路
                    address.getSubThoroughfare() //巷號
            );
            return addressText;
        }else{
            return null;
        }
    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


//導航
    public void router_action() {
        if (nowPoint != null) {
            float distance = getDistance(currentLocation, arraySteps.get(nowPoint[0]).get(nowPoint[1]).get(nowPoint[2]));
            if (distance < 5) {
                if (arraySteps.get(nowPoint[0]).get(nowPoint[1]).size() == nowPoint[2] + 1) {
                    nowPoint[2] = 0;
                    if (arraySteps.get(nowPoint[0]).size() == nowPoint[1] + 1) {
                        if (points.size() == nowPoint[0] + 1) {
                            //Toast.makeText(MapsActivity.this, "結束導航", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(MapsActivity.this, "下個路徑點(大)", Toast.LENGTH_SHORT).show();
                            nowPoint[0]++;
                        }

                    } else {
                        //Toast.makeText(MapsActivity.this, "下個路徑點(小)", Toast.LENGTH_SHORT).show();
                        nowPoint[1]++;
                    }
                } else {
                    //Toast.makeText(MapsActivity.this, "下個點", Toast.LENGTH_SHORT).show();
                    nowPoint[2]++;
                }
                //Toast.makeText(MapsActivity.this, "點距" + distance, Toast.LENGTH_SHORT).show();
            } else if (distance < 10) {
                //Toast.makeText(MapsActivity.this, "點距" + distance, Toast.LENGTH_SHORT).show();
            } else if (distance < 15) {
                //Toast.makeText(MapsActivity.this, "點距" + distance, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(MapsActivity.this, "點距" + distance, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
