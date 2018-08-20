package io.github.wulkanowy.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class LoginInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        val original = chain!!.request()
        val request = original.newBuilder()

        return chain.proceed(request.build())
    }

}
