package no.group15.playmagic.ui.views

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import no.group15.playmagic.ui.controllers.GamePresenter


class MainMenuView(
	private val appContext: Game,
	private val batch: SpriteBatch
) : Screen {


	private lateinit var font: BitmapFont
	private val glyph = GlyphLayout()


	override fun show() {
		font = BitmapFont()
		font.data.setScale(4f)
		glyph.setText(font, "Play!")
	}

	override fun render(deltaTime: Float) {
		if (Gdx.input.justTouched()) {
			appContext.screen = GamePresenter(appContext, batch)
		}

		batch.begin()
		font.draw(
			batch,
			glyph,
			Gdx.graphics.width / 2 - glyph.width / 2,
			Gdx.graphics.height / 2 + glyph.height / 2
		)
		batch.end()
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun resize(width: Int, height: Int) {
		// TODO update viewport
	}

	override fun hide() {
		dispose()
	}

	override fun dispose() {
		font.dispose()
	}
}
