package no.group15.playmagic.ui.views

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.*
import no.group15.playmagic.ui.views.widgets.VirtualStickWidget
import no.group15.playmagic.ui.views.widgets.Widget


class GameView(assetManager: AssetManager) {

	private val widgets = gdxArrayOf<Widget>()


	init {
		assetManager.load("virtual_joystick.png", Texture::class.java)
		assetManager.finishLoading()
		val texture = assetManager.get("virtual_joystick.png", Texture::class.java)
	    // Setup widgets based on platform and config
		widgets.add(VirtualStickWidget(
			TextureRegion(texture, 0, 0, 300, 300),
			TextureRegion(texture, 300, 0, 140, 140),
			2f
		))
		widgets.shrink()
	}

	fun update(deltaTime: Float) {
		for (widget in widgets) {
			widget.update(deltaTime)
		}
	}

	fun render(batch: SpriteBatch) {
		for (widget in widgets) {
			widget.render(batch)
		}
	}

	fun resize(width: Float, height: Float) {
		for (widget in widgets) {
			widget.resize(width, height)
		}
	}
}
