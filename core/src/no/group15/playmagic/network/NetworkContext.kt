package no.group15.playmagic.network

import ktx.inject.Context
import no.group15.playmagic.server.Server
import no.group15.playmagic.server.ServerConfig


/**
 * Contain lazy loaded client and server and configs for them
 * server will be initialized to null when not hosting on this device
 */
class NetworkContext(
	injectContext: Context,
	val clientConfig: ClientConfig = ClientConfig(),
	val serverConfig: ServerConfig? = null
) {

	val client: GameClient by lazy {
		GameClient(injectContext, clientConfig)
	}

	val server: Server? by lazy {
		if (serverConfig != null) Server(serverConfig)
		else null
	}

}
