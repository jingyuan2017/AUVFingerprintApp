package com.example.auvfingerprintapp.bean
import androidx.annotation.Keep


@Keep
data class DelUserInfoBean(
    val resultCode: Int,
    val resultMsg: String,
    val resultObject: Boolean
)
