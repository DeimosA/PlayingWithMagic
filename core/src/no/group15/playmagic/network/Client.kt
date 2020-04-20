package no.group15.playmagic.network

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.net.SocketHints
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ktx.async.*
import ktx.collections.*
import ktx.json.*
import ktx.log.*
import no.group15.playmagic.server.Server
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException


class Client(
	config: ClientConfig = ClientConfig()
) : Disposable, CoroutineScope by CoroutineScope(newAsyncContext(2)) {

	val socket: Socket by lazy {
		Gdx.net.newClientSocket(Net.Protocol.TCP, config.host, config.port, SocketHints())
	}
	private val log = logger<Client>()
	private var reader: BufferedReader? = null
	private var writer: BufferedWriter? = null
	private val json = Json()

	private var id: Int = 0


	fun connect() = launch {
		try {
			socket
			log.info { "Connected to server: ${socket.remoteAddress}" }
			reader = socket.inputStream.bufferedReader()
			writer = socket.outputStream.bufferedWriter()
			launch { receive() }

		} catch (e: GdxRuntimeException) {
			log.error { e.cause?.message ?: "Error opening client socket: ${e.message}" }
		}
	}

	private tailrec fun receive() {
		try {
			val line = reader?.readLine() ?: return
			// TODO do something with line
			val message = json.fromJson<Server.Message>(line)
			log.debug { "${message.type} ${message.text} ${message.clientId}" }
		} catch (e: IOException) {
			// TODO connection lost
		}
		receive()
	}

	override fun dispose() {
		socket.dispose()
		reader?.close()
		writer?.close()
	}
}
