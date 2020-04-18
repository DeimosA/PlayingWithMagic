package no.group15.playmagic.network

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.net.SocketHints
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.log.*


class Client(
	config: ClientConfig = ClientConfig()
) : Disposable {

	val socket: Socket by lazy {
		Gdx.net.newClientSocket(Net.Protocol.TCP, config.host, config.port, SocketHints())
	}
	private val log = logger<Client>()


	fun connect() {
		try {
			socket
			log.info { "Connected to server: ${socket.remoteAddress}" }
			val msg = socket.inputStream.bufferedReader().readLine()
			log.info { "Server says: $msg" }
		} catch (e: GdxRuntimeException) {
			log.error { e.cause?.message ?: "Error opening client socket: ${e.message}" }
		}
	}

	override fun dispose() {
		socket.dispose()
	}
}
