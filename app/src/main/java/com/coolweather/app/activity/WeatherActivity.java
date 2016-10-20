package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;


/**
 * Created by WANGWEIRAN on 2016/10/20.
 */

public class WeatherActivity extends Activity implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    /**
     * 用于显示天气描述信息
     */
    private TextView weatherDespText;
    /**
     * 用于显示气温1
     */
    private TextView temp1Text;
    /**
     * 用于显示气温2
     */
    private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;
    /**
     * 切换城市按钮
     */
    private Button switchCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;


    public static final String TAG = "WeatherActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("同步中。");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    /**
     * 查询县级代号所对应的天气代号
     *
     * @param countyCode 县级代号
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        Log.d(TAG, "queryWeatherCode: 这里是通过Code来查询具体的天气情况，这里的Code是：" + countyCode);
        Log.d(TAG, "queryWeatherCode: 网络查询的地址address是：" + address);
        queryFromServer(address, "countyCode");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中。。");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = sharedPreferences.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询编号所对应的天气
     *
     * @param weatherCode 天气代号
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        Log.d(TAG, "queryWeatherInfo: address的内容是：" + address);
        queryFromServer(address, "weatherCode");
        Log.d(TAG, "queryWeatherInfo: 传入地址，来分析内容是：" + address);
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                Log.d(TAG, "onFinish: 这里是回调的函数，看response：" + response);
                Log.d(TAG, "onFinish: 这里是回调的函数，看type：" + type);
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
//                            Log.d(TAG, "onFinish: array不为0，且长度为2");
                            String weatherCode = array[1];
                            Log.d(TAG, "onFinish: weatherCode = array[1]\n" + array[0] + ":" + array[1]);
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Log.d(TAG, "onFinish: 这里测试天气代码进入之后" + response);
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("city_name", ""));
        temp1Text.setText(sharedPreferences.getString("temp1", ""));
        temp2Text.setText(sharedPreferences.getString("temp2", ""));
        weatherDespText.setText(sharedPreferences.getString("weather_desp", ""));
        publishText.setText("今天" + sharedPreferences.getString("publish_time", "") + "发布");
        currentDateText.setText(sharedPreferences.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }


}
