package no.group15.playmagic.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.ServerSocket
import com.badlogic.gdx.net.ServerSocketHints
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.net.SocketHints
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlinx.coroutines.*
import ktx.async.*
import ktx.collections.*
import ktx.log.*


class Server(
	private val config: ServerConfig = ServerConfig()
) : Disposable, CoroutineScope by CoroutineScope(newSingleThreadAsyncContext()) {

	private var running = false
	private var socket: ServerSocket? = null
	private val clients = gdxMapOf<Int, ServerClient>()
	private val log = logger<Server>()
	private var nextClientId = 1
		get() = field++


	fun start() = launch {
		if (running) return@launch
		running = true

		val acceptExecutor = newSingleThreadAsyncContext()
//		val executor = newAsyncContext(2)

		val serverSocketHints = ServerSocketHints()
		serverSocketHints.acceptTimeout = 0
		try {
			socket = Gdx.net.newServerSocket(Net.Protocol.TCP, config.host, config.port, serverSocketHints)
			log.info { "Server started on tid ${Thread.currentThread().id}" }
		} catch (e: GdxRuntimeException) {
			// Failed starting server
			log.error(e) { "Failed to start server" }
			running = false
			return@launch
		}

		// Accept incoming connections on its own thread
		launch(acceptExecutor) {
			acceptSocket()
			log.debug { "Exiting acceptSocket" }
		}
	}

	/**
	 * Listen for incoming connection
	 */
	private tailrec fun acceptSocket() {
		try {
			if (socket == null) {
				log.error { "Inconsistent state?: running is $running wile socket is $socket" }
				return
			}
			val clientSocket = socket?.accept(SocketHints()) ?: return
			acceptClient(clientSocket)

		} catch (e: GdxRuntimeException) {
			log.error { e.cause?.message ?: "Error while accepting client: ${e.message}" }
		}
		if (!running) return else acceptSocket()
	}

	/**
	 * Accept or reject client [socket] depending on if server is full or not
	 */
	private fun acceptClient(socket: Socket) = launch {
		if (clients.size < config.maxPlayers) {
			val id = nextClientId
			val serverClient = ServerClient(id, socket)
			clients[id] = serverClient
			log.info { "Client with id ${serverClient.id} connected from ${socket.remoteAddress},  Client count: ${clients.size}" }

		} else {
			// Reject client
			log.debug { "Client tried to connect while server was full" }
			val writer = socket.outputStream.bufferedWriter()
			writer.write("Server is full\n")
			writer.close()
			socket.dispose()
		}
	}

	fun removeClient(id: Int) = launch {
		val client = clients.remove(id)
		client?.dispose()
	}

	override fun dispose() {
		running = false

		launch {
			// Close connections and cleanup
			try { socket?.dispose() } catch (e: GdxRuntimeException) {}
			clients.values().forEach {
				try { it.dispose() } catch (e: GdxRuntimeException) {}
			}
			clients.clear()
			socket = null
			log.info { "Server closed" }
		}
	}
}
