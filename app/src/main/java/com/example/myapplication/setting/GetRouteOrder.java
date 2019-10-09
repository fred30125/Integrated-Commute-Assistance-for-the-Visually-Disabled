package com.example.myapplication.setting;

import com.example.myapplication.setting.DisplayStopOfRoute.DisplayStopOfRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetRouteOrder {
    @GET("/track?")
    Call<List<DisplayStopOfRoute>> GetRouteorder(@Query("data") String Data, @Query("city") String City, @Query("query") String Query);

}
