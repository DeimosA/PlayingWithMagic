package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.graphics.g2d.SpriteBatch


interface Widget {

	/**
	 * Updates the widget with [deltaTime]
	 */
	fun update(deltaTime: Float)

	/**
	 * Render the widget using [batch] SpriteBatch
	 * Requires that batch is begun
	 */
	fun render(batch: SpriteBatch)

	/**
	 * Inform the widget of the [width] and [height] of the viewport in world coordinates
	 */
	fun resize(width: Float, height: Float)

}
