package com.example.androidnetworkdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import com.example.androidnetworkdemo.retrofit.App
import com.example.androidnetworkdemo.retrofit.AppService
import com.example.androidnetworkdemo.retrofit.ServiceCreator
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        initWebView()

        okhttp3()

        val jsonString = JSONObject()
        jsonString.put("name", "fage")
        parseJson(jsonString)

        retrofit()
    }

    // 协程
    suspend fun getAppData() {
        try {
            val appList = ServiceCreator.create<AppService>().getAppData().await()
        } catch (e: Exception) {

        }
    }

    // 非协程
    private fun retrofit() {

        val appService = ServiceCreator.create<AppService>()
        appService.getAppData().enqueue(object : Callback<List<App>> {
            override fun onFailure(call: Call<List<App>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<List<App>>, response: Response<List<App>>) {
                val list = response.body()
                if (list != null) {
                    for (app in list) {
                        Log.d("tag", app.id + app.name + app.version)
                    }
                }
            }
        })
    }

    private fun parseJson(jsonString: JSONObject) {

    }

    private fun okhttp3() {
        sendRequestBtn.setOnClickListener {
            thread {
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://www.baidu.com")
                        .build()
                    val response = client.newCall(request).execute()
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        showResponse(responseData)
                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
//        mWebView.settings.javaScriptEnabled = true
//        mWebView.webViewClient = WebViewClient()
//        mWebView.loadUrl("https://www.baidu.com")

        // Send Request
        sendRequestBtn.setOnClickListener {
            thread {
                var connection: HttpURLConnection? = null
                try {
                    val response = StringBuilder()
                    val url = URL("https://www.baidu.com")
                    connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    val input = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(input))
                    reader.use {
                        reader.forEachLine {
                            response.append(it)
                        }
                    }
                    showResponse(response.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection?.disconnect()
                }

            }
        }
    }

    private fun showResponse(toString: String) {
        runOnUiThread {
            responseTv.text = toString
        }
    }
}