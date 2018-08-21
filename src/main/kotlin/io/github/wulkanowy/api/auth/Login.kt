package io.github.wulkanowy.api.auth

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class Login(private val client: Client) {

    companion object {

        private const val LOGIN_PAGE_URL = "{schema}://cufs.{host}/{symbol}/Account/LogOn"

        private const val LOGIN_PAGE_URL_QUERY = "?ReturnUrl=%2F{symbol}%2FFS%2FLS%3Fwa%3Dwsignin1.0%26wtrealm%3D" +
                "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx%26wctx%3D" +
                "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx"

        private val logger = LoggerFactory.getLogger(Login::class.java)
    }

    //TODO: replace with request retry
    private var lastSuccessRequest: Date? = null

    fun isLoggedIn(): Boolean {
        return !client.getCookies().isEmpty() && lastSuccessRequest != null &&
                5 > TimeUnit.MILLISECONDS.toMinutes(Date().time - lastSuccessRequest!!.time)
    }

    @Throws(VulcanException::class)
    fun login(email: String, password: String, symbol: String) {
        val certDoc = sendCredentials(email, password)

        if ("Błąd" == certDoc.title()) {
            client.clearCookies()
            throw NotLoggedInErrorException(certDoc.body().text())
        }

        sendCertificate(certDoc, symbol)
    }

    @Throws(VulcanException::class)
    private fun sendCredentials(email: String, password: String): Document {
        val credentials = arrayOf(arrayOf("LoginName", email), arrayOf("Password", password))

        val nextDoc = sendCredentialsData(credentials, LOGIN_PAGE_URL + LOGIN_PAGE_URL_QUERY.replace(":", "%253A"))

        val errorMessage = nextDoc.selectFirst(".ErrorMessage, #ErrorTextLabel")
        if (null != errorMessage) {
            throw BadCredentialsException(errorMessage.text())
        }

        return nextDoc
    }

    @Throws(VulcanException::class)
    private fun sendCredentialsData(credentials: Array<Array<String>>, nextUrl: String): Document {
        var next = nextUrl
        var creds = credentials

        val formFirst = client.getPageByUrl(nextUrl).selectFirst("#form1")
        if (null != formFirst) { // only on adfs login
            val formSecond = client.postPageByUrl(
                    formFirst.attr("abs:action"),
                    getFormStateParams(formFirst, "", "")
            )

            creds = getFormStateParams(formSecond, credentials[0][1], credentials[1][1])
            next = formSecond.selectFirst("#form1").attr("abs:action")
        }

        return client.postPageByUrl(next, creds)
    }

    private fun getFormStateParams(form: Element, email: String, password: String): Array<Array<String>> {
        return arrayOf(
                arrayOf("__VIEWSTATE", form.select("#__VIEWSTATE").`val`()),
                arrayOf("__VIEWSTATEGENERATOR", form.select("#__VIEWSTATEGENERATOR").`val`()),
                arrayOf("__EVENTVALIDATION", form.select("#__EVENTVALIDATION").`val`()),
                arrayOf("__db", form.select("input[name=__db]").`val`()),
                arrayOf("PassiveSignInButton.x", "0"),
                arrayOf("PassiveSignInButton.y", "0"),
                arrayOf("SubmitButton.x", "0"),
                arrayOf("SubmitButton.y", "0"),
                arrayOf("UsernameTextBox", email),
                arrayOf("PasswordTextBox", password)
        )
    }

    @Throws(VulcanException::class)
    private fun sendCertificate(doc: Document, defaultSymbol: String) {
        client.setSymbol(findSymbol(defaultSymbol, doc.select("input[name=wresult]").`val`()))

        val targetDoc = sendCertData(doc)
        var title = targetDoc.title()

        if ("Working..." == title) {
            logger.info("ADFS login")
            title = sendCertData(targetDoc).title()
        }

        if ("Logowanie" == title) {
            throw AccountPermissionException("No account access. Try another symbol")
        }

        if ("Uonet+" != title) {
            logger.debug("Login failed. Body: {}", targetDoc.body())
            throw LoginErrorException("Expected page title `UONET+`, got $title")
        }

//        client.setSchools(StartPage(client).getSchools(targetDoc))
    }

    @Throws(VulcanException::class)
    private fun sendCertData(doc: Document): Document {
        val url = doc.select("form[name=hiddenform]").attr("action")

        return client.postPageByUrl(url.replaceFirst("Default", "{symbol}"),
                arrayOf(arrayOf("wa", "wsignin1.0"),
                        arrayOf("wresult", doc.select("input[name=wresult]").`val`()),
                        arrayOf("wctx", doc.select("input[name=wctx]").`val`())
                )
        )
    }

    @Throws(AccountPermissionException::class)
    private fun findSymbol(symbol: String, certificate: String): String {
        val symbols = getSymbolsFromCertificate(certificate)
//        client.setSymbols(symbols)

        return if ("Default" == symbol) {
            getLastSymbol(symbols)
        } else symbol

    }

    private fun getSymbolsFromCertificate(certificate: String): List<String> {
        val instances = Jsoup
                .parse(certificate.replace(":".toRegex(), ""), "", Parser.xmlParser())
                .select("[AttributeName=\"UserInstance\"] samlAttributeValue")

        return instances.map {
            it.text()
        }
    }

    @Throws(AccountPermissionException::class)
    private fun getLastSymbol(symbols: List<String>): String {
        if (symbols.isEmpty()) { // on adfs login
            return ""
        }

        if (symbols.size < 2) { // 1st index is always `Default`
            throw AccountPermissionException("First login detected, specify symbol")
        }

        return symbols[1]
    }
}
