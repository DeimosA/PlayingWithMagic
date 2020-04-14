package no.group15.playmagic.ui.controllers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.ecs.engineFactory
import no.group15.playmagic.ecs.loadGameAssets
import no.group15.playmagic.network.Client
import no.group15.playmagic.server.Server
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.views.GameView
import no.group15.playmagic.ui.views.MainMenuView


class GamePresenter(
	private val appContext: PlayMagic,
	private val batch: SpriteBatch,
	private val inputMultiplexer: InputMultiplexer
) : AppState {

	private val engineViewHeight = 10f
	private val engineViewport = ExtendViewport(
		4 / 3f * engineViewHeight, engineViewHeight, 21 / 9f * engineViewHeight, engineViewHeight
	)
	private val assetManager = AssetManager()
	private lateinit var engine: Engine
	private lateinit var gameView: GameView

	private var server: Server? = null

	override fun create() {
//		server = Server()
//		val thread = Thread(server)
//		thread.start()

		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		loadGameAssets(assetManager)
		assetManager.finishLoading()
		engine = engineFactory(engineViewport, batch, assetManager)
		gameView = GameView(assetManager, inputMultiplexer)
		val client = Client()
	}

	override fun update(deltaTime: Float) {
		gameView.update(deltaTime)

		engine.update(deltaTime)

		gameView.render(batch)
	}

	override fun resize(width: Int, height: Int) {
		engineViewport.update(width, height, false)
		gameView.resize(width, height)
	}

	override fun back() {
		appContext.appState = MainMenuView(appContext, batch, inputMultiplexer)
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun dispose() {
		server?.dispose()
		gameView.dispose()
		assetManager.dispose()
	}
}
