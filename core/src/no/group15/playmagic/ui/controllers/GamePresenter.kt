package no.group15.playmagic.ui.controllers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.inject.*
import ktx.log.*
import ktx.freetype.*
import no.group15.playmagic.network.GameClient
import no.group15.playmagic.network.NetworkContext
import no.group15.playmagic.server.Server
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.commands.CommandDispatcher
import no.group15.playmagic.ecs.engineFactory
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.views.GameView
import no.group15.playmagic.ui.views.MainMenuView
import no.group15.playmagic.utils.assets.*


class GamePresenter(
	private val appContext: PlayMagic,
	private val injectContext: Context,
	private val networkContext: NetworkContext
) : AppState {

	private val batch: SpriteBatch = injectContext.inject()
	private val inputMultiplexer: InputMultiplexer = injectContext.inject()
	private val assetManager: AssetManager = injectContext.inject()

	private val engineViewHeight = 10f
	private val engineViewport = ExtendViewport(
		4 / 3f * engineViewHeight, engineViewHeight, 21 / 9f * engineViewHeight, engineViewHeight
	)

	private lateinit var engine: Engine
	private lateinit var gameView: GameView
	private var server: Server? = null
	private lateinit var client: GameClient


	override fun create() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		debug { "Rendering thread tid: ${Thread.currentThread().id}" }

		// Start server if any
		server = networkContext.server
		server?.start()

		injectContext.register {
			bindSingleton(CommandDispatcher())
//			bindSingleton(networkContext.client)
		}

		assetManager.registerFreeTypeFontLoaders()
		assetManager.load(FontAssets.DRAGONFLY_25.desc)
		loadAssets<GameAssets>(assetManager)
		loadAssets<VirtualStickAssets>(assetManager)
		assetManager.finishLoading()

		gameView = GameView(injectContext)

		// Connect client to server
		client = networkContext.client
		client.connect()

		engine = engineFactory(injectContext, engineViewport)
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
		client.dispose()
		injectContext.remove<CommandDispatcher>()
		injectContext.remove<GameClient>()
		gameView.dispose()
		assetManager.clear()
	}
}
