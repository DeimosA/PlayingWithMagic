package no.group15.playmagic.server

import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ktx.async.newSingleThreadAsyncContext
import ktx.collections.*
import java.io.IOException


/**
 * Server side client
 */
class ServerClient(
	val id: Int,
	private val socket: Socket,
	private val server: Server
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
			val line = reader.readLine()
			// TODO do something with line
		} catch (e: IOException) {
			// TODO connection lost?
		}
		receive()
	}

	private fun sendWelcome() {
		val message = Server.Message(id, Server.Message.Type.WELCOME, "Connected to server")
		message.params = gdxMapOf()
		// tick rate, game map, etc
		message.params!!["tickRate"] = server.config.tickRate

		writeLine(json.toJson(message))
	}

	fun writeLine(string: String) {
		try {
			writer.write(string)
			writer.newLine()
			writer.flush()
		} catch (e: IOException) {
			// TODO close connection?
		}
	}

	override fun dispose() {
		// TODO send goodbye message?
		try { socket.dispose() } catch (e: RuntimeException) {}
		try { reader.close() } catch (e: RuntimeException) {}
		try { writer.close() } catch (e: RuntimeException) {}
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
