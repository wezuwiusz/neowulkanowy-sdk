package io.github.wulkanowy.sdk.scrapper.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Permissions(

    @SerialName("AuthInfos")
    val authInfos: List<AuthInfo> = emptyList(),

    @SerialName("Units")
    val units: List<PermissionUnit> = emptyList(),
)

@Serializable
internal data class AuthInfo(
    @SerialName("JednostkaSprawozdawczaId")
    val unitId: Int,

    @SerialName("LoginId")
    val loginId: Int,

    @SerialName("OpiekunIds")
    val parentIds: List<Int> = emptyList(),

    @SerialName("PracownikIds")
    val employeeIds: List<Int> = emptyList(),

    @SerialName("UczenIds")
    val studentIds: List<Int> = emptyList(),
)

@Serializable
internal data class PermissionUnit(
    @SerialName("Id")
    val id: Int,

    @SerialName("Nazwa")
    val name: String,

    @SerialName("Skrot")
    val short: String,

    @SerialName("Symbol")
    val symbol: String,
)
