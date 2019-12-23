package io.github.wulkanowy.sdk.scrapper

import okhttp3.OkHttpClient

class OkHttpClientBuilderFactory {

    private val okHttpClient by lazy { OkHttpClient() }

    fun create(): OkHttpClient.Builder = okHttpClient.newBuilder()
}
