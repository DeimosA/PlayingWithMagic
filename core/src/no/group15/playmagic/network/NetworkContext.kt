package no.group15.playmagic.network

import no.group15.playmagic.server.Server
import no.group15.playmagic.server.ServerConfig


/**
 * Contain lazy loaded client and server and configs for them
 * server will be initialized to null when not hosting on this device
 */
class NetworkContext(
	val clientConfig: ClientConfig = ClientConfig(),
	val serverConfig: ServerConfig? = null
) {

	val client: GameClient by lazy {
		GameClient(clientConfig)
	}

	val server: Server? by lazy {
		if (serverConfig != null) Server(serverConfig)
		else null
	}

}
