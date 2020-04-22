package no.group15.playmagic.server

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ktx.async.newSingleThreadAsyncContext
import ktx.collections.*
import no.group15.playmagic.commands.Command
import no.group15.playmagic.commands.ConfigCommand
import java.io.IOException
import java.lang.Exception


/**
 * Server side client
 */
class ServerClient(
	val id: Int,
	private val socket: Socket,
	private val server: Server,
	val position: Vector2
) : Disposable, CoroutineScope by CoroutineScope(newSingleThreadAsyncContext()) {

	private val reader = socket.inputStream.bufferedReader()
	private val writer = socket.outputStream.bufferedWriter()
	private val json = server.json


	init {
		launch { receive() }
		sendWelcome()
	}

	private tailrec fun receive() {
		try {
			val line = reader.readLine() ?: return
			server.handleMessage(line)
		} catch (e: IOException) {
			// TODO connection lost?
		}
		receive()
	}

	private fun sendWelcome() {
		val command = ConfigCommand()
		// Send id, tick rate, game map, spawn position, etc
		command.playerId = id
		command.tickRate = server.config.tickRate
		command.spawnPosX = position.x
		command.spawnPosY = position.y
		val array = gdxArrayOf<Command>()
		array.add(command)
		sendCommands(array)
	}

	fun sendCommands(array: GdxArray<Command>) {
		try {
			writer.write(json.toJson(array))
			writer.newLine()
			writer.flush()
		} catch (e: IOException) {
			// TODO close connection?
		}
	}

	override fun dispose() {
		// TODO send goodbye message?
		try { socket.dispose() } catch (e: Exception) {}
		try { reader.close() } catch (e: Exception) {}
		try { writer.close() } catch (e: Exception) {}
	}

	companion object {
		fun rejectClient(socket: Socket, json: String) {
			val writer = socket.outputStream.bufferedWriter()
			writer.write(json)
			writer.close()
			socket.dispose()
		}
	}
}
