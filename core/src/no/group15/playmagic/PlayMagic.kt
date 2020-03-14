package no.group15.playmagic

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import no.group15.playmagic.ui.views.MainMenuView


class PlayMagic : Game() {

	private lateinit var batch: SpriteBatch


	override fun create() {
		batch = SpriteBatch()
		Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
		val inputMultiplexer = InputMultiplexer()
		Gdx.input.inputProcessor = inputMultiplexer
		setScreen(MainMenuView(this, batch, inputMultiplexer))
	}

	override fun render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		super.render()
	}

	override fun dispose() {
		super.dispose()
		batch.dispose()
	}
}
