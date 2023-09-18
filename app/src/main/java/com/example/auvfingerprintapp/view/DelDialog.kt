package com.example.auvfingerprintapp.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import com.example.auvfingerprintapp.R

class DelDialog : Dialog {


    var onDelDialogListener  : OnDelDialogListener ? = null
    var btn_cancel :Button ? = null
    var btn_confirm :Button ? = null
    constructor(context: Context) : super(context) {}
    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}

    constructor(context: Context,onDelDialogListener: OnDelDialogListener) : super(context) {
        this.onDelDialogListener = onDelDialogListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.layout_del_dialog)

        btn_cancel = findViewById(R.id.btn_cancel)
        btn_confirm = findViewById(R.id.btn_confirm)
        btn_cancel?.setOnClickListener{
            onDelDialogListener?.onDelDialogCancel()
            dismiss()
        }
        btn_confirm?.setOnClickListener{
            onDelDialogListener?.onDelDialogSure()
            dismiss()
        }

    }

    override fun show() {
        hideNavigationBar(window!!)
        super.show()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        hideNavigationBar(window!!)
    }

    fun hideNavigationBar(window: Window) {
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

    interface OnDelDialogListener {
        fun onDelDialogSure()
        fun onDelDialogCancel()
    }
}