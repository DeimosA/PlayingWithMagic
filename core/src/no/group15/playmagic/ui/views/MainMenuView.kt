package no.group15.playmagic.ui.views

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.graphics.use
import no.group15.playmagic.ui.controllers.GamePresenter


class MainMenuView(
	private val appContext: Game,
	private val batch: SpriteBatch
) : Screen {


	private lateinit var font: BitmapFont
	private val glyph = GlyphLayout()
	// Reference height of menus
	private val refMenuHeight = 720f
	// Extend to support 5:4 through 32:9 ratios
	private val viewport = ExtendViewport(
		900f, refMenuHeight, 2560f, refMenuHeight
	)


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

		viewport.apply()
		batch.use(viewport.camera) {
			font.draw(
				batch,
				glyph,
				viewport.worldWidth / 2 - glyph.width / 2,
				refMenuHeight / 2 + glyph.height / 2
			)
		}
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun resize(width: Int, height: Int) {
		viewport.update(width, height, true)
	}

	override fun hide() {
		dispose()
	}

	override fun dispose() {
		font.dispose()
	}
}
