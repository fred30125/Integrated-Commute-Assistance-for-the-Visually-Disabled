package com.example.myapplication.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.setting.DisplayStopOfRoute.DisplayStopOfRoute;
import com.example.myapplication.setting.Gson_StopOrder.BusRouteOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class GetStopDataUtil {
    String Data = "";
    String City_a = "Taipei";
    String Query = "";
    LinkedList<HashMap<String, String>> routeList = new LinkedList<>();
    List<String> routeOrder = new ArrayList<String>();
    List<String> SameDestanationRouteList = new ArrayList<String>();
    //----------------------------Test Data------------------------------
    String StopName1 = "歡仔園"; //上車
    String StopName2 = "僑中一街";  //下車站牌
    //----------------------------Test Data------------------------------
    //List<String> RouteNumbList = new ArrayList<String>();



//    排序用 先setStopName1再呼叫
    public void RouteOrder() {
        Log.e("loc", "現在執行的函式是= route_order");
        Data = "DisplayStopOfRoute";
        City_a = "Taipei";
        Query = "?$top=1000&$format=JSON";
        routeList=new LinkedList<>();
        GetRouteOrder GetRouteOrder = AppClientManager.getClient().create(GetRouteOrder.class);
        GetRouteOrder.GetRouteorder(Data, City_a, Query).enqueue(new Callback<List<DisplayStopOfRoute>>() {
            @Override
            public void onResponse(Call<List<DisplayStopOfRoute>> call, retrofit2.Response<List<DisplayStopOfRoute>> response) {
                Log.e("OkHttp", "車牌順序成功了啦 response = " + response.body().toString());
                List<DisplayStopOfRoute> list = response.body();

                for (DisplayStopOfRoute p : list) {
                    // Log.e("RouteNumbList", p.getRouteName().getZhTw()+"="+p.getStops().size());

                    for (int i = 0; i < p.getStops().size(); i++) {
                        HashMap<String, String> RouteNumbList = new HashMap<>();
                        if (p.getStops().get(i).getStopName().getZhTw().equals(StopName1)) { //放公車站名
                            RouteNumbList.put("RouteNumb", p.getRouteName().getZhTw());// 站牌抓有的公車號碼 RouteNumbList
                            RouteNumbList.put("Direction", p.getDirection().toString());
                            Log.e("RouteNumbList", p.getRouteName().getZhTw());
                            Log.e("RouteNumbList", p.getDirection().toString());
                            routeList.add(RouteNumbList);
                        }

                    }
                }
                Log.d("loc", "現在執行結束的函式是= route_order");
                Log.d("loc123123", routeList.size()+"");
                for (int i = 0; i < routeList.size(); i++) {
                    Log.e("routeList", "路線: " + routeList.get(i).get("RouteNumb") + "方向: " + routeList.get(i).get("Direction"));
                  /*  Route(routeList.get(i).get("RouteNumb"), routeList.get(i).get("Direction"));
                    SameDestanationRoute(routeList.get(i).get("RouteNumb"), routeList.get(i).get("Direction"));*/
                }
            }

            @Override
            public void onFailure(Call<List<DisplayStopOfRoute>> call, Throwable t) {
                Log.e("OkHttp", "車牌順序的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= route_order");
    }


//    用來當順序
//    不用改
    public void Route(String RouteNumb, final String Direction) { //輸入公車號碼+方向->找到該路線的站牌資料 routeOrder
        Log.e("loc", "現在執行的函式是= route_order");
        Data = "StopOfRoute";
        City_a = "Taipei";
        Query = "?$top=2";
        final String route = RouteNumb;
        final String direction = Direction;
        GetRoute GetRoute = AppClientManager.getClient().create(GetRoute.class);
        GetRoute.GetRouteorder(Data, City_a, route, Query).enqueue(new Callback<List<BusRouteOrder>>() {
            @Override
            public void onResponse(Call<List<BusRouteOrder>> call, retrofit2.Response<List<BusRouteOrder>> response) {
                Log.e("OkHttp", "車牌順序成功了啦 response = " + response.body().toString());
                List<BusRouteOrder> list = response.body();
                for (BusRouteOrder p : list) {
                    if (p.getDirection().toString().equals(direction)) {//dep
                        for (int i = 0; i < p.getStopOrders().size(); i++) {
                            routeOrder.add(i, p.getStopOrders().get(i).getStopName().getZhTw()); //
                            Log.e("列出下車站牌", routeOrder.get(i));
                        }
                    }
                }
                Log.d("loc", "現在執行結束的函式是= route_order");
            }

            @Override
            public void onFailure(Call<List<BusRouteOrder>> call, Throwable t) {
                Log.e("OkHttp", "車牌順序的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= route_order");
    }

    //找同一個下車站牌
    public void SameDestanationRoute(String RouteNumb, final String Direction) {// 1211          1回..0去
        Log.e("loc", "現在執行的函式是= route_order");
        Data = "StopOfRoute";
        City_a = "Taipei";
        Query = "?$top=2";
        final String route = RouteNumb;
        final String direction = Direction;
        GetRoute GetRoute = AppClientManager.getClient().create(GetRoute.class);
        GetRoute.GetRouteorder(Data, City_a, route, Query).enqueue(new Callback<List<BusRouteOrder>>() {
            @Override
            public void onResponse(Call<List<BusRouteOrder>> call, retrofit2.Response<List<BusRouteOrder>> response) {
                Log.e("OkHttp", "車牌順序成功了啦 response = " + response.body().toString());
                List<BusRouteOrder> list = response.body();
                for (BusRouteOrder p : list) {
                    if (p.getDirection().toString().equals(direction)) {//dep
                        for (int i = 0; i < p.getStopOrders().size(); i++) {
                            if (p.getStopOrders().get(i).getStopName().getZhTw().equals(StopName1)) {
                                SameDestanationRouteList.add(route);
                                Log.d("SameDestanationRoute", "列出相同的路線: " + route);
                            }
                        }
                    }
                }
                Log.d("loc", "現在執行結束的函式是= route_order");
            }

            @Override
            public void onFailure(Call<List<BusRouteOrder>> call, Throwable t) {
                Log.e("OkHttp", "車牌順序的資料連接，怎麼會失敗");
            }
        });
        Log.d("loc", "現在執行結束的函式是= route_order");
    }

    public void setStopName1(String stopName1) {
        StopName1 = stopName1;
    }
    public void setStopName2(String stopName2) {
        StopName2 = stopName2;
    }
    //TODO:getRouteList 注意return值 之後記得改.
    public LinkedList<HashMap<String, String>> getRouteList(){
        return routeList;
    }
}
