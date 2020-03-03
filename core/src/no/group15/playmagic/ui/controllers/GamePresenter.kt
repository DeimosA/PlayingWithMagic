package no.group15.playmagic.ui.controllers

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport


class GamePresenter(
	private val appContext: Game,
	private val batch: SpriteBatch
) : Screen {


	private val viewPort = FitViewport(16f, 9f)
	private lateinit var img: Texture


	override fun show() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		img = Texture("badlogic.jpg")
	}

	override fun render(deltaTime: Float) {
		viewPort.apply()
		batch.projectionMatrix = viewPort.camera.combined
		batch.begin()
		batch.draw(img, 1f, 1f, 3f, 3f)
		batch.end()
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun resize(width: Int, height: Int) {
		viewPort.update(width, height, true)
	}

	override fun hide() {
		dispose()
	}

	override fun dispose() {
		img.dispose()
	}


}
