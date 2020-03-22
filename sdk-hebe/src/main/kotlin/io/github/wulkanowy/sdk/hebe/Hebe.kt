package io.github.wulkanowy.sdk.hebe

import io.github.wulkanowy.sdk.hebe.register.RegisterResponse
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.repository.RepositoryManager
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

class Hebe {

    private val resettableManager = resettableManager()

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
            resettableManager.reset()
        }

    var privateKey = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var baseUrl = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var schoolSymbol = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var deviceModel = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    private val serviceManager by resettableLazy(resettableManager) { RepositoryManager(logLevel, privateKey, interceptors, baseUrl, schoolSymbol) }

    private val routes by resettableLazy(resettableManager) { serviceManager.getRoutesRepository() }

    fun register(privateKey: String, certificateId: String, token: String, pin: String, symbol: String): Single<ApiResponse<RegisterResponse>> {
        return routes.getRouteByToken(token).flatMap { baseUrl ->
            serviceManager
                .getRegisterRepository(baseUrl, symbol)
                .registerDevice(privateKey, certificateId, deviceModel, pin, token)
        }
    }

    fun getStudents(url: String, symbol: String): Single<List<StudentInfo>> {
        return serviceManager
            .getRegisterRepository(url, symbol)
            .getStudentInfo()
    }
}
