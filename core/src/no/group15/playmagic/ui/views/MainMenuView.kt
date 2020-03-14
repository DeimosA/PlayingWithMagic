package no.group15.playmagic.ui.views

import com.badlogic.gdx.*
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.graphics.use
import no.group15.playmagic.ui.controllers.GamePresenter


class MainMenuView(
	private val appContext: Game,
	private val batch: SpriteBatch,
	private val inputMultiplexer: InputMultiplexer
) : Screen {

	// Reference height of menus
	private val refMenuHeight = 720f
	// Extend to support 5:4 through 32:9 ratios
	private val viewport = ExtendViewport(
		900f, refMenuHeight, 2560f, refMenuHeight
	)
	private lateinit var menuFont: BitmapFont
	private lateinit var titleFontBig: BitmapFont
	private lateinit var titleFontSmall: BitmapFont
	private val playGlyph = GlyphLayout()
	private val title1 = GlyphLayout()
	private val title1pos = Vector2(50f, refMenuHeight - 50)
	private val title2 = GlyphLayout()
	private val title2pos = Vector2(240f, refMenuHeight - 146)
	private val title3 = GlyphLayout()
	private val title3pos = Vector2(50f, refMenuHeight - 155)

	private lateinit var clickSound: Sound

	private val menuInput = object : InputAdapter() {
		override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			clickSound.play()
			return true
		}
		override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			appContext.screen = GamePresenter(appContext, batch, inputMultiplexer)
			return true
		}
	}

	override fun show() {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)

		val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Dragonfly-z9jl.ttf"))
		val parameter = FreeTypeFontParameter()
		parameter.size = 70
		menuFont = generator.generateFont(parameter)
		parameter.size = 120
		titleFontBig = generator.generateFont(parameter)
		parameter.size = 59
		titleFontSmall = generator.generateFont(parameter)
		generator.dispose()

		playGlyph.setText(menuFont, "Play!")
		title1.setText(titleFontBig, "Playing")
		title2.setText(titleFontSmall, "with")
		title3.setText(titleFontBig, "Magic")

		clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.wav"))
//		clickSound.play()
		inputMultiplexer.addProcessor(menuInput)

		viewport.apply()
	}


	override fun render(deltaTime: Float) {
		batch.use(viewport.camera) {
			menuFont.draw(
				batch,
				playGlyph,
				viewport.worldWidth - playGlyph.width - 150,
				playGlyph.height + 150
			)
			titleFontBig.draw(batch, title1, title1pos.x, title1pos.y)
			titleFontSmall.draw(batch, title2, title2pos.x, title2pos.y)
			titleFontBig.draw(batch, title3, title3pos.x, title3pos.y)
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
		menuFont.dispose()
		titleFontBig.dispose()
		titleFontSmall.dispose()
		clickSound.dispose()
		inputMultiplexer.removeProcessor(menuInput)
	}
}
