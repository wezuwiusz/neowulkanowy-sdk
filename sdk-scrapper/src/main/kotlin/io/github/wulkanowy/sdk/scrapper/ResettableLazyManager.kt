package io.github.wulkanowy.sdk.scrapper

import java.util.LinkedList
import kotlin.reflect.KProperty

/**
 * see https://stackoverflow.com/a/35757638/6695449
 */
internal class ResettableLazyManager {
    // we synchronize to make sure the timing of a reset() call and new inits do not collide
    val managedDelegates = LinkedList<Resettable>()

    fun register(managed: Resettable) {
        synchronized(managedDelegates) {
            managedDelegates.add(managed)
        }
    }

    fun reset() {
        synchronized(managedDelegates) {
            managedDelegates.forEach { it.reset() }
            managedDelegates.clear()
        }
    }
}

internal interface Resettable {
    fun reset()
}

internal class ResettableLazy<PROPTYPE>(val manager: ResettableLazyManager, val init: () -> PROPTYPE) : Resettable {
    @Volatile
    var lazyHolder = makeInitBlock()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): PROPTYPE {
        return lazyHolder.value
    }

    override fun reset() {
        lazyHolder = makeInitBlock()
    }

    fun makeInitBlock(): Lazy<PROPTYPE> {
        return lazy {
            manager.register(this)
            init()
        }
    }
}

internal fun <PROPTYPE> resettableLazy(manager: ResettableLazyManager, init: () -> PROPTYPE): ResettableLazy<PROPTYPE> {
    return ResettableLazy(manager, init)
}

internal fun resettableManager(): ResettableLazyManager = ResettableLazyManager()
