package no.group15.playmagic.server

import com.badlogic.gdx.net.Socket
import com.badlogic.gdx.utils.Disposable


/**
 * Server side client
 */
class ServerClient(
	val id: Int,
	private val socket: Socket
) : Disposable {

	private val inputStream = socket.inputStream
	private val outputStream = socket.outputStream

	init {
		// TODO send welcome message
		val writer = outputStream.bufferedWriter()
		writer.write("Welcome!\n")
		writer.close()
		// TODO start listening to client
	}

	override fun dispose() {
		// TODO send goodbye message?
		socket.dispose()
	}
}
