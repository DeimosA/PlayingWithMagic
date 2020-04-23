package no.group15.playmagic.ui.views.menulists

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import no.group15.playmagic.network.ClientConfig
import no.group15.playmagic.network.NetworkContext
import no.group15.playmagic.server.ServerConfig
import no.group15.playmagic.ui.views.MainMenuView
import no.group15.playmagic.ui.views.widgets.MenuItem
import no.group15.playmagic.ui.views.widgets.MenuItemWidget
import no.group15.playmagic.ui.views.widgets.MenuListWidget


class MainMenuList(
	boundingBox: Rectangle,
	font: BitmapFont,
	hoverBackground: TextureRegion,
	mainMenu: MainMenuView,
	injectContext: Context
) : MenuListWidget(
	boundingBox,
	font,
	MenuItem.Alignment.RIGHT,
	hoverBackground
) {

	override val itemList: GdxArray<MenuItemWidget> = gdxArrayOf()


	init {
		// Sandbox button
		itemList.add(object : MenuItemWidget(
			Rectangle(boundingBox.x, boundingBox.y + 3 * itemHeight, boundingBox.width, itemHeight),
			font,
			"Sandbox",
			alignment,
			hoverBackground
		) {
			override fun click(x: Float, y: Float) {
				mainMenu.startGame(
					NetworkContext(
						injectContext,
						ClientConfig(host = "localhost"),
						ServerConfig(host = "localhost", maxPlayers = 1)
					)
				)
			}
		})

		// Online multiplayer button
		itemList.add(object : MenuItemWidget(
			Rectangle(boundingBox.x, boundingBox.y + 2 * itemHeight, boundingBox.width, itemHeight),
			font,
			"Online multiplayer",
			alignment,
			hoverBackground
		) {
			override fun click(x: Float, y: Float) {
				try {
					val context = NetworkContext(injectContext)
					// Attempt to connect by initializing socket
					// TODO find a better way of checking if the server is online
//					context.client.socket
					mainMenu.startGame(context)
				} catch (e: GdxRuntimeException) {
					// If connection fails, show error
					mainMenu.setMenuList(ErrorList(boundingBox, font, hoverBackground, mainMenu, "Could not connect!", injectContext))
				}
			}
		})

		// Local multiplayer button
		itemList.add(object : MenuItemWidget(
			Rectangle(boundingBox.x, boundingBox.y + 1 * itemHeight, boundingBox.width, itemHeight),
			font,
			"Local multiplayer",
			alignment,
			hoverBackground
		) {
			override fun click(x: Float, y: Float) {
				// TODO show local IP address and lobby
				// TODO connect to IP dialog and then lobby
				mainMenu.startGame(
					NetworkContext(
						injectContext,
						ClientConfig(host = "localhost")
					)
				)
			}
		})

		// Exit button
		itemList.add(object : MenuItemWidget(
			Rectangle(boundingBox.x, boundingBox.y + 0 * itemHeight, boundingBox.width, itemHeight),
			font,
			"Exit",
			alignment,
			hoverBackground
		) {
			override fun click(x: Float, y: Float) {
				back()
			}
		})
	}

	override fun back() {
		Gdx.app.exit()
	}
}
