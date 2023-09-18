package com.example.auvfingerprintapp

import AddUserInfoBean
import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.auvfingerprintapp.adapter.FingerDataListAdapter
import com.example.auvfingerprintapp.bean.Data
import com.example.auvfingerprintapp.bean.DelUserInfoBean
import com.example.auvfingerprintapp.bean.UserInfoBeanFinger
import com.example.auvfingerprintapp.config.*
import com.example.auvfingerprintapp.view.DelDialog
import com.example.auvfingerprintapp.view.FingerRegisterDialog
import com.example.auvfingerprintapp.view.LoadingDialog
import com.example.auvfingerprintapp.view.showLoadingExt
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import okhttp3.Response
import pub.devrel.easypermissions.EasyPermissions.*
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {

    private val ACTION_USB_PERMISSION = "com.example.auvfingerprintapp.USB_PERMISSION"

    var main_rv : RecyclerView? = null
    var main_edt : EditText ? = null
    var myAdapter : FingerDataListAdapter? = null
    val data  = mutableListOf<Data>()
    var system_setting : TextView? = null
    var refreshLayout : SmartRefreshLayout? = null
    var page : Int = 1 //页数
    var isNoMore = false //是否还有更多数据
    var has_data_lin : RelativeLayout ? = null
    var no_data_lin : LinearLayout ? = null
    var fingerDialog : FingerRegisterDialog? = null
    val loadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()
        initUSBPermission()
        hideBottomUIMenu()
        initview()
        initData()
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

    private fun initPermission() {
        //权限申请
        val hasPermissions = hasPermissions(
            this,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        if (hasPermissions){
            Log.e("TAG","已经申请了权限")
        }else{
            requestPermissions(this,"需要申请权限",1,Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }
    fun initUSBPermission() {
        val usbManager = this.getSystemService(Context.USB_SERVICE) as UsbManager
        for (device in usbManager.deviceList.values) {
            if (device.vendorId == VID && device.productId == PID) {
                val intent = Intent(ACTION_USB_PERMISSION)
                val pendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0)
                usbManager.requestPermission(device, pendingIntent)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    fun initData() {
        getFingerData(1)
    }

    private fun getFingerData(page : Int,search:String = "") {
        loadingDialog.show()
        var url = ""
        try {
             url = MMKV.defaultMMKV().getString(API_HOST, "http://jurong_gongan.58auv.com").toString()
        }catch (e : Exception){
            e.printStackTrace()
        }

        OkGo.get<UserInfoBeanFinger>(url+GET_USERINFO)
            .params("appId","")
            .params("deviceId","")
            .params("page",page)
            .params("updateTime", "1970-01-01 00:00:00")
            .params("searchKey",search)
            .execute(object : AbsCallback<UserInfoBeanFinger>() {
                override fun convertResponse(response: Response?): UserInfoBeanFinger {
                    return Gson().fromJson(response?.body()?.string(),UserInfoBeanFinger::class.java)
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onSuccess(response: com.lzy.okgo.model.Response<UserInfoBeanFinger>?) {
                    if (response?.isSuccessful == true ){
                        Log.i("TAG","onSuccess: ${Gson().toJson(response.body().resultObject.data)}" )
                        if (page==1){
                            data.clear()
                            data.addAll(response.body().resultObject.data)
                            myAdapter?.setList(data)
                            refreshLayout?.finishRefresh(true) //传入false表示刷新失败
                        }else{
                            if (response.body().resultObject.currentPage == response.body().resultObject.totalPage){
                                isNoMore = true
                                data.addAll(response.body().resultObject.data)
                                myAdapter?.setList(data)
                                refreshLayout?.finishLoadMore(true)
                                return
                            }
                            data.addAll(response.body().resultObject.data)
                            myAdapter?.setList(data)
                            refreshLayout?.finishLoadMore(true)
                        }
                    }
                    noDataDeal()

                }

                override fun onError(response: com.lzy.okgo.model.Response<UserInfoBeanFinger>?) {
                    super.onError(response)
                    noDataDeal()
                }

                override fun onFinish() {
                    super.onFinish()
                    loadingDialog.dismiss()

//                    refreshLayout?.finishRefresh(true)
//                    refreshLayout?.finishLoadMore(true)
                }

            })

    }

    private fun initview() {
        main_rv = findViewById(R.id.main_rv)
        main_edt = findViewById(R.id.main_edt)
        refreshLayout = findViewById(R.id.refreshLayout)
        has_data_lin = findViewById(R.id.has_data_lin)
        no_data_lin = findViewById(R.id.no_data_lin)

        main_edt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (main_edt?.text.toString().isNotEmpty()){
                    getFingerData(1,main_edt?.text.toString())
                }else{
                    getFingerData(1)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


        refreshLayout?.setOnRefreshListener { refreshLayout ->
            getFingerData(1)
            page = 1
            isNoMore = false

        }

        refreshLayout?.setOnLoadMoreListener { refreshLayout ->
            if (isNoMore){
                refreshLayout.finishLoadMoreWithNoMoreData()
                return@setOnLoadMoreListener
            }
            page += 1
            getFingerData(page)
        }


        myAdapter = FingerDataListAdapter(R.layout.main_rv_item,data)
        myAdapter?.addChildClickViewIds(R.id.main_rv_item_btn_register,R.id.main_rv_item_btn_del)
        myAdapter?.setOnItemChildClickListener { adapter, view, position ->
            when(view.id){
                R.id.main_rv_item_btn_register -> {
                    fingerDialog = FingerRegisterDialog(this,data[position],true,object : FingerRegisterDialog.OnDialogClickListener{
                        override fun onDialogClick(userInfo: Data, finger: String) {
                            var user = userInfo
                            loadingDialog.show()
                            val url = MMKV.defaultMMKV().getString(API_HOST, "http://jurong_gongan.58auv.com")
                            OkGo.get<AddUserInfoBean>(url+ADD_USERINFO)
                                .params("id", user.id)
                                .params("appId","")
                                .params("deviceId","")
                                .params("imageUrl", user.imageUrl)
                                .params("nickname", user.nickname)
                                .params("mobile", user.mobile)
                                .params("feature","")
                                .params("fingerprint_feature",finger)
                                .execute(object : AbsCallback<AddUserInfoBean>() {
                                    override fun convertResponse(response: Response?): AddUserInfoBean {
                                        return Gson().fromJson(response?.body()?.string(),AddUserInfoBean::class.java)
                                    }

                                    override fun onSuccess(response: com.lzy.okgo.model.Response<AddUserInfoBean>?) {
                                        Log.i("TAG", "onSuccess: ")
                                        user.fingerprint_feature = finger
                                        data[position] = user
                                        myAdapter?.setList(data)
                                        fingerDialog?.hide()


                                    }

                                    override fun onError(response: com.lzy.okgo.model.Response<AddUserInfoBean>?) {
                                        super.onError(response)
                                        Toast.makeText(this@MainActivity,"网络连接异常",Toast.LENGTH_LONG).show()
                                    }

                                    override fun onFinish() {
                                        super.onFinish()
                                        loadingDialog.dismiss()
                                    }
                                })
                        }


                    })
                    fingerDialog?.show()
                }
                R.id.main_rv_item_btn_del -> {
                    DelDialog(this,object :DelDialog.OnDelDialogListener{
                        override fun onDelDialogSure() {
                            delUserInfo(data[position],position)
                        }

                        override fun onDelDialogCancel() {

                        }

                    }).show()

                }
            }
        }

        main_rv?.apply {
            layoutManager = GridLayoutManager(this@MainActivity,5)
            adapter = myAdapter
        }

        val initFinger = FingerUtil.instance.initFinger(context = this)
        if (initFinger) {
            Toast.makeText(this,"指纹模块初始化成功",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"指纹模块初始化失败",Toast.LENGTH_LONG).show()
        }
        init()

        system_setting = findViewById(R.id.system_setting)
        system_setting?.setOnLongClickListener {
            startActivity(Intent(this,SettingActivity::class.java))
            true
        }

//        FingerUtil.instance.checkFinger(object : FingerUtil.CheckFingerListener{
//            override fun checkSuccess(byteArray: ByteArray) {
//                var finger = byteArray
//                val byteArray1 = ByteArray(256)
//                val identify = identify(finger, byteArray1, 55, 1)
//                Log.e("TAG", "checkSuccess: ${identify}", )
//                if (identify>0){
//                    val strRes = String(byteArray1).split("\t")
//                    Log.e("TAG", "checkSuccess: ${strRes[0]}", )
//                    CoroutineScope(Dispatchers.Main).launch {
//                        Toast.makeText(this@MainActivity, "识别成功 ID : ${strRes[0]}", Toast.LENGTH_LONG).show()
//                    }
//                }
//
//            }
//
//            override fun checkError() {
//
//            }
//
//        })

        KeyboardVisibilityEvent.setEventListener(this) {
            hideBottomUIMenu()
        };


    }

    private fun delUserInfo(userinfo: Data,position: Int) {
        loadingDialog.show()
        val url = MMKV.defaultMMKV().getString(API_HOST, "http://jurong_gongan.58auv.com")
        OkGo.get<AddUserInfoBean>(url+ADD_USERINFO)
            .params("id", userinfo.id)
            .params("appId","")
            .params("deviceId","")
            .params("imageUrl", userinfo.imageUrl)
            .params("nickname", userinfo.nickname)
            .params("mobile", userinfo.mobile)
            .params("feature","")
            .params("fingerprint_feature","")
            .execute(object :AbsCallback<AddUserInfoBean>(){
                override fun onSuccess(response: com.lzy.okgo.model.Response<AddUserInfoBean>?) {
                    Toast.makeText(this@MainActivity,"删除成功",Toast.LENGTH_LONG).show()
                    userinfo.fingerprint_feature = ""
                    data[position] = userinfo
                    myAdapter?.setData(position,userinfo)
                }

                override fun onError(response: com.lzy.okgo.model.Response<AddUserInfoBean>?) {
                    super.onError(response)
                    Toast.makeText(this@MainActivity,"网络连接异常",Toast.LENGTH_LONG).show()
                }


                override fun onFinish() {
                    super.onFinish()
                    loadingDialog.dismiss()
                }

                override fun convertResponse(response: Response?): AddUserInfoBean {
                    return Gson().fromJson(response?.body()?.string(),AddUserInfoBean::class.java)
                }

            })

    }

    fun noDataDeal(){
        if (data.size == 0){
            has_data_lin?.visibility = View.GONE
            no_data_lin?.visibility = View.VISIBLE
        }else{
            has_data_lin?.visibility = View.VISIBLE
            no_data_lin?.visibility = View.GONE
        }
    }


}