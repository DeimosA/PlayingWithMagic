package no.group15.playmagic.network

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.net.SocketHints
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.TimeUtils
import kotlinx.coroutines.*
import ktx.async.*
import ktx.collections.*
import ktx.inject.Context
import ktx.json.*
import ktx.log.*
import no.group15.playmagic.commandstream.Command
import no.group15.playmagic.commandstream.CommandDispatcher
import no.group15.playmagic.commandstream.CommandReceiver
import no.group15.playmagic.commandstream.commands.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread


class GameClient(
	injectContext: Context,
	private val config: ClientConfig = ClientConfig()
) : Disposable, CommandReceiver, CoroutineScope by CoroutineScope(newSingleThreadAsyncContext()) {

	private var socket: Socket? = null
	private val log = logger<GameClient>()
	private var reader: BufferedReader? = null
	private var writer: BufferedWriter? = null
	private val json = Json()
	private val commandDispatcher: CommandDispatcher = injectContext.inject()
	private val commandQueue = gdxArrayOf<Command>()
	private var latestPosition: SendPositionCommand? = null

	private var running = false
	private var tickRate = 1f
		set(value) {
			tickTimeNano = (1000000000L / value).toLong()
			field = value
		}
	private var tickTimeNano = (1000000000L / tickRate).toLong()
	private var id: Int = 0


	init {
		// Register receiver
	    Command.Type.SEND_POSITION.receiver = this
		Command.Type.SEND_BOMB_POSITION.receiver = this
		Command.Type.SEND_KILL_PLAYER.receiver = this
		Command.Type.SEND_DESTROY.receiver = this
	}

	/**
	 * Connect and start listening
	 */
	fun connect() = launch {
		try {
			socket = Gdx.net.newClientSocket(Net.Protocol.TCP, config.host, config.port, SocketHints())
			log.info { "Connected to server: ${socket?.remoteAddress}" }
			writer = socket?.outputStream?.bufferedWriter()
			reader = socket?.inputStream?.bufferedReader()
			receiveCommands()

		} catch (e: GdxRuntimeException) {
			log.error { e.cause?.message ?: "Error opening client socket: ${e.message}" }
			KtxAsync.launch {
				val command = commandDispatcher.createCommand(Command.Type.MESSAGE) as MessageCommand
				command.text = "Could not connect to server"
				commandDispatcher.send(command)
			}
			return@launch
		}

		running = true
		thread {
			val nanosPerSec = 1000000000L
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
					log.debug { "Network client is lagging behind: $deltaTime" }
				}

				clientTick(deltaTime / nanosPerSec.toFloat())
			}
		}
	}

	private fun clientTick(deltaTime: Float) = launch {
		if (latestPosition != null) commandQueue.add(latestPosition)
		if (commandQueue.size > 0) {
			// Send command array
			sendCommands(commandQueue)
			// Free commands and clear list
			commandQueue.forEach { it.free() }
			commandQueue.clear()
		}
	}

	/**
	 * Listen for incoming message from the server
	 */
	private fun receiveCommands() {
		thread {
			while (running) {
				try {
					val line = reader?.readLine()
					if (line == null) {
						log.error { "Reached end of stream" }
						running = false
						// TODO connection lost
					} else {
						handleCommands(line)
					}
				} catch (e: IOException) {
					log.error { "Error while reading from input stream: ${e.message}" }
					running = false
					// TODO connection lost
				}
			}
		}
	}

	/**
	 * Handle incoming commands from the server
	 */
	private fun handleCommands(string: String) = launch {

		fun send(command: Command) {
			KtxAsync.launch { commandDispatcher.send(command) }
		}

		fun createAsync(type: Command.Type): Deferred<Command?> = async(Dispatchers.KTX) {
			commandDispatcher.createCommand(type)
		}

		val commands = json.fromJson<GdxArray<Command>>(string)
		for (command in commands) {
			// Check if we need to process anything locally
			when (command) {
				is ConfigCommand -> {
					// Pass on command and notify player
					send(command)
					id = command.playerId
					tickRate = command.tickRate
					latestPosition = SendPositionCommand(
						command.spawnPosX,
						command.spawnPosY,
						command.playerId
					)
					log.debug { "Client configured with id $id and tick time ${tickTimeNano / 1000000f} ms" }
					val message = createAsync(Command.Type.MESSAGE).await() as MessageCommand
					message.text = "Connected to server"
					send(message)
				}
				is SpawnPlayerCommand -> {
					// Pass on command and notify player
					send(command)
					val message = createAsync(Command.Type.MESSAGE).await() as MessageCommand
					message.text = "Player joined"
					send(message)
				}
				is RemovePlayerCommand -> {
					// Pass on command and notify player
					send(command)
					val message = createAsync(Command.Type.MESSAGE).await() as MessageCommand
					message.text = "Player disconnected"
					send(message)
				}
				is SendPositionCommand -> {
					// Convert to position command
					val position = createAsync(Command.Type.POSITION).await() as PositionCommand
					position.playerId = command.playerId
					position.x = command.x
					position.y = command.y
					send(position)
				}
				is SendBombPositionCommand -> {
					// Convert to bomb position command
					val bombPosition = createAsync(Command.Type.BOMB_POSITION).await() as BombPositionCommand
					bombPosition.x = command.x
					bombPosition.y = command.y
					send(bombPosition)
				}
				is SendKillPlayerCommand -> {
					// Convert to kill player command
					val kill = createAsync(Command.Type.KILL_PLAYER).await() as KillPlayerCommand
					kill.playerId = command.playerId
					send(kill)
				}
				is SendDestroyCommand -> {
					// Convert to destroy command
					val destroy = createAsync(Command.Type.DESTROY).await() as DestroyCommand
					destroy.x = command.x
					destroy.y = command.y
					send(destroy)
				}
				is ServerMessageCommand -> {
					when (command.action) {
						ServerMessageCommand.Action.REJECTED -> {
							val message = createAsync(Command.Type.MESSAGE).await() as MessageCommand
							message.text = "Server is full"
							send(message)
							dispose()
						}
						ServerMessageCommand.Action.SHUTDOWN -> {
							val message = createAsync(Command.Type.MESSAGE).await() as MessageCommand
							message.text = "Server is shutting down"
							send(message)
							send(createAsync(Command.Type.RESET_GAME).await() as ResetGameCommand)
							dispose()
						}
					}
				}

				else -> send(command)
			}
		}
	}

	/**
	 * Send commands to server
	 */
	private fun sendCommands(array: GdxArray<Command>) {
		try {
			writer?.write(json.toJson(array))
			writer?.write("\n")
			writer?.flush()
		} catch (e: IOException) {
			log.error { "Error sending commands to server: ${e.message}" }
			// TODO close connection?
		}
	}

	/**
	 * Receive commands from game
	 */
	override fun receive(command: Command) {
		launch {
			when (command) {
				is SendPositionCommand -> {
					latestPosition = command
				}
				else -> commandQueue.add(command)
			}
		}
	}

	override fun dispose() {
		running = false
		try { socket?.dispose() } catch (e: Exception) {}
		try { reader?.close() } catch (e: Exception) {}
		try { writer?.close() } catch (e: Exception) {}
	}
}
