package com.example.auvfingerprintapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.auvfingerprintapp.R
import java.util.*

//loading框
@SuppressLint("StaticFieldLeak")
private var loadingDialog: MaterialDialog? = null
private var showTime : Long = 0;
private const val showLoadingTime :Long = 1000;
/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中") {
    if (!this.isFinishing) {
        if (loadingDialog == null) {
            loadingDialog = MaterialDialog(this)
                .cancelable(true)
                .cancelOnTouchOutside(false)
                .cornerRadius(12f)
                .customView(R.layout.layout_custom_progress_dialog_view)
                .lifecycleOwner(this)
            loadingDialog?.getCustomView()?.run {
                this.findViewById<TextView>(R.id.loading_tips).text = message
//                this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(this@showLoadingExt)
            }
        }
        showTime = System.currentTimeMillis();
        loadingDialog?.show()
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    activity?.let {
        if (!it.isFinishing) {
            if (loadingDialog == null) {
                loadingDialog = MaterialDialog(it)
                    .cancelable(true)
                    .cancelOnTouchOutside(false)
                    .cornerRadius(12f)
                    .customView(R.layout.layout_custom_progress_dialog_view)
                    .lifecycleOwner(this)
                loadingDialog?.getCustomView()?.run {
                    this.findViewById<TextView>(R.id.loading_tips).text = message
//                    this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(it)
                }
            }
            showTime = System.currentTimeMillis();
            loadingDialog?.show()
        }
    }
}

/**
 * 关闭等待框
 */
fun Activity.dismissLoadingExt() {
    if (System.currentTimeMillis() - showTime < showLoadingTime){
        Timer().schedule(object : TimerTask() {
            override fun run() {
                dismissLoadingExt()
            }
        }, showLoadingTime-(System.currentTimeMillis() - showTime ))

    }else{
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    if (System.currentTimeMillis() - showTime < showLoadingTime){
        Timer().schedule(object : TimerTask() {
            override fun run() {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }, showLoadingTime-(System.currentTimeMillis() - showTime ))
    }else{
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}
