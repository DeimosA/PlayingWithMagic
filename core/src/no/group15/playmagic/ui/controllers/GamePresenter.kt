package no.group15.playmagic.ui.controllers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import no.group15.playmagic.ecs.engineFactory


class GamePresenter(
	private val appContext: Game,
	private val batch: SpriteBatch
) : Screen {


	private val viewport = ExtendViewport(
		4/3f * 10, 10f, 21/9f * 10, 10f
	)
	private lateinit var engine: Engine


	override fun show() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		engine = engineFactory(batch, viewport)
	}

	override fun render(deltaTime: Float) {
		viewport.apply()
		batch.projectionMatrix = viewport.camera.combined
		batch.begin()
		engine.update(deltaTime)
		batch.end()
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun resize(width: Int, height: Int) {
		viewport.update(width, height, false)
	}

	override fun hide() {
		dispose()
	}

	override fun dispose() {
	}
}
