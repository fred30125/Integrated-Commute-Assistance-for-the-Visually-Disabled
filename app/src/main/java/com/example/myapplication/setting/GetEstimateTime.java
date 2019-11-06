package com.example.myapplication.setting;

import com.example.myapplication.setting.EstimateTime.EsimateTime;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface GetEstimateTime {
    @GET("/track?")
    Call<List<EsimateTime>> GetEstimateTime(@Query("data") String Data, @Query("city") String City, @Query("route") String Route, @Query("query") String Query);
}
