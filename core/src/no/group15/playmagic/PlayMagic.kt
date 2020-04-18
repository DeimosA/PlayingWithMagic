package no.group15.playmagic

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.async.KtxAsync
import ktx.inject.*
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.views.MainMenuView


class PlayMagic(private val logLevel: Int) : ApplicationListener {

	private lateinit var appState: AppState
	private lateinit var injectContext: Context

	private val commonInput = object : InputAdapter() {
		override fun keyUp(keycode: Int): Boolean {
			if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
				appState.back()
				return true
			}
			return false
		}
	}

	override fun create() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.input.setCatchKey(Input.Keys.BACK, true)
		Gdx.app.logLevel = logLevel
		KtxAsync.initiate()

		injectContext = Context()
		injectContext.register {
			bindSingleton(SpriteBatch())
			bindSingleton(InputMultiplexer())
		}

		val inputMultiplexer: InputMultiplexer = injectContext.inject()
		inputMultiplexer.addProcessor(commonInput)
		Gdx.input.inputProcessor = inputMultiplexer
		initAppState(MainMenuView(this, injectContext))
	}

	override fun render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		appState.update(Gdx.graphics.deltaTime)
	}

	fun setAppState(newState: AppState) {
		appState.dispose()
		initAppState(newState)
	}
	private fun initAppState(appState: AppState) {
		this.appState = appState
		this.appState.create()
		this.appState.resize(Gdx.graphics.width, Gdx.graphics.height)
	}

	override fun resize(width: Int, height: Int) {
		appState.resize(width, height)
	}

	override fun pause() {
		appState.pause()
	}

	override fun resume() {
		appState.resume()
	}

	override fun dispose() {
		injectContext.dispose()
		appState.dispose()
	}
}
