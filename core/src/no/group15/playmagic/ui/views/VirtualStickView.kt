package no.group15.playmagic.ui.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2


class VirtualStickView(
	padTexture: TextureRegion,
	handleTexture: TextureRegion,
	private val size: Float
) {


	private val padSprite = Sprite(padTexture)
	private val handleSprite = Sprite(handleTexture)
	private val handleCenter = Vector2(0f, 0f)
	private val margin = 1f


	init {
		val scale = size / padSprite.width
	    padSprite.setSize(size, size)
		handleSprite.setScale(scale)

	}

	fun update(batch: SpriteBatch) {
		if (Gdx.input.isTouched) {
			// TODO check pos
		} else {
			handleSprite.setCenter(handleCenter.x, handleCenter.y)
		}
		padSprite.draw(batch)
		handleSprite.draw(batch)
	}

	/**
	 * Size of the viewport the stick should be drawn in
	 */
	fun updateWorldSize(width: Float, height: Float) {
		// Lower left corner
		padSprite.setPosition(-width / 2 + margin, -height / 2 + margin)
		handleCenter.set(
			padSprite.x + padSprite.width / 2,
			padSprite.y + padSprite.height / 2
		)
	}
}
