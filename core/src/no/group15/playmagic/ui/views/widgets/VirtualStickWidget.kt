package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.math.div
import kotlin.math.pow


class VirtualStickWidget(
	private val viewport: Viewport,
	padTexture: TextureRegion,
	handleTexture: TextureRegion,
	size: Float,
	private val inputMultiplexer: InputMultiplexer
) : Widget {

	private val padSprite = Sprite(padTexture)
	private val handleSprite = Sprite(handleTexture)
	private val handleCenter = Vector2()
	private val stickPosition = Vector2()
	private val margin = 1f
	private val padRadius = size / 2
	private val padRadius2 = padRadius.pow(2)

	private var touching = false
	private var touchIndex = -1

	private val stickInput = object : InputAdapter() {
		override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			val cursor = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
			if (handleCenter.dst2(cursor) < padRadius2) {
				touchIndex = pointer
				touching = true
				return true
			}
			return false
		}
		override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			if (touching && pointer == touchIndex) {
				touching = false
				handleSprite.setCenter(handleCenter.x, handleCenter.y)
				stickPosition.setZero()
				return true
			}
			return false
		}
	}


	init {
		val scale = size / padSprite.width
	    padSprite.setSize(size, size)
		handleSprite.setScale(scale)
		inputMultiplexer.addProcessor(stickInput)
	}

	private var timer = 0f
	override fun update(deltaTime: Float) {
		if (touching && Gdx.input.isTouched(touchIndex)) {
			stickPosition.set(
				viewport.unproject(Vector2(
					Gdx.input.getX(touchIndex).toFloat(),
					Gdx.input.getY(touchIndex).toFloat())
				).sub(handleCenter)
			)
			if (stickPosition.len2() > padRadius2) {
				stickPosition.setLength2(padRadius2)
			}
			handleSprite.setCenter(
				handleCenter.x + stickPosition.x,
				handleCenter.y + stickPosition.y
			)
			stickPosition.div(padRadius)
			// TODO do something with stick position
		}
		timer += deltaTime
		if (timer >= 1f) {
			timer -= 1f
			println("Stick pos: ${stickPosition.x}, ${stickPosition.y}")
		}
	}

	override fun render(batch: SpriteBatch) {
		padSprite.draw(batch)
		handleSprite.draw(batch)
	}

	override fun resize(width: Float, height: Float) {
		// Lower left corner
		padSprite.setPosition(-width / 2 + margin, -height / 2 + margin)
		handleCenter.set(
			padSprite.x + padSprite.width / 2,
			padSprite.y + padSprite.height / 2
		)
		handleSprite.setCenter(handleCenter.x, handleCenter.y)
	}

	override fun dispose() {
		inputMultiplexer.removeProcessor(stickInput)
	}
}
