package com.ssafy.cafe_in_port_client.base

import android.Manifest
import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.cafe_in_port_client.base.interceptors.AddCookiesInterceptor
import com.ssafy.cafe_in_port_client.base.interceptors.ReceivedCookiesInterceptor
import com.ssafy.cafe_in_port_client.common.Constants.OPENAI_API_KEY
import com.ssafy.cafe_in_port_client.common.Constants.OPENAI_URL
import com.ssafy.cafe_in_port_client.common.Constants.SERVER_URL
import com.ssafy.cafe_in_port_client.data.local.SharedPreferencesUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ApplicationClass_싸피"

class ApplicationClass : Application() {
    companion object {
        // ipconfig를 통해 ip확인하기
        // 핸드폰으로 접속은 같은 인터넷으로 연결 되어있어야함 (유,무선)
//        const val SERVER_URL = "http://192.168.33.33:9987/"


        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit
        lateinit var retrofitForAi: Retrofit

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.CALL_PHONE,
        )

        // 주문 준비 완료 확인 시간 1분
        const val ORDER_PROCESS_TIME = 1 * 60 * 1000
        const val ORDER_COMPLETE_TIME = 5 * 60 * 1000

    }


    override fun onCreate() {
        super.onCreate()

        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
        // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AddCookiesInterceptor())
            .addInterceptor(ReceivedCookiesInterceptor()).build()

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${OPENAI_API_KEY}")
                .build()
            chain.proceed(request)
        }

        val openAiClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        retrofitForAi = Retrofit.Builder()
            .baseUrl(OPENAI_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(openAiClient)
            .build()
    }

    //GSon은 엄격한 json type을 요구하는데, 느슨하게 하기 위한 설정. success, fail이 json이 아니라 단순 문자열로 리턴될 경우 처리..
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
}