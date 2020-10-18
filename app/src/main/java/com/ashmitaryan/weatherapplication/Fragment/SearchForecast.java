package com.ashmitaryan.weatherapplication.Fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashmitaryan.weatherapplication.Model.City;
import com.ashmitaryan.weatherapplication.Model.Common;
import com.ashmitaryan.weatherapplication.R;
import com.ashmitaryan.weatherapplication.Retrofit.RetrofitClient;
import com.ashmitaryan.weatherapplication.Retrofit.WeatherNetworkCall;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ashmitaryan.weatherapplication.R.id.SearchFragmentToolbar;
import static com.ashmitaryan.weatherapplication.R.id.SearchProgressCircular;
import static com.ashmitaryan.weatherapplication.R.id.SearchRootView;
import static com.ashmitaryan.weatherapplication.R.id.Search_ivWeatherIcon;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvDescription;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvHumidity;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvPressure;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvSunrise;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvSunset;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvTemp_max_Temp_min;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvTemperature;
import static com.ashmitaryan.weatherapplication.R.id.Search_tvWindSpeed;
import static com.ashmitaryan.weatherapplication.R.id.etCityName;
import static com.ashmitaryan.weatherapplication.R.id.ivSearchWeather;


public class SearchForecast extends Fragment {

    private static SearchForecast instance;
    private EditText CityName;
    private ImageView SearchWeather, WeatherIcon;
    private RelativeLayout RootView;
    private Toolbar toolbar;
    private TextView Temp,Des,Temp_Max_Min, Pressure, Humidity , WindSpeed, Sunrise,  Sunset;
    private ProgressBar bar;
    private CompositeDisposable disposable;
    private WeatherNetworkCall weatherNetworkCall;


    public static SearchForecast getInstance() {
        if(instance == null){
            instance = new SearchForecast();
        }
        return instance;
    }

    public SearchForecast() {
        disposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        weatherNetworkCall = retrofit.create(WeatherNetworkCall.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_forecast, container, false);
        CityName = view.findViewById(etCityName);
        SearchWeather = view.findViewById(ivSearchWeather);
        RootView = view.findViewById(SearchRootView);
        toolbar = view.findViewById(SearchFragmentToolbar);
        bar = view.findViewById(SearchProgressCircular);
        Temp = view.findViewById(Search_tvTemperature);
        Des = view.findViewById(Search_tvDescription);
        Temp_Max_Min = view.findViewById(Search_tvTemp_max_Temp_min);
        Pressure = view.findViewById(Search_tvPressure);
        Humidity = view.findViewById(Search_tvHumidity);
        WindSpeed = view.findViewById(Search_tvWindSpeed);
        Sunrise = view.findViewById(Search_tvSunrise);
        Sunset = view.findViewById(Search_tvSunset);
        WeatherIcon = view.findViewById(Search_ivWeatherIcon);
        SearchWeather.setOnClickListener(v -> {
            String cityName = CityName.getText().toString();
            bar.setVisibility(VISIBLE);
            if(TextUtils.isEmpty(cityName)){
                bar.setVisibility(GONE);
                CityName.setError("Enter City Name Please");
                CityName.requestFocus();
                return;
            }
            getWeatherData(view, cityName);
        });
        return view;
    }

    public void getWeatherData(View view, String CityName){
        disposable.add(weatherNetworkCall.getWeatherByCity(CityName,
                "metric",Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResult -> {

                    Picasso.get()
                            .load("https://openweathermap.org/img/wn/" + weatherResult.getWeather().get(0).getIcon() + "@2x.png")
                            .into(WeatherIcon);
                    Des.setText(new StringBuilder(weatherResult.getWeather().get(0).getMain())
                            .append(" , ")
                            .append(weatherResult.getWeather().get(0).getDescription())
                    );
                    Temp.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                            .append(getResources().getString(R.string.celcius))
                    );
                    Temp_Max_Min.setText(new StringBuilder(getResources().getString(R.string.temperature))
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
                    bar.setVisibility(GONE);
                    RootView.setVisibility(VISIBLE);

                }, throwable -> {
                    Log.d("Fragment", Objects.requireNonNull(throwable.getMessage()));
                    Snackbar.make(view, Objects.requireNonNull(throwable.getLocalizedMessage()),Snackbar.LENGTH_LONG).show();
                    bar.setVisibility(GONE);
                    RootView.setVisibility(VISIBLE);
                })
        );
    }

}
