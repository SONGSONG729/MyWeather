package com.example.myweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.Utility;

import java.io.IOException;

import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;  //气温
    private TextView weatherInfoText;  //天气概况
    private LinearLayout forecastLayout;
    private TextView humText;
    private TextView cloudText;
    private TextView comfortText;
    private TextView clothWareText;
    private TextView sportText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        //定义缓存对象
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
        else {
            //无缓存时去服务器查询天气信息
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);  // 隐藏ScrollView
            requestWeather(weatherId);
        }
    }


    /*根据天气ID请求天气信息*/
    private void requestWeather(final String weatherId) {
        //TODO
        String weatherUrl ="https://free-api.heweather.net/s6/weather?location="+
                weatherId+"&key=35ffed71cf1e41b689ad8151471f3c36";
        Log.i("url",weatherUrl);
        HttpUtil.sendOkHttpRequests(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"从网上获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.getStatus())) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                        else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //缓存数据下处理并展示Weather实体类中的数据
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.getBasic().getLocation();
        String updateTime = weather.getUpdate().getLoc().split(" ")[1];
        String degree = weather.getNow().getTmp();
        String weatherInfo = weather.getNow().getCond_txt();
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (ForecastBase forecast : weather.getDaily_forecast()){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.getDate());
            Log.i("date:",forecast.getDate());
            infoText.setText(forecast.getCond_txt_d());
            Log.i("date:",forecast.getCond_txt_d());
            maxText.setText(forecast.getTmp_max());
            Log.i("date:",forecast.getTmp_max());
            minText.setText(forecast.getTmp_min());
            Log.i("date:",forecast.getTmp_min());
            forecastLayout.addView(view);
        }

        humText.setText(weather.getNow().getHum());
        cloudText.setText(weather.getNow().getCloud());
        String comfort = "舒适度：" +weather.getLifestyle().get(0).getTxt();
        String carWash = " 穿衣指数：" +weather.getLifestyle().get(1).getTxt();
        String sport = "运动建议：" +weather.getLifestyle().get(4).getTxt();
        Log.i("com:",comfort+carWash+sport);
        comfortText.setText(comfort);
        clothWareText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);  //ScrollView设为可见
    }

    //初始化控件
    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        humText = (TextView)findViewById(R.id.hum_text);
        cloudText = (TextView)findViewById(R.id.cloud_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        clothWareText = (TextView) findViewById(R.id.cloth_ware_text);
        sportText = (TextView)findViewById(R.id.sport_text);
    }
}
