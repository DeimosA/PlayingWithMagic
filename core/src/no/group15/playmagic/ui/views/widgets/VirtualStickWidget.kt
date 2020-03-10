package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2


class VirtualStickWidget(
	padTexture: TextureRegion,
	handleTexture: TextureRegion,
	size: Float
) : Widget {

	private val padSprite = Sprite(padTexture)
	private val handleSprite = Sprite(handleTexture)
	private val handleCenter = Vector2()
	private val margin = 1f


	init {
		val scale = size / padSprite.width
	    padSprite.setSize(size, size)
		handleSprite.setScale(scale)
	}

	override fun update(deltaTime: Float) {
		if (Gdx.input.isTouched) {
			// TODO check pos
		} else {
			handleSprite.setCenter(handleCenter.x, handleCenter.y)
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
	}
}
