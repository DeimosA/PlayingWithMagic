package no.group15.playmagic.ui.views

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.graphics.use
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.controllers.GamePresenter
import no.group15.playmagic.utils.assets.MenuAssets
import no.group15.playmagic.utils.assets.loadAssets


class MainMenuView(
	private val appContext: PlayMagic,
	private val batch: SpriteBatch,
	private val inputMultiplexer: InputMultiplexer
) : AppState {

	// Reference height of menus
	private val viewHeight = 720f
	// Extend to support 5:4 through 32:9 ratios
	private val viewport = ExtendViewport(
		5 / 4f * viewHeight, viewHeight, 32 / 9f * viewHeight, viewHeight
	)
	private val assetManager = AssetManager()

	private lateinit var menuFont: BitmapFont
	private lateinit var titleFontBig: BitmapFont
	private lateinit var titleFontSmall: BitmapFont
	private val playGlyph = GlyphLayout()
	private val title1 = GlyphLayout()
	private val title1pos = Vector2(50f, viewHeight - 50)
	private val title2 = GlyphLayout()
	private val title2pos = Vector2(240f, viewHeight - 146)
	private val title3 = GlyphLayout()
	private val title3pos = Vector2(50f, viewHeight - 155)

	private lateinit var clickSound: Sound

	private val menuInput = object : InputAdapter() {
		override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			clickSound.play()
			return true
		}
		override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			appContext.appState = GamePresenter(appContext, batch, inputMultiplexer)
			return true
		}
	}

	override fun create() {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)

		assetManager.registerFreeTypeFontLoaders()
		loadAssets<MenuAssets>(assetManager)
		assetManager.finishLoading()

		menuFont = assetManager.get(MenuAssets.DRAGONFLY_70.desc.fileName)
		titleFontBig = assetManager.get(MenuAssets.DRAGONFLY_120.desc.fileName)
		titleFontSmall = assetManager.get(MenuAssets.DRAGONFLY_59.desc.fileName)

		playGlyph.setText(menuFont, "Play!")
		title1.setText(titleFontBig, "Playing")
		title2.setText(titleFontSmall, "with")
		title3.setText(titleFontBig, "Magic")

		clickSound = assetManager.get(MenuAssets.SOUND_CLICK.desc.fileName)
		inputMultiplexer.addProcessor(menuInput)

		viewport.apply()
	}

	override fun update(deltaTime: Float) {
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

	override fun resize(width: Int, height: Int) {
		viewport.update(width, height, true)
	}

	override fun back() {
		Gdx.app.exit()
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun dispose() {
		assetManager.dispose()
		inputMultiplexer.removeProcessor(menuInput)
	}
}
