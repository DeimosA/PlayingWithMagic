package no.group15.playmagic.ui.controllers

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch


class GamePresenter(
	private val appContext: Game,
	private val batch: SpriteBatch
) : Screen {


	private lateinit var img: Texture


	override fun show() {
		img = Texture("badlogic.jpg")
	}

	override fun render(deltaTime: Float) {
		batch.begin()
		batch.draw(img, 0f, 0f)
		batch.end()
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun resize(width: Int, height: Int) {
	}

	override fun hide() {
		dispose()
	}

	override fun dispose() {
		img.dispose()
	}


}
