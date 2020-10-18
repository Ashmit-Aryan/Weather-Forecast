package com.ashmitaryan.weatherapplication.Fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ashmitaryan.weatherapplication.Adapter.RecyclerViewAdapter;
import com.ashmitaryan.weatherapplication.Model.Common;
import com.ashmitaryan.weatherapplication.Model.MyList;
import com.ashmitaryan.weatherapplication.Model.WeatherForecastResult;
import com.ashmitaryan.weatherapplication.R;
import com.ashmitaryan.weatherapplication.Retrofit.RetrofitClient;
import com.ashmitaryan.weatherapplication.Retrofit.WeatherNetworkCall;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class HourlyForecast extends Fragment {
    private CompositeDisposable disposable;
    private WeatherNetworkCall weatherNetworkCall;
    private static HourlyForecast instance;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private ProgressBar bar;

    public static HourlyForecast getInstance() {
        if(instance == null){
            instance = new HourlyForecast();
        }
        return instance;
    }

    public HourlyForecast() {
        disposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        weatherNetworkCall = retrofit.create(WeatherNetworkCall.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hourly_forecast, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        toolbar = view.findViewById(R.id.day5_toolbar);
        linearLayout = view.findViewById(R.id.child_view);
        bar = view.findViewById(R.id.progress);
        getWeatherData(view);
        return view;
    }

    private void getWeatherData(View view) {
        disposable.add(weatherNetworkCall.getWeatherForecast(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                "metric", Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherForecastResult -> {
                    toolbar.setTitle(weatherForecastResult.getCity().getName()
                            + ","
                            + weatherForecastResult.getCity().getCountry()
                    );
                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext());
                    adapter.setList(weatherForecastResult.getList());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(adapter);
                    bar.setVisibility(GONE);
                    linearLayout.setVisibility(VISIBLE);
                },throwable -> {
                    Log.d("Fragment", Objects.requireNonNull(throwable.getMessage()));
                    Snackbar.make(view, Objects.requireNonNull(throwable.getLocalizedMessage()),Snackbar.LENGTH_LONG).show();
                    bar.setVisibility(GONE);
                    linearLayout.setVisibility(VISIBLE);
                })
        );
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}