package com.example.myweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    /**
     * 记录省
     */
    private int id;  //id
    private String provinceName;  //名字
    private int provinceCode;  //代号
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getProvinceName() {
        return provinceName;
    }
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    public int getProvinceCode() {
        return provinceCode;
    }
    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

}

