package com.example.myweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweather.db.City;
import com.example.myweather.db.County;
import com.example.myweather.db.Province;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 选择省市县
 */
public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;  //省列表
    private List<City> cityList;  //市列表
    private List<County> countyList;  //县列表

    private Province selectedProvince;  //当前被选中的级别
    private City selectedCity;  //当前被选中的城市
    private int currentLevel;  //当前被选中的级别


    /**
     * 初始化视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("ChooseAreaFragment","onCreateView");
        View view = inflater.inflate(R.layout.choose_area,container,false);

        //获取控件实例
        titleText = (TextView)view.findViewById(R.id.title_text);  //获取标题栏文本id
        backButton = (Button) view.findViewById(R.id.back_button);  //获取标题栏id
        listView = (ListView)view.findViewById(R.id.list_view);    //获取Item列表id

        //获取ArrayAdapter对象
        adapter =new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);//设置并初始化适配器
        return view;//将视图返回
    }

    /**
     * 设置点击事件
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("ChooseAreaFragment","onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        //设置listView的点击事件，列表任意一栏被点击，则...
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ChooseAreaFragment","列表被点了的...");
                if (currentLevel == LEVEL_PROVINCE){   //当前选中的级别为省份时
                    selectedProvince = provinceList.get(position);  //当前点击为选中状态
                    queryCities();//查询市的方法
                }
                else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCountries();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询省，优先从数据库查询，没有再到服务器查询
     */
    private void queryProvinces() {
        titleText.setText("中国");  //设置标题内容为中国
        backButton.setVisibility(View.GONE);  //设置返回按钮不可见
        //查询被选中的省份
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0){  //如果列表为空
            dataList.clear();
            for (Province province: provinceList){  //遍历每一份省级数据
                dataList.add(province.getProvinceName());  //添加到数据列表中

            }
            adapter.notifyDataSetChanged();  //通知适配器更新了
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;  //设置当前级别
        }else{
            String address ="http://guolin.tech/api/china";  //获取查询地址
            queryFromServer(address,"province");  //从网络中查询
        }
    }

    /**
     * 查询选中的省内的所有市。。。。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());  //设置标题为该省
        backButton.setVisibility(View.VISIBLE);  //返回按钮可见
        //查询被选中的省份的市
        cityList=DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId())).find(City.class  );

        if(cityList.size()>0) {  //如果市列表不为空
            dataList.clear();
            for (City city : cityList) {  //遍历城市
                dataList.add(city.getCityName());  //将数据添加到列表中
            }
            adapter.notifyDataSetChanged();  //通知适配器更新
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else{
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;  //设置请求网址
            queryFromServer(address,"city");  //
        }
    }

    /**
     * 查询当前选中的市的县。。。。
     */
    private void queryCountries(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList =DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int  provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode() ;
            String address="http://guolin.tech/api/china/"+provinceCode + "/" + cityCode;
            queryFromServer(address,"country");
        }
    }

    /**
     * 从服务器上查询
     * @param address  服务器地址
     * @param type  类型
     */
    private void queryFromServer(String address,final String type){
        showProgressDialog();  //显示进度
        //发送请求
        HttpUtil.sendOkHttpRequests(address, new Callback() {
            /**
             * 请求加载失败
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread方法从子线程切换到主线程逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();

                    }
                });

            }
            /**
             * 响应的数据回调到该方法中
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText= response.body().string();
                boolean result = false;
                //解析和处理服务器返回的数据，并存储到数据库中
                if ("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }
                else if ("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("country".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());}
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }
            }

        });
    }

    /**
     * 显示进度
     */
    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度
     */
    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }



}
