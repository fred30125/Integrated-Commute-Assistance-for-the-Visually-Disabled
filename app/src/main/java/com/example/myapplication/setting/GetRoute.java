package com.example.myapplication.setting;

import com.example.myapplication.setting.Gson_StopOrder.BusRouteOrder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRoute {
    @GET("/track?")
    Call<List<BusRouteOrder>> GetRouteorder(@Query("data") String Data, @Query("city") String City, @Query("route") String Route, @Query("query") String Query);

}

