package no.group15.playmagic.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.ServerSocket
import com.badlogic.gdx.net.ServerSocketHints
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.net.SocketHints
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ktx.collections.gdxArrayOf


class Server(
	private val config: ServerConfig = ServerConfig()
) : Runnable, Disposable {

	private var running = true
	private val socket: ServerSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, config.port, ServerSocketHints())
	private val clients = gdxArrayOf<Socket>(true, config.maxPlayers)
//	var serverThread: Thread? = null


	override fun run() {
//		Thread.sleep(5000)
		println("Server started")

		val scope = GlobalScope.launch {
			acceptSocket()
		}

		while (running) {
			Thread.sleep(500)
		}
		// Close connections
		scope.cancel()
		closeAll()
		println("Server closed")
	}

	private tailrec fun acceptSocket() {
		try {
			val client = socket.accept(SocketHints())
			clients.add(client)
			println("Client connected: ${client.remoteAddress}")
		} catch (e: GdxRuntimeException) {
			println(e.cause?.message)
		}
		if (!running) return else acceptSocket()
	}

	override fun dispose() {
		running = false
	}

	private fun closeAll() {
		try { socket.dispose() } catch (e: GdxRuntimeException) {}
		clients.forEach {
			try { it.dispose() } catch (e: GdxRuntimeException) {}
		}
		clients.clear()
	}
}
