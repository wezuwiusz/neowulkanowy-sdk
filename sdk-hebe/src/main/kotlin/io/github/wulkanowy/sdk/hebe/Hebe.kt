package io.github.wulkanowy.sdk.hebe

import io.github.wulkanowy.sdk.hebe.register.RegisterDevice
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
import io.github.wulkanowy.sdk.hebe.repository.RepositoryManager
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

    private val appInterceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun addInterceptor(interceptor: Interceptor, network: Boolean = false) {
        appInterceptors.add(interceptor to network)
    }

    private val serviceManager by resettableLazy(resettableManager) {
        RepositoryManager(
            logLevel = logLevel,
            keyId = keyId,
            privatePem = privatePem,
            deviceModel = deviceModel,
        ).apply {
            appInterceptors.forEach { (interceptor, isNetwork) ->
                setInterceptor(interceptor, isNetwork)
            }
        }
    }

    private val routes by resettableLazy(resettableManager) { serviceManager.getRoutesRepository() }

    suspend fun register(token: String, pin: String, symbol: String, firebaseToken: String? = null): RegisterDevice {
        val (publicPem, privatePem, publicHash) = generateKeyPair()

        this.keyId = publicHash
        this.privatePem = privatePem

        val envelope = serviceManager.getRegisterRepository(
            baseUrl = routes.getRouteByToken(token),
            symbol = symbol,
        ).register(
            firebaseToken = firebaseToken,
            token = token,
            pin = pin,
            certificatePem = publicPem,
            certificateId = publicHash,
            deviceModel = deviceModel,
        )

        return RegisterDevice(
            loginId = envelope.loginId,
            restUrl = envelope.restUrl,
            userLogin = envelope.userLogin,
            userName = envelope.userName,
            certificateHash = publicHash,
            privatePem = privatePem,
        )
    }

    suspend fun getStudents(url: String): List<StudentInfo> {
        return serviceManager
            .getRegisterRepository(url)
            .getStudentInfo()
    }
}
