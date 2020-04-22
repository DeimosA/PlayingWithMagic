package no.group15.playmagic

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ktx.async.KtxAsync
import no.group15.playmagic.server.Server

class PlayMagicServer(private val logLevel: Int) : ApplicationListener {

	private lateinit var server: Server

	override fun create() {
		Gdx.app.logLevel = logLevel
		KtxAsync.initiate()
		// Respond to TERM signal
		Runtime.getRuntime().addShutdownHook(object : Thread() {
			override fun run() = runBlocking {
				Gdx.app.exit()
				delay(1000)
			}
		})
		server = Server()
		server.start()
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
