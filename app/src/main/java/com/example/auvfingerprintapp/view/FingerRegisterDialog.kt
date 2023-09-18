package com.example.auvfingerprintapp.view

import AddUserInfoBean
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.auvfingerprintapp.FingerUtil
import com.example.auvfingerprintapp.R
import com.example.auvfingerprintapp.bean.Data
import com.example.auvfingerprintapp.config.ADD_USERINFO
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response

class FingerRegisterDialog : Dialog {

    //是否是添加指纹
    var isadd : Boolean = true
    var img : CircleImageView? = null
    var userInfo : Data? = null
    var finger_register_tv_tip : TextView? = null
    var finger_register_dialog_statue : CircleImageView? = null
    var add_finger_btn : Button? = null
    var register = mutableListOf<ByteArray>()
    var finger : String = ""
    var finger_register_tv_name:TextView? = null
    var finger_register_tv_id:TextView? = null
    var dialog_back : ImageView? = null
    var onDialogClickListener : OnDialogClickListener? = null
    var registerStr : ByteArray? = null
    var finger_register_dialog_statue_tv : TextView? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context,userInfo:Data,isadd :Boolean,onDialogClickListener : OnDialogClickListener) : super(context) {
        this.userInfo = userInfo
        this.isadd = isadd
        this.onDialogClickListener = onDialogClickListener
    }
    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finger_register_dialog)
        window?.setBackgroundDrawableResource(android.R.color.transparent);

        img = findViewById(R.id.finger_register_dialog_img)
        finger_register_tv_id = findViewById(R.id.finger_register_tv_id)
        finger_register_tv_name = findViewById(R.id.finger_register_tv_name)
        finger_register_dialog_statue_tv= findViewById(R.id.finger_register_dialog_statue_tv)
        dialog_back = findViewById(R.id.dialog_back_id)
        dialog_back?.setOnClickListener{
            dismiss()
        }
        finger_register_tv_id?.text = "ID: ${userInfo?.id}"
        finger_register_tv_name?.text = userInfo?.nickname
        add_finger_btn = findViewById(R.id.add_finger_btn)
        add_finger_btn?.setOnClickListener {
            if (finger.isEmpty()) {
                Toast.makeText(context,"请先注册指纹",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(onDialogClickListener!=null){
                if (userInfo!=null){
                    //val save = ZKFingerService.save(registerStr, userInfo?.id)

                    onDialogClickListener?.onDialogClick(userInfo!!,finger)
                }
            }

        }
        finger_register_dialog_statue = findViewById(R.id.finger_register_dialog_statue)
        img?.setImageResource(R.mipmap.img)

        finger_register_dialog_statue?.setImageResource(R.mipmap.finger_null)
        finger_register_tv_tip = findViewById(R.id.finger_register_tv_tip)
        finger_register_tv_tip?.text = "请将手指放置指纹机"

        initFinger()
    }

    /**
     * 初始化指纹
     */
    private fun initFinger() {
        FingerUtil.instance.registerFinger(object : FingerUtil.RegisterFingerListener{
            override fun registerSuccess(byteArray: ByteArray) {
                var fingerbyte = byteArray
                if(registerStr != null) return
                if (fingerbyte != null && register.size < 3) {
                    val bufids = ByteArray(256)
                    val identify = ZKFingerService.identify(fingerbyte, bufids, 55, 1)
                    if (identify>0){
                        return
                    }
                    register.add(fingerbyte)
                    CoroutineScope(Dispatchers.Main).launch {
                        finger_register_tv_tip?.text = "请再次放置手指 ${4-register.size} 次"
                        finger_register_tv_tip?.setTextColor(Color.parseColor("#999999"))
                    }
                }else{

                    registerStr = ByteArray(2048)
                    var errorCode =
                        ZKFingerService.merge(register[0], register[1], register[2], registerStr)
                    if ( errorCode > 0){

                        finger = Base64.encodeToString(registerStr,Base64.DEFAULT)
                        CoroutineScope(Dispatchers.Main).launch {
                            finger_register_tv_tip?.text = "请按确认保存指纹"
                            finger_register_dialog_statue?.setImageResource(R.mipmap.finger_success)
                            finger_register_dialog_statue_tv?.text = "识别成功"
                            finger_register_dialog_statue_tv?.setTextColor(Color.parseColor("#00B956"))

                        }
                        register.clear()
                    }else{
                        register.clear()
                        CoroutineScope(Dispatchers.Main).launch {
                            finger_register_tv_tip?.text = "指纹录入失败，请重新录入"
                        }

                    }

                }
            }

            override fun registerError() {

            }

            override fun registerProgress(progress: Int) {

            }

        })

    }

    override fun show() {
        hideNavigationBar(window!!)
        super.show()
    }

    private fun hideNavigationBar(window: Window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        super.show()
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = uiOptions
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    override fun onDetachedFromWindow() {
        FingerUtil.instance.removeLinstener()
        super.onDetachedFromWindow()
        hideNavigationBar(window!!)
    }

    override fun hide() {
        FingerUtil.instance.removeLinstener()
        super.hide()
    }
    interface OnDialogClickListener{
        fun onDialogClick(userInfo: Data,finger:String)
    }


}