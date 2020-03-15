package no.group15.playmagic.ui.views

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.collections.*
import ktx.graphics.use
import no.group15.playmagic.ui.views.widgets.VirtualStickWidget
import no.group15.playmagic.ui.views.widgets.Widget


class GameView(assetManager: AssetManager, inputMultiplexer: InputMultiplexer) {

	private val widgets = gdxArrayOf<Widget>()
	private val viewHeight = 1080f
	private val	viewport = ExtendViewport(
		4 / 3f * viewHeight, viewHeight, 21 / 9f * viewHeight, viewHeight
	)


	init {
		assetManager.load("virtual_joystick.png", Texture::class.java)
		assetManager.finishLoading()
		val texture = assetManager.get("virtual_joystick.png", Texture::class.java)
	    // Setup widgets based on platform and config
		widgets.add(VirtualStickWidget(
			viewport,
			TextureRegion(texture, 0, 0, 300, 300),
			TextureRegion(texture, 300, 0, 140, 140),
			250f,
			inputMultiplexer
		))
		widgets.shrink()
	}

	fun update(deltaTime: Float) {
		for (widget in widgets) {
			widget.update(deltaTime)
		}
	}

	fun render(batch: SpriteBatch) {
		viewport.apply()
		batch.use(viewport.camera) {
			for (widget in widgets) {
				widget.render(batch)
			}
		}
	}

	fun resize(width: Int, height: Int) {
		viewport.update(width, height, true)
		for (widget in widgets) {
			widget.resize(viewport.worldWidth, viewport.worldHeight)
		}
	}

	fun dispose() {
		for (widget in widgets) {
			widget.dispose()
		}
	}
}
