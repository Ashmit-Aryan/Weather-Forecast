package com.ashmitaryan.weatherapplication.Fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashmitaryan.weatherapplication.Model.Common;
import com.ashmitaryan.weatherapplication.R;
import com.ashmitaryan.weatherapplication.Retrofit.RetrofitClient;
import com.ashmitaryan.weatherapplication.Retrofit.WeatherNetworkCall;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ashmitaryan.weatherapplication.R.id.fragmentRootView;
import static com.ashmitaryan.weatherapplication.R.id.fragment_toolbar;
import static com.ashmitaryan.weatherapplication.R.id.ivWeatherIcon;
import static com.ashmitaryan.weatherapplication.R.id.progress_circular;
import static com.ashmitaryan.weatherapplication.R.id.tvDescription;
import static com.ashmitaryan.weatherapplication.R.id.tvHumidity;
import static com.ashmitaryan.weatherapplication.R.id.tvPressure;
import static com.ashmitaryan.weatherapplication.R.id.tvSunrise;
import static com.ashmitaryan.weatherapplication.R.id.tvSunset;
import static com.ashmitaryan.weatherapplication.R.id.tvTemp_max_Temp_min;
import static com.ashmitaryan.weatherapplication.R.id.tvTemperature;
import static com.ashmitaryan.weatherapplication.R.id.tvWindSpeed;


public class CurrentWeatherFragment extends Fragment {

    private static CurrentWeatherFragment instance;
    private CompositeDisposable disposable;
    private WeatherNetworkCall weatherNetworkCall;
    private RelativeLayout RootView;
    private TextView Temp,Temp_max_min,Pressure,Humidity,WindSpeed,Sunrise,Sunset,Description;
    private Toolbar toolbar;
    private ImageView WeatherIcon;
    private ProgressBar progressBar;

    public static CurrentWeatherFragment getInstance() {
        if (instance == null) {
            instance = new CurrentWeatherFragment();
        }
        return instance;
    }

    public CurrentWeatherFragment(){
        disposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        weatherNetworkCall = retrofit.create(WeatherNetworkCall.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        RootView = view.findViewById(fragmentRootView);
        Temp = view.findViewById(tvTemperature);
        Temp_max_min = view.findViewById(tvTemp_max_Temp_min);
        Pressure = view.findViewById(tvPressure);
        Humidity = view.findViewById(tvHumidity);
        WindSpeed = view.findViewById(tvWindSpeed);
        Sunrise = view.findViewById(tvSunrise);
        Sunset = view.findViewById(tvSunset);
        toolbar = view.findViewById(fragment_toolbar);
        WeatherIcon = view.findViewById(ivWeatherIcon);
        Description = view.findViewById(tvDescription);
        progressBar = view.findViewById(progress_circular);
        getWeather(view);
        return view;
    }
    public void getWeather(View view){
        disposable.add(weatherNetworkCall.getWeatherByLatLon(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                "metric",Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResult -> {

                    Picasso.get()
                            .load("https://openweathermap.org/img/wn/" + weatherResult.getWeather().get(0).getIcon() + "@2x.png")
                            .into(WeatherIcon);
                    Description.setText(new StringBuilder(weatherResult.getWeather().get(0).getMain())
                            .append(" , ")
                            .append(weatherResult.getWeather().get(0).getDescription())
                    );
                    Temp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                            .append(getResources().getString(R.string.celcius))
                    );
                    Temp_max_min.setText(new StringBuilder(getResources().getString(R.string.temperature))
                            .append(weatherResult.getMain().getTemp_min())
                            .append(getResources().getString(R.string.celcius)).append(" / ")
                            .append(weatherResult.getMain().getTemp_max())
                            .append(getResources().getString(R.string.celcius))
                    );
                    Pressure.setText(new StringBuilder(getResources().getString(R.string.pressure)).
                            append(weatherResult.getMain().getPressure())
                            .append(" hpa")
                    );
                    Humidity.setText(new StringBuilder(getResources().getString(R.string.humidity))
                            .append(weatherResult.getMain().getHumidity()).append(" %")
                    );
                    WindSpeed.setText(new StringBuilder(getResources().getString(R.string.wind_speed))
                            .append(weatherResult.getWind().getSpeed()).append(" m/s ")
                            .append("in ")
                            .append(weatherResult.getWind().getDeg())
                            .append("°")
                    );
                    Sunrise.setText(new StringBuilder(getResources().getString(R.string.sunrise))
                            .append(Common.covertUnixToHour(weatherResult.getSys().getSunrise()))
                    );
                    Sunset.setText(new StringBuilder(getResources().getString(R.string.sunset))
                            .append(Common.covertUnixToHour(weatherResult.getSys().getSunset()))
                    );
                    toolbar.setTitle(new StringBuilder(weatherResult.getName()).append(" , ")
                            .append(weatherResult.getSys().getCountry())
                    );
                    toolbar.setSubtitle(new StringBuilder(Common.covertUnixToDate(weatherResult.getDt())));
                    progressBar.setVisibility(GONE);
                    RootView.setVisibility(VISIBLE);

                }, throwable -> {
                    Log.d("Fragment", Objects.requireNonNull(throwable.getMessage()));
                    Snackbar.make(view, Objects.requireNonNull(throwable.getLocalizedMessage()),Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(GONE);
                    RootView.setVisibility(VISIBLE);
                })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}