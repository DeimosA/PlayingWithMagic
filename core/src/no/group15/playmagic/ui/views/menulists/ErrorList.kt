package no.group15.playmagic.ui.views.menulists

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import ktx.collections.*
import ktx.inject.Context
import no.group15.playmagic.ui.views.MainMenuView
import no.group15.playmagic.ui.views.widgets.MenuItem
import no.group15.playmagic.ui.views.widgets.MenuItemWidget
import no.group15.playmagic.ui.views.widgets.MenuListWidget

class ErrorList(
	boundingBox: Rectangle,
	font: BitmapFont,
	override val hoverBackground: TextureRegion,
	private val mainMenu: MainMenuView,
	errorMessage: String,
	private val injectContext: Context
) : MenuListWidget(
	boundingBox,
	font,
	MenuItem.Alignment.RIGHT,
	hoverBackground
) {

	override val itemList: GdxArray<MenuItemWidget> = gdxArrayOf()


	init {
		itemList.add(MenuItemWidget(
			Rectangle(boundingBox.x, boundingBox.y + 1 * itemHeight, boundingBox.width, itemHeight),
			font,
			errorMessage,
			alignment,
			color = Color.ORANGE
		))

		itemList.add(object : MenuItemWidget(
			Rectangle(boundingBox.x, boundingBox.y + 0 * itemHeight, boundingBox.width, itemHeight),
			font,
			"Back",
			alignment,
			hoverBackground
		) {
			override fun click(x: Float, y: Float) {
				back()
			}
		})
	}

	override fun back() {
		mainMenu.setMenuList(MainMenuList(boundingBox, font, hoverBackground, mainMenu, injectContext))
	}


}
