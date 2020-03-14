package no.group15.playmagic

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.views.MainMenuView


class PlayMagic : ApplicationListener {

	private lateinit var batch: SpriteBatch

	var appState: AppState? = null
		set(value) {
			appState?.dispose()
			field = value
			appState?.create()
			appState?.resize(Gdx.graphics.width, Gdx.graphics.height)
		}

	private val commonInput = object : InputAdapter() {
		override fun keyUp(keycode: Int): Boolean {
			if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
				appState?.back()
				return true
			}
			return false
		}
	}

	override fun create() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		batch = SpriteBatch()
		Gdx.input.setCatchKey(Input.Keys.BACK, true)
		val inputMultiplexer = InputMultiplexer()
		inputMultiplexer.addProcessor(commonInput)
		Gdx.input.inputProcessor = inputMultiplexer
		appState = MainMenuView(this, batch, inputMultiplexer)
	}

	override fun render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		appState?.update(Gdx.graphics.deltaTime)
	}

	override fun resize(width: Int, height: Int) {
		appState?.resize(width, height)
	}

	override fun pause() {
		appState?.pause()
	}

	override fun resume() {
		appState?.resume()
	}

	override fun dispose() {
		batch.dispose()
		appState?.dispose()
	}
}
