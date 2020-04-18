package no.group15.playmagic.ui.controllers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.inject.Context
import ktx.log.*
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.ecs.engineFactory
import no.group15.playmagic.ecs.loadGameAssets
import no.group15.playmagic.network.Client
import no.group15.playmagic.network.NetworkContext
import no.group15.playmagic.server.Server
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.views.GameView
import no.group15.playmagic.ui.views.MainMenuView


class GamePresenter(
	private val appContext: PlayMagic,
	private val injectContext: Context,
	private val networkContext: NetworkContext
) : AppState {

	private val batch: SpriteBatch = injectContext.inject()
	private val inputMultiplexer: InputMultiplexer = injectContext.inject()
	private val engineViewHeight = 10f
	private val engineViewport = ExtendViewport(
		4 / 3f * engineViewHeight, engineViewHeight, 21 / 9f * engineViewHeight, engineViewHeight
	)
	private val assetManager = AssetManager()
	private lateinit var engine: Engine
	private lateinit var gameView: GameView

	private var server: Server? = null
	private lateinit var client: Client

	override fun create() {
		debug { "Rendering thread tid: ${Thread.currentThread().id}" }
		server = networkContext.server
		server?.start()

		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		loadGameAssets(assetManager)
		assetManager.finishLoading()
		engine = engineFactory(engineViewport, batch, assetManager)
		gameView = GameView(assetManager, inputMultiplexer)

		client = networkContext.client
		client.connect()
		injectContext.register {
			bindSingleton(client)
		}
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
		appContext.setAppState(MainMenuView(appContext, injectContext))
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun dispose() {
		server?.dispose()
		injectContext.remove<Client>()
		gameView.dispose()
		assetManager.dispose()
	}
}
