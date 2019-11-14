package io.github.wulkanowy.api

import okhttp3.OkHttpClient

class OkHttpClientBuilderFactory {

    private val okHttpClient by lazy { OkHttpClient() }

    fun create(): OkHttpClient.Builder = okHttpClient.newBuilder()
}
