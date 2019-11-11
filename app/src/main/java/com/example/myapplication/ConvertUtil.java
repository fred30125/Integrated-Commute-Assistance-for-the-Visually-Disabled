package com.example.myapplication;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class ConvertUtil extends Activity {
    // 根据地址获取对应的经纬度


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        TextView tvAddress = (TextView) findViewById(R.id.testAddress);

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;
        List<Address> addressList=null;
        double lat = 25.035219;
        double lon = 121.386183;
        try {
            addressList = geocoder.getFromLocationName(
                    "長庚大學", 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null && addressList.size() > 0) {
            lat = (addressList.get(0).getLatitude());
            lon = (addressList.get(0).getLongitude());

            tvAddress.setText("長庚大學:"+tvAddress.getText()+"獲取位置:"+lat+","+lon+"\n");
        }
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); //放入座標
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressText = String.format("%s-%s%s%s%s",
                    address.getCountryName(), //國家
                    address.getAdminArea(), //城市
                    address.getLocality(), //區
                    address.getThoroughfare(), //路
                    address.getSubThoroughfare() //巷號
            );

            tvAddress.setText(tvAddress.getText()+addressText);
        }




    }




}
