package no.group15.playmagic.ui.views

import com.badlogic.gdx.*
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.graphics.use
import ktx.inject.Context
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.network.NetworkContext
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.controllers.GamePresenter
import no.group15.playmagic.ui.views.menulists.MainMenuList
import no.group15.playmagic.ui.views.widgets.MenuListWidget


class MainMenuView(
	private val appContext: PlayMagic,
	private val injectContext: Context
) : AppState {

	private val batch: SpriteBatch = injectContext.inject()
	private val inputMultiplexer: InputMultiplexer = injectContext.inject()
	// Reference height of menus
	private val refMenuHeight = 720f
	// Extend to support 5:4 through 32:9 ratios
	private val viewport = ExtendViewport(
		900f, refMenuHeight, 2560f, refMenuHeight
	)
	private lateinit var menuFont: BitmapFont
	private lateinit var titleFontBig: BitmapFont
	private lateinit var titleFontSmall: BitmapFont
	private val title1 = GlyphLayout()
	private val title1pos = Vector2(50f, refMenuHeight - 50)
	private val title2 = GlyphLayout()
	private val title2pos = Vector2(240f, refMenuHeight - 146)
	private val title3 = GlyphLayout()
	private val title3pos = Vector2(50f, refMenuHeight - 155)

	private lateinit var clickSound: Sound

	private var menuList: MenuListWidget? = null
	private var cursorPos = Vector2()

	private val menuInput = object : InputAdapter() {
		override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			clickSound.play()
			return true
		}
		override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			val cursor = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
			if(menuList?.contains(cursor.x, cursor.y) == true) {
				menuList?.click(cursor.x, cursor.y)
				return true
			}
			return false
		}
		override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
			cursorPos = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
			return true
		}
	}

	override fun create() {
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

		title1.setText(titleFontBig, "Playing")
		title2.setText(titleFontSmall, "with")
		title3.setText(titleFontBig, "Magic")

		clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.wav"))
//		clickSound.play()

		// TODO change to using asset manager after merge with master, also fix text size and hover mess
		val hoverBackground = TextureRegion(Texture("menuhoverbackground.png"))
		val width = 622f
		menuList = MainMenuList(
			Rectangle(viewport.worldWidth - width - 50f, 50f, width, 620f),
			menuFont,
			hoverBackground,
			this
		)
		inputMultiplexer.addProcessor(menuInput)
		viewport.apply()
	}

	override fun update(deltaTime: Float) {
		if (menuList?.contains(cursorPos.x, cursorPos.y) == true) {
			menuList?.hover(cursorPos.x, cursorPos.y)
		}
		menuList?.update(deltaTime)

		batch.use(viewport.camera) {
			titleFontBig.draw(batch, title1, title1pos.x, title1pos.y)
			titleFontSmall.draw(batch, title2, title2pos.x, title2pos.y)
			titleFontBig.draw(batch, title3, title3pos.x, title3pos.y)

			menuList?.render(batch)
		}
	}

	fun setMenuList(menuList: MenuListWidget) {
		this.menuList?.dispose()
		this.menuList = menuList
	}

	fun startGame(networkContext: NetworkContext) {
		appContext.setAppState(GamePresenter(appContext, injectContext, networkContext))
	}

	override fun resize(width: Int, height: Int) {
		viewport.update(width, height, true)
		menuList?.resize(viewport.worldWidth, viewport.worldHeight)
	}

	override fun back() {
		menuList?.back() ?: Gdx.app.exit()
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun dispose() {
		menuFont.dispose()
		titleFontBig.dispose()
		titleFontSmall.dispose()
		clickSound.dispose()
		inputMultiplexer.removeProcessor(menuInput)
		menuList?.dispose()
	}
}
