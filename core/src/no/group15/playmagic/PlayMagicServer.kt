package no.group15.playmagic

import com.badlogic.gdx.ApplicationListener
import no.group15.playmagic.server.Server

class PlayMagicServer : ApplicationListener {

	private lateinit var server: Server

	override fun create() {
		server = Server()
		server.run()
	}

	override fun render() {
	}

	override fun resize(width: Int, height: Int) {
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun dispose() {
		server.dispose()
	}
}
