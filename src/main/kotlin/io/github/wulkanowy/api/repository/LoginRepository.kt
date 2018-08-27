package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.service.LoginService
import io.github.wulkanowy.api.login.ADFSFormResponse
import io.github.wulkanowy.api.login.CertificateResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.URLEncoder

class LoginRepository(
        val schema: String,
        val host: String,
        private val symbol: String,
        val client: OkHttpClient
) {

    private val firstEndpointUrl by lazy {
        val url = URLEncoder.encode("$schema://uonetplus.$host/$symbol/LoginEndpoint.aspx", "UTF-8")
        "/$symbol/FS/LS?wa=wsignin1.0&wtrealm=$url&wctx=$url"
    }

    private val api by lazy {
        Retrofit.Builder()
                .baseUrl("$schema://cufs.$host/$symbol/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client)
                .build()
                .create(LoginService::class.java)
    }

    fun sendCredentials(credentials: Map<String, String>): Single<CertificateResponse> {
        return api.sendCredentials(firstEndpointUrl, credentials)
    }

    fun sendCertificate(certificate: CertificateResponse, url: String = certificate.action): Single<CertificateResponse> { // response for adfs
        return api.sendCertificate(url, mapOf(
                "wa" to certificate.wa,
                "wresult" to certificate.wresult,
                "wctx" to certificate.wctx
        ))
    }

    // ADFS

    fun isADFS(): Boolean {
        return when(host) {
            "vulcan.net.pl" -> false
            "fakelog.cf" -> false
            "fakelog.localhost:3000" -> false
            else -> true
        }
    }

    fun getADFSFormState(): Single<ADFSFormResponse> {
        return api.getForm(firstEndpointUrl)
    }

    fun sendADFSFormStandardChoice(url: String, formState: Map<String, String>): Single<ADFSFormResponse> {
        return api.sendADFSFormStandardChoice("$schema://adfs.$host/$url", formState)
    }

    fun sendADFSCredentials(url: String, credentials: Map<String, String>): Single<CertificateResponse> {
        return api.sendADFSCredentials("$schema://adfs.$host/$url", credentials)
    }
}
