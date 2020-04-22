package no.group15.playmagic.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.ServerSocket
import com.badlogic.gdx.net.ServerSocketHints
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.net.SocketHints
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.TimeUtils
import kotlinx.coroutines.*
import ktx.async.*
import ktx.collections.*
import ktx.json.*
import ktx.log.*
import no.group15.playmagic.commands.Command
import no.group15.playmagic.commands.SpawnPlayerCommand
import java.lang.Exception
import kotlin.concurrent.thread


class Server(
	val config: ServerConfig = ServerConfig()
) : Disposable, CoroutineScope by CoroutineScope(newSingleThreadAsyncContext()) {

	var running = false
		private set
	private var socket: ServerSocket? = null
	private val clients = gdxMapOf<Int, ServerClient>()
	private val log = logger<Server>()
	val json = Json()
	private val commandQueue = gdxArrayOf<Command>()
	private var nextClientId = 1
		get() = field++


	fun start() = launch {
		if (running) return@launch
		running = true

		val serverSocketHints = ServerSocketHints()
		serverSocketHints.acceptTimeout = 0
		try {
			socket = Gdx.net.newServerSocket(Net.Protocol.TCP, config.host, config.port, serverSocketHints)
			log.info { "Server started on tid ${Thread.currentThread().id}" }
		} catch (e: GdxRuntimeException) {
			log.error(e) { "Failed to start server" }
			running = false
			return@launch
		}

		// Accept incoming connections on its own thread
		val acceptExecutor = newSingleThreadAsyncContext()
		launch(acceptExecutor) {
			acceptSocket()
		}

		thread {
			val nanosPerSec = 1000000000L
			val tickTimeNano = (nanosPerSec / config.tickRate).toLong()
			var lastTime = TimeUtils.nanoTime()
			var sleepTime = 0L
			while (running) {
				// Calculate actual tick time and sleep accordingly
				val currentTime = TimeUtils.nanoTime()
				val deltaTime = currentTime - lastTime
				lastTime = currentTime
				val newSleepTime = tickTimeNano - (deltaTime - sleepTime)
				if (newSleepTime > 0) {
					Thread.sleep(TimeUtils.nanosToMillis(newSleepTime))
					sleepTime = TimeUtils.nanoTime() - lastTime
				} else {
					log.debug { "Server is lagging behind: $deltaTime" }
				}

				serverTick(deltaTime / nanosPerSec.toFloat())
			}
		}
	}

	private fun serverTick(deltaTime: Float) {
		// TODO
		//  -handle incoming commands
		//  -send commands to other clients
		launch {
			sendToAll(commandQueue)
			commandQueue.clear()
		}
	}

	/**
	 * Handle incoming data
	 */
	fun handleMessage(string: String) = launch {
		val array = json.fromJson<GdxArray<Command>>(string)
		commandQueue.addAll(array)
//		log.debug { string }
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
			val serverClient = ServerClient(id, socket, this@Server)
			clients[id] = serverClient
			log.info { "Client with id ${serverClient.id} connected from ${socket.remoteAddress},  Client count: ${clients.size}" }
			// Notify players
			spawnPlayers(serverClient)

		} else {
			// Reject client
			log.debug { "Client tried to connect while server was full" }
			// TODO redo reject client with command
//			ServerClient.rejectClient(socket, json.toJson(Message(0, Message.Type.REJECT, "Server is full")))
		}
	}

	/**
	 * Spawn [playerClient] on all players and all players on [playerClient]
	 */
	private fun spawnPlayers(playerClient: ServerClient) {
		val newPlayer = gdxArrayOf<Command>()
		val oldPlayers = gdxArrayOf<Command>()
		newPlayer.add(SpawnPlayerCommand(playerClient.id, playerClient.position.x, playerClient.position.y))

		for (client in clients.values()) {
			if (client.id != playerClient.id) {
				client.sendCommands(newPlayer)
				oldPlayers.add(SpawnPlayerCommand(client.id, client.position.x, client.position.y))
			}
		}

		playerClient.sendCommands(oldPlayers)
	}

	/**
	 * Send [array] to all players
	 */
	private fun sendToAll(array: GdxArray<Command>) {
		for (client in clients.values()) {
			client.sendCommands(array)
		}
	}

	/**
	 * Send [array] to all players except player with [playerId]
	 */
	private fun sendToAllExcept(playerId: Int, array: GdxArray<Command>) {
		for (client in clients.values()) {
			if (client.id != playerId) {
				client.sendCommands(array)
			}
		}
	}

	/**
	 * Remove client with [id]
	 */
	fun removeClient(id: Int) = launch {
		val client = clients.remove(id)
		client?.dispose()
	}

	override fun dispose() {
		running = false

		launch {
			// Close connections and cleanup
			try { socket?.dispose() } catch (e: Exception) {}
			clients.values().forEach {
				it.dispose()
			}
			clients.clear()
			socket = null
			log.info { "Server closed" }
		}
	}
}
