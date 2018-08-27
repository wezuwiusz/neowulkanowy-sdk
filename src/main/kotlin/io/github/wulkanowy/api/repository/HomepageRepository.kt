package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.auth.VulcanException
import io.github.wulkanowy.api.service.HomepageService
import io.reactivex.Single
import okhttp3.OkHttpClient
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class HomepageRepository(
        private val schema: String,
        private val host: String,
        private val client: OkHttpClient
) {

    private val api by lazy {
        Retrofit.Builder()
                .baseUrl("$schema://uonetplus.$host/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client)
                .build()
                .create(HomepageService::class.java)
    }

    fun getStartInfo(symbol: String): Single<List<String>> {
        return api.getStartInfo(symbol).map {
            it.schools.map { schoolUrl ->
                getExtractedIdFromUrl(schoolUrl)
            }
        }
    }

    private fun getExtractedIdFromUrl(snpPageUrl: String): String {
        val path = snpPageUrl.split(host).getOrNull(1)?.split("/")

        if (6 != path?.size) {
            throw VulcanException("Na pewno używasz konta z dostępem do Witryny ucznia i rodzica?")
        }

        return path[2]
    }
}
