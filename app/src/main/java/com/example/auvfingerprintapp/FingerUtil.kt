package com.example.auvfingerprintapp

import android.content.Context
import android.util.Log
import com.example.auvfingerprintapp.config.PID
import com.example.auvfingerprintapp.config.VID
import com.zkteco.android.biometric.core.device.ParameterHelper
import com.zkteco.android.biometric.core.device.TransportType
import com.zkteco.android.biometric.core.utils.LogHelper
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService.*
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class FingerUtil {
    //lazy延迟初始化
    var context : Context? = null
    val TAG = "FingerUtil"


    var registerFingerListener : RegisterFingerListener? = null
    var checkFingerListener : CheckFingerListener? = null

    private var fingerprintSensor: FingerprintSensor? = null
    companion object {
        val instance: FingerUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FingerUtil()
        }
    }

    /**
     * 初始化指纹仪
     */
    fun initFinger(context: Context) :Boolean{
        this.context = context
        // Define output log level
        LogHelper.setLevel(Log.ASSERT)
        LogHelper.setNDKLogLevel(Log.ASSERT)
        var fingerprintParams = mutableMapOf<String,Int>()
        fingerprintParams[ParameterHelper.PARAM_KEY_VID] = VID
        fingerprintParams[ParameterHelper.PARAM_KEY_PID] = PID
        if (fingerprintSensor==null){
            fingerprintSensor = FingprintFactory.createFingerprintSensor(this.context, TransportType.USB,
                fingerprintParams as Map<String, Any>?
            )
        }
        try {
            fingerprintSensor?.open(0)
        }catch (E : FingerprintException){
            return false
        }


        fingerprintSensor?.setFingerprintCaptureListener(0,object :FingerprintCaptureListener{
            override fun captureOK(p0: ByteArray?) {
                //指纹采集成功


            }

            override fun captureError(p0: FingerprintException?) {
                //指纹采集失败

            }

            override fun extractOK(p0: ByteArray?) {
                //指纹特征提取成功

                Log.i(TAG, "extractOK: ")
                if (registerFingerListener!=null){
                    registerFingerListener?.registerSuccess(p0!!)
                    return
                }
                if (checkFingerListener!=null){
                    checkFingerListener?.checkSuccess(p0!!)
                }


            }

            override fun extractError(p0: Int) {
                //指纹特征提取失败

            }

        })
        fingerprintSensor?.startCapture(0)
        return true

    }
    //初始化指纹数据
    fun initFingerData(fingerData : MutableList<ByteArray>){
        CoroutineScope(Dispatchers.IO).launch {
            fingerData.forEach {
                save(it,"name")
            }

        }
    }

    //注册指纹信息
    fun registerFinger(registerFingerListener: RegisterFingerListener){
        this.registerFingerListener = registerFingerListener
    }

    /**
     * 解除回调
     */
    open fun removeLinstener(){
        this.registerFingerListener = null
       // this.checkFingerListener = null
    }


    //对比指纹信息
    fun checkFinger(checkFingerListener: CheckFingerListener){
        this.checkFingerListener = checkFingerListener
    }

    fun stopFinger(){
        fingerprintSensor?.stopCapture(0)
        fingerprintSensor?.close(0)
        //fingerprintSensor?.destroy()
        //fingerprintSensor=null
    }


    interface RegisterFingerListener{
        fun registerSuccess(byteArray: ByteArray)
        fun registerError()
        fun registerProgress(progress : Int)
    }

    interface CheckFingerListener{
        fun checkSuccess(byteArray: ByteArray)
        fun checkError()
    }




}