package com.example.auvfingerprintapp

import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.auvfingerprintapp.config.API_HOST
import com.tencent.mmkv.MMKV
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.util.*

class SettingActivity : AppCompatActivity() {

    var setting_api_edt :EditText? = null
    var iv_back :ImageView? = null
    var setting_btn_save :Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        hideBottomUIMenu()
        initView()
    }

    private fun initView() {
        val settingApi = MMKV.defaultMMKV().getString(API_HOST, "http://jurong_gongan.58auv.com")
        setting_btn_save = findViewById(R.id.setting_btn_save)
        iv_back = findViewById(R.id.iv_back)
        setting_api_edt = findViewById(R.id.setting_api_edt)
        setting_api_edt?.setText(settingApi)
        iv_back?.setOnClickListener(View.OnClickListener {
            finish()
        })
        setting_btn_save?.setOnClickListener{
            saveSettingApi()
        }

        KeyboardVisibilityEvent.setEventListener(this) {
            hideBottomUIMenu()
        };


    }
    fun saveSettingApi(){
        val settingApi = setting_api_edt?.text.toString()
        if (!settingApi.isNullOrEmpty()){
            MMKV.defaultMMKV().putString(API_HOST,settingApi)
            Toast.makeText(this,"保存成功",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"Api地址不能为空",Toast.LENGTH_SHORT).show()
        }

    }


    private fun hideBottomUIMenu() {
        val actionBar = supportActionBar
        actionBar?.hide()
        fullscreen(false)
        //隐藏底部导航栏
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else {
            //for new api versions.
            val decorView = window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }
    }

    fun fullscreen(enable: Boolean) {
        if (enable) { //显示状态栏
            val lp = window.attributes
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = lp
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else { //隐藏状态栏
            val lp = window.attributes
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = lp
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

}