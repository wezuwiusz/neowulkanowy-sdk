package io.github.wulkanowy.sdk.scrapper.login

internal data class LoginResult(
    val isStudentSchoolUseEduOne: Boolean,
    val studentSchools: List<String>,
)
