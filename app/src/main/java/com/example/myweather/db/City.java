package com.example.myweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    /**
     * 记录市
     */
    private int id;  //id
    private String cityName;  //市名
    private int cityCode;  //代号
    private int provinceId;  //所属省的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
