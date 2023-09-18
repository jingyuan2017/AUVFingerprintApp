package com.example.auvfingerprintapp.bean
import androidx.annotation.Keep


@Keep
data class UserInfoBeanFinger(
    val resultCode: Int,
    val resultMsg: String,
    val resultObject: ResultObject
)

@Keep
data class ResultObject(
    val currentPage: Int,
    val `data`: List<Data>,
    val limit: Int,
    val totalNum: String,
    val totalPage: Int
)

@Keep
data class Data(
    val createdTime: String,
    val deleted: Int,
    val feature: String,
    var fingerprint_feature: String,
    val id: String,
    val imageUrl: String,
    val mobile: String,
    val nickname: String,
    val updatedTime: String
)