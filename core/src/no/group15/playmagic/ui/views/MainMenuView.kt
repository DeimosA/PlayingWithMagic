package no.group15.playmagic.ui.views

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.freetype.*
import ktx.graphics.*
import ktx.inject.*
import no.group15.playmagic.PlayMagic
import no.group15.playmagic.network.NetworkContext
import no.group15.playmagic.ui.AppState
import no.group15.playmagic.ui.controllers.GamePresenter
import no.group15.playmagic.ui.views.menulists.MainMenuList
import no.group15.playmagic.ui.views.widgets.MenuListWidget
import no.group15.playmagic.utils.assets.MenuAssets
import no.group15.playmagic.utils.assets.loadAssets


class MainMenuView(
	private val appContext: PlayMagic,
	private val injectContext: Context
) : AppState {

	private val batch: SpriteBatch = injectContext.inject()
	private val inputMultiplexer: InputMultiplexer = injectContext.inject()
	private val assetManager: AssetManager = injectContext.inject()

	// Reference height of menus
	private val viewHeight = 720f
	// Extend to support 4:3 through 21:9 ratios
	private val viewport = ExtendViewport(
		4 / 3f * viewHeight, viewHeight, 21 / 9f * viewHeight, viewHeight
	)

	private lateinit var menuFont: BitmapFont
	private lateinit var titleFontBig: BitmapFont
	private lateinit var titleFontSmall: BitmapFont
	private val title1 = GlyphLayout()
	private val title1pos = Vector2(50f, viewHeight - 50)
	private val title2 = GlyphLayout()
	private val title2pos = Vector2(240f, viewHeight - 146)
	private val title3 = GlyphLayout()
	private val title3pos = Vector2(50f, viewHeight - 155)

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
			if (menuList?.contains(cursor.x, cursor.y) == true) {
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

		assetManager.registerFreeTypeFontLoaders()
		loadAssets<MenuAssets>(assetManager)
		assetManager.finishLoading()

		menuFont = assetManager.get(MenuAssets.DRAGONFLY_70.desc.fileName)
		titleFontBig = assetManager.get(MenuAssets.DRAGONFLY_120.desc.fileName)
		titleFontSmall = assetManager.get(MenuAssets.DRAGONFLY_59.desc.fileName)

		title1.setText(titleFontBig, "Playing")
		title2.setText(titleFontSmall, "with")
		title3.setText(titleFontBig, "Magic")

		clickSound = assetManager.get(MenuAssets.SOUND_CLICK.desc.fileName)

		// TODO change to using asset manager after merge with master, also fix text size and hover mess
		val hoverBackground = TextureRegion(assetManager.get<Texture>(MenuAssets.HOVER_BACKGROUND.desc.fileName))
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
		assetManager.clear()
		inputMultiplexer.removeProcessor(menuInput)
		menuList?.dispose()
	}
}
