package com.daqsoft.library_base.utils

import com.blankj.utilcode.util.GsonUtils
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.global.MMKVGlobal
import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.library_base.pojo.LoginInfo
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url
import java.io.File

/**
 * @package name：com.daqsoft.library_base.utils
 * @date 31/5/2021 下午 4:14
 * @author zp
 * @describe
 */
object UploadUtils {

    val retrofit : UploadApiService by lazy( LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitClient
            .Builder()
            .build()
            .create(UploadApiService::class.java)
    }

    fun upload(list:List<String>,callback: (list:List<OSSUploadResult>) -> Unit){
        if (list.isNullOrEmpty()){
            callback(arrayListOf<OSSUploadResult>())
            return
        }

        var ossKey = ""
        val loginInfoJson = MMKVUtils.decodeString(MMKVGlobal.LOGIN_INFO)
        if (!loginInfoJson.isNullOrBlank()){
            val loginInfo = GsonUtils.fromJson<LoginInfo>(loginInfoJson, LoginInfo::class.java)
            ossKey = loginInfo.profile.config.ossKey
        }
        val keyPart = MultipartBody.Part.createFormData(
            "key",
            ossKey,
        )

        Observable
            .fromIterable(list)
            // flatMap才是并发操作，由于返回数据信息不完整，这里采用串行的方式保证顺序，从请求数据中心重新拿想要的值
            .concatMap(Function<String, ObservableSource<OSSUploadResult>> {
                val file = File(it)
                val body = RequestBody.create(MediaType.get("multipart/form-data"), file)
                val filePart = MultipartBody.Part.createFormData(
                    "Filedata",
                    file.name,
                    body
                )
                val observable = retrofit
                    .uploadSingle(key = keyPart,file = filePart)
                    .observeOn(Schedulers.newThread())
                return@Function observable
            })
            .toList()
            .toObservable()
            .compose(RxUtils.schedulersTransformer())
            .subscribeWith(object : DisposableObserver<List<OSSUploadResult>>() {
                override fun onComplete() {
                }

                override fun onNext(t: List<OSSUploadResult>) {
                    callback(t)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    callback(arrayListOf<OSSUploadResult>())
                }
            })
    }
}


interface UploadApiService {

    @Multipart
    @POST()
    fun uploadSingle(
        @Url uploadUrl:String = HttpGlobal.OSS_UPLOAD,
        @Part key : MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Observable<OSSUploadResult>

}

data class OSSUploadResult(
    /**
     * 消息
     */
    var messages: String? = null,

    /**
     * 图片
     */
    var fileUrl: String? = null,

    /**
     * 错误消息
     */
    var error : Int = 0,

    /**
     * 消息
     */
    var message: String? = null,

    /**
     * 图片
     */
    var url: String? = null,
)