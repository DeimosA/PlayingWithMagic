package no.group15.playmagic.ui.views

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import no.group15.playmagic.ui.controllers.GamePresenter


class MainMenuView(
	private val appContext: Game,
	private val batch: SpriteBatch
) : Screen {


	private lateinit var font: BitmapFont
	private val glyph = GlyphLayout()
	private val menuWidth = 1280f
	private val menuHeight = 720f
	private val viewPort = ExtendViewport(menuWidth, menuHeight, menuWidth, menuHeight)


	override fun show() {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
		font = BitmapFont()
		font.data.setScale(8f)
		glyph.setText(font, "Play!")
	}

	override fun render(deltaTime: Float) {
		if (Gdx.input.justTouched()) {
			appContext.screen = GamePresenter(appContext, batch)
		}

		viewPort.apply()
		batch.projectionMatrix = viewPort.camera.combined
		batch.begin()
		font.draw(
			batch,
			glyph,
			menuWidth / 2 - glyph.width / 2,
			menuHeight / 2 + glyph.height / 2
		)
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
		font.dispose()
	}
}
