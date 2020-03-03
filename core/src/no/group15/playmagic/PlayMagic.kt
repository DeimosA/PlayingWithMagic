package no.group15.playmagic

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import no.group15.playmagic.ui.views.MainMenuView


class PlayMagic : Game() {


	private var batch: SpriteBatch? = null


	override fun create() {
		batch = SpriteBatch()
		Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
		setScreen(MainMenuView(this, batch!!))
	}

	override fun render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		super.render()
	}

	override fun dispose() {
		super.dispose()
		batch!!.dispose()
	}
}
