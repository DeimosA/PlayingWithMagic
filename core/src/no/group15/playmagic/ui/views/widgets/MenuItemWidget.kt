package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align


open class MenuItemWidget(
	private val boundingBox: Rectangle,
	private val font: BitmapFont,
	text: String,
	alignment: MenuItem.Alignment,
	private val hoverBackground: TextureRegion? = null,
	color: Color = Color.WHITE
) : MenuItem {

	private val glyph = GlyphLayout()
	private val position = Vector2()
	private val margin = 5f
	private val padding = 20f
	private var hover = false


	init {
		val hAlign = when (alignment) {
			MenuItem.Alignment.LEFT -> Align.left
			MenuItem.Alignment.CENTER -> Align.center
			MenuItem.Alignment.RIGHT -> Align.right
		}
		glyph.setText(font, text, color, boundingBox.width - 2 * (margin + padding), hAlign, false)
		position.set(
			boundingBox.x + margin + padding,
			boundingBox.y + boundingBox.height / 2 + glyph.height / 2
		)
	}

	override fun update(deltaTime: Float) {
	}

	override fun render(batch: SpriteBatch) {
		if (hover) {
			batch.draw(
				hoverBackground,
				boundingBox.x + margin,
				boundingBox.y + margin,
				boundingBox.width - 2 * margin,
				boundingBox.height - 2 * margin
			)
			hover = false
		}
		font.draw(batch, glyph, position.x, position.y)
	}

	override fun contains(x: Float, y: Float): Boolean {
		return boundingBox.contains(x, y)
	}

	override fun hover(x: Float, y: Float) {
		if (hoverBackground != null) {
			hover = true
		}
	}

	override fun click(x: Float, y: Float) {}

	override fun resize(width: Float, height: Float) {}

	override fun reposition(x: Float, y: Float) {
		// Only width changes, so repos should only affect x
		boundingBox.x = x
		position.x = boundingBox.x + margin + padding
	}

	override fun dispose() {
		// Nothing to dispose of
	}
}
