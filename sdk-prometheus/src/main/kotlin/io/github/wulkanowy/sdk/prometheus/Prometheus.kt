package io.github.wulkanowy.sdk.prometheus

import io.github.wulkanowy.sdk.prometheus.models.AccountInfo
import io.github.wulkanowy.sdk.prometheus.repository.InfoRepository
import io.github.wulkanowy.sdk.prometheus.service.ServiceManager

class Prometheus {
    private val changeManager = resettableManager()

    private val cookieJarCabinet = CookieJarCabinet()

    var baseURL: String = "https://eduvulcan.pl/"

    var username: String = ""
        set(value) {
            if (field != value) {
                cookieJarCabinet.onUserChange()
            }
            field = value
        }

    var password: String = ""
        set(value) {
            if (field != value) {
                cookieJarCabinet.onUserChange()
            }
            field = value
        }

    private val okHttpFactory by lazy { OkHttpClientBuilderFactory() }

    private val serviceManager by resettableLazy(changeManager) {
        ServiceManager(
            okHttpClientBuilderFactory = okHttpFactory,
            cookieJarCabinet = cookieJarCabinet,
            baseURL = baseURL,
            username = username,
            password = password,
        )
    }

    private val infoRepository by resettableLazy(changeManager) {
        InfoRepository(
            infoService = serviceManager.getInfoService(),
        )
    }

    suspend fun login() {
        serviceManager.userLogin()
    }

    suspend fun getStudentInfo(): AccountInfo = infoRepository.getAccountInfo()
}
