package com.example.myweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.myweather.db.City;
import com.example.myweather.db.County;
import com.example.myweather.db.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;

/**
 * 处理获取到的Json数据
 */
public class Utility {
    /**
     * 解析和处理返回的省数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){  //如果返回的数据不为空
            try {
                //将所有的省级数据解析出来，并组装成实体类对像
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //将该实体类对象存入数据库
                    province.save();
                }
                return true;//解析成功
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;//解析失败
    }

    /*解析和处理服务器返回的市级数据 */
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);  //所属的省级id
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*解析和处理服务器返回的县级数据 */
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountries = new JSONArray(response);
                for (int i=0;i<allCountries.length();i++){
                    JSONObject countryObject = allCountries.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countryObject.getString("name"));
                    //县级天气信息
                    county.setWeatherId(countryObject.getString("weather_id"));
                    //所属的市级代号
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*将返回的JSON数据解析成Weather实体类*/
    public static Weather handleWeatherResponse(String response){
        try {
            //通过JSONObject和JSONArray将天气数据中的主体内容解析出来
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");

            //打印gson数据
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.i("gson",weatherContent);
            //将JSON数据转换成Weather对象
            return new Gson().fromJson(weatherContent, interfaces.heweather.com.interfacesmodule.bean.weather.Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static NowBase handleNowBaseResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("now");
            String nowContent = jsonArray.getJSONObject(0).toString();

            return new Gson().fromJson(nowContent, NowBase.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }*/
}
