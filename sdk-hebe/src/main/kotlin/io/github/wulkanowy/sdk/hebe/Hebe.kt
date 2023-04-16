package io.github.wulkanowy.sdk.hebe

import io.github.wulkanowy.sdk.hebe.register.RegisterDevice
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.repository.RepositoryManager
import io.github.wulkanowy.signer.hebe.generateCertificate
import io.github.wulkanowy.signer.hebe.generateKeyPair
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

class Hebe {

    private val resettableManager = resettableManager()

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
            resettableManager.reset()
        }

    var keyId = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var privatePem = ""
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

    private val serviceManager by resettableLazy(resettableManager) {
        RepositoryManager(
            logLevel = logLevel,
            keyId = keyId,
            privatePem = privatePem,
            deviceModel = deviceModel,
            interceptors = interceptors,
        )
    }

    private val routes by resettableLazy(resettableManager) { serviceManager.getRoutesRepository() }

    suspend fun register(firebaseToken: String, token: String, pin: String, symbol: String): RegisterDevice {
        val (publicPem, privatePem, publicHash) = generateKeyPair()
        val (certificatePem, certificateHash) = generateCertificate(privatePem)

        this.privatePem = privatePem
        this.keyId = certificateHash

        return serviceManager.getRegisterRepository(routes.getRouteByToken(token), symbol)
            .register(
                firebaseToken = firebaseToken,
                token = token,
                pin = pin,
                privatePem = privatePem,
                keyId = keyId,
                deviceModel = deviceModel,
            )
    }

    suspend fun getStudents(url: String, symbol: String): List<StudentInfo> {
        return serviceManager
            .getRegisterRepository(url, symbol)
            .getStudentInfo()
    }
}
