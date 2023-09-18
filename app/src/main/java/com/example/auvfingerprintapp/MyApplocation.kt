package com.example.auvfingerprintapp

import android.app.Application
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService
import okhttp3.internal.http.HttpHeaders

class MyApplocation : Application() {
    override fun onCreate() {
        super.onCreate()
        initOkGo()
        MMKV.initialize(this)
        MMKV.setLogLevel(MMKVLogLevel.LevelInfo)
        //ZKFingerService.init()
    }

    private fun initOkGo() {
        OkGo.getInstance().init(this)
    }
}