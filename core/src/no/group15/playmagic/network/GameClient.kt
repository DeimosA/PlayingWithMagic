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
import no.group15.playmagic.commands.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread


class GameClient(
	injectContext: Context,
	config: ClientConfig = ClientConfig()
) : Disposable, CommandReceiver, CoroutineScope by CoroutineScope(newAsyncContext(2)) {

	val socket: Socket by lazy {
		Gdx.net.newClientSocket(Net.Protocol.TCP, config.host, config.port, SocketHints())
	}
	private val log = logger<GameClient>()
	private var reader: BufferedReader? = null
	private var writer: BufferedWriter? = null
	private val json = Json()
	private val commandDispatcher: CommandDispatcher = injectContext.inject()
	private val commandQueue = gdxArrayOf<Command>()

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
	}

	/**
	 * Connect and start listening
	 */
	fun connect() = launch {
		try {
			socket
			log.info { "Connected to server: ${socket.remoteAddress}" }
			writer = socket.outputStream.bufferedWriter()
			reader = socket.inputStream.bufferedReader()
			launch { receiveCommands() }

		} catch (e: GdxRuntimeException) {
			log.error { e.cause?.message ?: "Error opening client socket: ${e.message}" }
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
	private tailrec fun receiveCommands() {
		try {
//			log.debug { "Reader ready? ${reader?.ready()}" }
			val line = reader?.readLine()
			if (line == null) {
				log.error { "Reached end of stream" }
				// TODO connection lost
				return
			} else {
				handleCommands(line)
			}
		} catch (e: IOException) {
			log.error { "Error while reading from input stream: ${e.message}" }
			// TODO connection lost
		}
		if (!running) return else receiveCommands()
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
					// Notify player and pass on command
					id = command.playerId
					tickRate = command.tickRate
					log.debug { "Client configured with id $id and tick time ${tickTimeNano / 1000000f} ms" }
					val message = createAsync(Command.Type.MESSAGE).await() as MessageCommand
					message.text = "Connected to server"
					send(message)
					send(command)
				}
				is SpawnPlayerCommand -> {
					// Notify player and pass on command
					val message = createAsync(Command.Type.MESSAGE).await() as MessageCommand
					message.text = "Player joined"
					send(message)
					send(command)
				}
				is SendPositionCommand -> {
					// Convert to position command
					val position = createAsync(Command.Type.POSITION).await() as PositionCommand
					position.playerId = command.playerId
					position.x = command.x
					position.y = command.y
					send(position)
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
//			log.debug { "Sending ${array.size} commands" }
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
			commandQueue.add(command)
		}
	}

	override fun dispose() {
		running = false
		// TODO send disconnect?
		try { socket.dispose() } catch (e: Exception) {}
		try { reader?.close() } catch (e: Exception) {}
		try { writer?.close() } catch (e: Exception) {}
	}
}
