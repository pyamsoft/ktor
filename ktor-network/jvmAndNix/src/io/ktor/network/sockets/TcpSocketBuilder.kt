package io.ktor.network.sockets

import io.ktor.network.selector.*

/**
 * TCP socket builder
 */
@Suppress("PublicApiImplicitType")
public class TcpSocketBuilder(
    private val selector: SelectorManager,
    override var options: SocketOptions
) : Configurable<TcpSocketBuilder, SocketOptions> {
    /**
     * Connect to [hostname] and [port].
     */
    public suspend fun connect(
        hostname: String,
        port: Int,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit = {}
    ): Socket = connect(InetSocketAddress(hostname, port), configure)

    /**
     * Connect to [hostname] and [port].
     */
    public suspend fun connectWithConfiguration(
        hostname: String,
        port: Int,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit = {},
        onBeforeConnect: suspend (Socket) -> Unit = {}
    ): Socket = connectWithConfiguration(InetSocketAddress(hostname, port), configure, onBeforeConnect)

    /**
     * Bind server socket at [port] to listen to [hostname].
     */
    public fun bind(
        hostname: String = "0.0.0.0",
        port: Int = 0,
        configure: SocketOptions.AcceptorOptions.() -> Unit = {}
    ): ServerSocket = bind(InetSocketAddress(hostname, port), configure)

    /**
     * Bind server socket at [port] to listen to [hostname].
     */
    public fun bindWithConfiguration(
        hostname: String = "0.0.0.0",
        port: Int = 0,
        configure: SocketOptions.AcceptorOptions.() -> Unit = {},
        onBeforeBind: (ServerSocket) -> Unit = {}
    ): ServerSocket = bindWithConfiguration(InetSocketAddress(hostname, port), configure, onBeforeBind)

    /**
     * Connect to [remoteAddress].
     */
    public suspend fun connect(
        remoteAddress: SocketAddress,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit = {}
    ): Socket = connect(selector, remoteAddress, options.peer().tcp().apply(configure))

    /**
     * Connect to [remoteAddress].
     */
    public suspend fun connectWithConfiguration(
        remoteAddress: SocketAddress,
        configure: SocketOptions.TCPClientSocketOptions.() -> Unit = {},
        onBeforeConnect: suspend (Socket) -> Unit = {}
    ): Socket = connectWithConfiguration(selector, remoteAddress, options.peer().tcp().apply(configure), onBeforeConnect)

    /**
     * Bind server socket to listen to [localAddress].
     */
    public fun bind(
        localAddress: SocketAddress? = null,
        configure: SocketOptions.AcceptorOptions.() -> Unit = {},
    ): ServerSocket = bind(selector, localAddress, options.peer().acceptor().apply(configure))

    /**
     * Bind server socket to listen to [localAddress].
     */
    public fun bindWithConfiguration(
        localAddress: SocketAddress? = null,
        configure: SocketOptions.AcceptorOptions.() -> Unit = {},
        onBeforeBind: (ServerSocket) -> Unit = {}
    ): ServerSocket = bindWithConfiguration(selector, localAddress, options.peer().acceptor().apply(configure), onBeforeBind)
}
