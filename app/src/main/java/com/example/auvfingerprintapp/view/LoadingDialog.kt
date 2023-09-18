package com.example.auvfingerprintapp.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.example.auvfingerprintapp.R

class LoadingDialog : Dialog {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_custom_progress_dialog_view)
        window?.setBackgroundDrawableResource(android.R.color.transparent);

    }

    override fun hide() {
        super.hide()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        hideNavigationBar(window!!)
    }

    override fun show() {
        hideNavigationBar(window!!)
        super.show()

    }
    fun hideNavigationBar(window: Window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = uiOptions
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

    }
}