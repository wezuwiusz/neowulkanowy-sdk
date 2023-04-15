package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.login.BadCredentialsException
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Ignore
import org.junit.Test

@Ignore
class HostsRemoteTest : BaseTest() {

    private val knownHosts = listOf(
        "vulcan.net.pl" to "Default",
        "eszkola.opolskie.pl" to "opole",
        "edu.gdansk.pl" to "gdansk",
        // "edu.lublin.eu" to "lublin", // they are blocking us :///
        "umt.tarnow.pl" to "tarnow",
        "eduportal.koszalin.pl" to "koszalin",
        "vulcan.net.pl" to "rawamazowiecka",
        "vulcan.net.pl" to "zdunskawola",
        "vulcan.net.pl" to "sieradz",
        "vulcan.net.pl" to "skarzyskokamienna",
        "vulcan.net.pl" to "lask",
        "vulcan.net.pl" to "powiatlaski",
        "vulcan.net.pl" to "powiatkrasnostawski",
        "vulcan.net.pl" to "rzeszowprojekt",
        "vulcan.net.pl" to "powiatketrzynski",
        "vulcan.net.pl" to "gminaulanmajorat",
        "vulcan.net.pl" to "gminaozorkow",
        "vulcan.net.pl" to "tomaszowmazowieckiprojekt",
        "vulcan.net.pl" to "gminalopiennikgorny",
    )

    @Test
    fun loginTest() = runBlocking {
        knownHosts.forEach { (host, symbol) ->
            println("$host/$symbol")
            val res = runCatching { getScrapper(host, symbol).getStudents() }
            requireNotNull(res.exceptionOrNull()).cause!!.printStackTrace()
            assert(res.exceptionOrNull() is BadCredentialsException)
            println()
        }
    }

    private fun getScrapper(domain: String, startSymbol: String): Scrapper = Scrapper().apply {
        logLevel = HttpLoggingInterceptor.Level.BASIC
        loginType = Scrapper.LoginType.AUTO
        ssl = true
        host = domain
        symbol = startSymbol
        email = "jan@fakelog.cf"
        password = "jan123"
    }
}
