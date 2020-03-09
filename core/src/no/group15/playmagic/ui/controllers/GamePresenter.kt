package no.group15.playmagic.ui.controllers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.graphics.use
import no.group15.playmagic.ecs.engineFactory
import no.group15.playmagic.ui.views.VirtualStickView


class GamePresenter(
	private val appContext: Game,
	private val batch: SpriteBatch
) : Screen {


	private val viewport = ExtendViewport(
		4 / 3f * 10, 10f, 21 / 9f * 10, 10f
	)
	private lateinit var engine: Engine
	private lateinit var virtualStick: VirtualStickView
	private lateinit var virtualStickTexture: Texture


	override fun show() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		engine = engineFactory(batch, viewport)
		virtualStickTexture = Texture("virtual_joystick.png")
		virtualStick = VirtualStickView(
			TextureRegion(virtualStickTexture, 0, 0, 300, 300),
			TextureRegion(virtualStickTexture, 300, 0, 140, 140),
			2f
		)
		viewport.apply()
	}

	override fun render(deltaTime: Float) {
		batch.use(viewport.camera) {
			engine.update(deltaTime)
			virtualStick.update(batch)
		}
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun resize(width: Int, height: Int) {
		viewport.update(width, height, false)
		virtualStick.updateWorldSize(
			viewport.worldWidth,
			viewport.worldHeight
		)
	}

	override fun hide() {
		dispose()
	}

	override fun dispose() {
		virtualStickTexture.dispose()
	}
}
