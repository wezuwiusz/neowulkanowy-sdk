package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.service.HomepageService
import io.reactivex.Maybe

class HomepageRepository(private val api: HomepageService) {

    fun getLuckyNumber(): Maybe<Int> {
        return api.getLuckyNumber()
                .map { it.luckyNumer }
                .filter { it != 0 }
    }

}
