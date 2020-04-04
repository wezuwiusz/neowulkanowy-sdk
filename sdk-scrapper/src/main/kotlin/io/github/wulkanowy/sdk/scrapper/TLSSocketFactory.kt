package io.github.wulkanowy.sdk.scrapper

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class TLSSocketFactory : SSLSocketFactory() {

    private val factory: SSLSocketFactory

    init {
        val context = SSLContext.getInstance("TLS")
        context.init(null, null, null)
        factory = context.socketFactory
    }

    private val protocols = arrayOf("TLSv1.2", "TLSv1.1")

    private fun isTLSServerEnabled(sslSocket: SSLSocket) = sslSocket.supportedProtocols.any { it in protocols }

    private fun enableTLSOnSocket(socket: Socket?) = socket?.apply {
        if (this is SSLSocket && isTLSServerEnabled(this)) {
            enabledProtocols = protocols
        }
    }

    override fun getDefaultCipherSuites(): Array<String> = factory.defaultCipherSuites

    override fun getSupportedCipherSuites(): Array<String> = factory.supportedCipherSuites

    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean) =
        enableTLSOnSocket(factory.createSocket(s, host, port, autoClose))

    override fun createSocket(host: String, port: Int) =
        enableTLSOnSocket(factory.createSocket(host, port))

    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int) =
        enableTLSOnSocket(factory.createSocket(host, port, localHost, localPort))

    override fun createSocket(host: InetAddress, port: Int) =
        enableTLSOnSocket(factory.createSocket(host, port))

    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int) =
        enableTLSOnSocket(factory.createSocket(address, port, localAddress, localPort))
}
