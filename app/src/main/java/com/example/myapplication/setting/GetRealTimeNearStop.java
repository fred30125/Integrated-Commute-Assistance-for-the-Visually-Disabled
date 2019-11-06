package com.example.myapplication.setting;

import com.example.myapplication.setting.RealTimeNearStop.RealTimeNearStop;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRealTimeNearStop {
    @GET("/track?")
    Call<List<RealTimeNearStop>> GetRealTimeNearStop(@Query("data") String Data, @Query("city") String City, @Query("route") String Route, @Query("query") String Query);

}
