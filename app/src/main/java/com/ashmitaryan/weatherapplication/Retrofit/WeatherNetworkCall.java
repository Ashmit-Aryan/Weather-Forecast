package com.ashmitaryan.weatherapplication.Retrofit;


import com.ashmitaryan.weatherapplication.Model.WeatherForecastResult;
import com.ashmitaryan.weatherapplication.Model.WeatherResult;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherNetworkCall {

    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLon(@Query("lat") String Latitude,
                                                 @Query("lon") String Longitude,
                                                 @Query("units") String Units,
                                                 @Query("appid") String API_KEY
    );

    @GET("weather")
    Observable<WeatherResult> getWeatherByCity(@Query("q") String CityName,
                                               @Query("units") String Units,
                                               @Query("appid") String API_KEY
    );

    @GET("forecast")
    Observable<WeatherForecastResult> getWeatherForecast(@Query("lat") String Latitude,
                                                         @Query("lon") String longitude,
                                                         @Query("units") String Unit,
                                                         @Query("appid") String API_KEY
    );
}
