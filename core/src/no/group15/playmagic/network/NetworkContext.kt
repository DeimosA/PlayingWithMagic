package no.group15.playmagic.network

import no.group15.playmagic.server.Server
import no.group15.playmagic.server.ServerConfig


class NetworkContext(
	val clientConfig: ClientConfig = ClientConfig(),
	val serverConfig: ServerConfig? = null
) {

	val client: Client by lazy {
		Client(clientConfig)
	}

	val server: Server? by lazy {
		if (serverConfig != null) Server(serverConfig)
		else null
	}

}
