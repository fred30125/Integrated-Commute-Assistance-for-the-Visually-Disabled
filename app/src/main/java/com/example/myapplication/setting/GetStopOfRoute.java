package com.example.myapplication.setting;

import com.example.myapplication.setting.StopOfRoute.StopOfRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetStopOfRoute {
    @GET("/track?")
    Call<List<StopOfRoute>> GetStopOfRoute(@Query("data") String Data, @Query("city") String City, @Query("route") String Route, @Query("query") String Query);

}
