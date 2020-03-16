package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
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
	// Value of the stick in +-1 range for x and y
	private val stickValue = Vector2()
	private val margin = 100f
	private val padRadius = size / 2
	private val padRadius2 = padRadius.pow(2)

	private var touchIndex = -1

	var drawStickValue = true
	private val font = BitmapFont()

	private val stickInput = object : InputAdapter() {
		override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			val cursor = viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
			if (handleCenter.dst2(cursor) < padRadius2) {
				touchIndex = pointer
				calculateStickValue(screenX, screenY)
				return true
			}
			return false
		}
		override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
			if (pointer == touchIndex) {
				calculateStickValue(screenX, screenY)
				return true
			}
			return false
		}
		override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
			if (pointer == touchIndex) {
				touchIndex = -1
				handleSprite.setCenter(handleCenter.x, handleCenter.y)
				stickValue.setZero()
				return true
			}
			return false
		}
	}


	init {
		val scale = size / padSprite.width
	    padSprite.setSize(size, size)
		padSprite.setPosition(margin, margin)
		handleSprite.setScale(scale)
		inputMultiplexer.addProcessor(stickInput)
		font.data.setScale(2f)
	}

	private fun calculateStickValue(screenX: Int, screenY: Int) {
		// Set position relative to handle center
		stickValue.set(
			viewport.unproject(Vector2(
				screenX.toFloat(),
				screenY.toFloat())
			).sub(handleCenter)
		)
		// Clamp to pad radius
		if (stickValue.len2() > padRadius2) {
			stickValue.setLength2(padRadius2)
		}
		// Set draw position
		handleSprite.setCenter(
			handleCenter.x + stickValue.x,
			handleCenter.y + stickValue.y
		)
		// Normalize stick value to +-1
		stickValue.scl(1 / padRadius)
	}

	override fun update(deltaTime: Float) {
		// TODO do something with stick value
	}

	override fun render(batch: SpriteBatch) {
		padSprite.draw(batch)
		handleSprite.draw(batch)
		if(drawStickValue) font.draw(
			batch,
			"${"%.2f".format(stickValue.x)} ${"%.2f".format(stickValue.y)}",
			margin, margin / 2 + 20
		)
	}

	override fun resize(width: Float, height: Float) {
		handleCenter.set(
			padSprite.x + padSprite.width / 2,
			padSprite.y + padSprite.height / 2
		)
		handleSprite.setCenter(handleCenter.x, handleCenter.y)
	}

	override fun dispose() {
		inputMultiplexer.removeProcessor(stickInput)
		font.dispose()
	}
}
