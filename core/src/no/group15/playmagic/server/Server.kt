package no.group15.playmagic.server

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.ServerSocket
import com.badlogic.gdx.net.ServerSocketHints
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.net.SocketHints
import com.badlogic.gdx.utils.*
import kotlinx.coroutines.*
import ktx.async.*
import ktx.collections.*
import ktx.log.*
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.commands.RemovePlayerCommand
import no.group15.playmagic.commandstream.commands.ServerMessageCommand
import no.group15.playmagic.commandstream.commands.SpawnPlayerCommand
import no.group15.playmagic.ecs.GameMap
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
	private val gameMap = GameMap()
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

		// Start accepting incoming connections
		acceptSocket()

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
		// -handle incoming commands
		// -send commands to other clients
		launch {
			for (client in ObjectMap.Values(clients)) {
				sendToAllExcept(client.id, client.receiveQueue)
				client.receiveQueue.clear()
			}
		}
	}

	/**
	 * Listen for incoming connection
	 */
	private fun acceptSocket() {
		thread {
			while (running) {
				try {
					if (socket == null) {
						log.error { "Inconsistent state?: running is $running wile socket is $socket" }
						break
					}
					val clientSocket = socket?.accept(SocketHints()) ?: break
					acceptClient(clientSocket)

				} catch (e: GdxRuntimeException) {
					log.error { e.cause?.message ?: "Error while accepting client: ${e.message}" }
				}
			}
		}
	}

	/**
	 * Accept or reject client [socket] depending on if server is full or not
	 */
	private fun acceptClient(socket: Socket) = launch {
		if (clients.size < config.maxPlayers) {
			val id = nextClientId
			val serverClient = ServerClient(id, socket, this@Server, gameMap.getRandomSpawn())
			clients[id] = serverClient
			log.info { "Client with id ${serverClient.id} connected from ${socket.remoteAddress},  Client count: ${clients.size}" }
			// Notify players
			spawnPlayers(serverClient)

		} else {
			// Reject client
			log.debug { "Client tried to connect while server was full" }
			val array = arrayOfCommands(ServerMessageCommand(
				ServerMessageCommand.Action.REJECTED
			))
			ServerClient.rejectClient(socket, json.toJson(array))
		}
	}

	/**
	 * Remove client with [id]
	 */
	fun removeClient(id: Int) = launch {
		val client = clients.remove(id)
		if (client != null) {
			// Free spawn point
			gameMap.returnSpawn(client.spawnPosition)
			client.dispose()
			// Command other players to remove the player
			sendToAll(arrayOfCommands(RemovePlayerCommand(id)))
		}
	}

	/**
	 * Spawn [playerClient] on all players and all players on [playerClient]
	 */
	private fun spawnPlayers(playerClient: ServerClient) {
		val newPlayer = arrayOfCommands(SpawnPlayerCommand(
			playerClient.id,
			playerClient.spawnPosition.x,
			playerClient.spawnPosition.y
		))
		val oldPlayers = arrayOfCommands()

		for (client in clients.values()) {
			if (client.id != playerClient.id) {
				client.sendCommands(newPlayer)
				oldPlayers.add(SpawnPlayerCommand(
					client.id,
					client.spawnPosition.x,
					client.spawnPosition.y
				))
			}
		}
		log.debug { "Sending spawn commands: ${newPlayer.size} new, ${oldPlayers.size} old" }

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
	 * Utility function for creating command array
	 */
	fun arrayOfCommands(vararg commands: Command): GdxArray<Command> {
		val array = gdxArrayOf<Command>()
		for (command in commands) {
			array.add(command)
		}
		return array
	}

	override fun dispose() {
		running = false

		launch {
			// Send goodbye message to all clients
			sendToAll(arrayOfCommands(ServerMessageCommand(
				ServerMessageCommand.Action.SHUTDOWN
			)))
			delay(100)
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
