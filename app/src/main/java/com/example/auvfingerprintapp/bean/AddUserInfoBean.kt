
import androidx.annotation.Keep
@Keep
data class AddUserInfoBean(
    val appId: String,
    val deviceId: String,
    val feature: String,
    val id: Int,
    val imageUrl: String,
    val mobile: String,
    val nickname: String,
    val fingerprint_feature:String
)