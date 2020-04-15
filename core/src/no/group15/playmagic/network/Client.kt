package no.group15.playmagic.network

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.SocketHints

class Client(
	config: ClientConfig = ClientConfig()
) {

	private val socket = Gdx.net.newClientSocket(Net.Protocol.TCP, config.host, config.port, SocketHints())


	init {
	    println("Connected to server: ${socket.remoteAddress}")
	}

}
