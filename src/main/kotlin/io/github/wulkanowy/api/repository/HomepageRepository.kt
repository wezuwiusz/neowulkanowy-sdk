package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.service.HomepageService
import io.reactivex.Single

class HomepageRepository(private val api: HomepageService) {

    fun getLuckyNumber(): Single<Int> {
        return api.getLuckyNumber().map { it.luckyNumer }
    }

}
