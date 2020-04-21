package no.group15.playmagic.ui.views.widgets

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import ktx.collections.*


abstract class MenuListWidget(
	protected val boundingBox: Rectangle,
	protected val font: BitmapFont,
	protected val alignment: MenuItem.Alignment,
	protected open val hoverBackground: TextureRegion? = null
) : MenuItem {

	protected abstract val itemList: GdxArray<MenuItemWidget>
	protected val itemHeight = 80f


	override fun update(deltaTime: Float) {
	}

	override fun render(batch: SpriteBatch) {
		itemList.forEach {
			it.render(batch)
		}
	}

	override fun contains(x: Float, y: Float): Boolean {
		return boundingBox.contains(x, y)
	}

	override fun hover(x: Float, y: Float) {
		itemList.forEach {
			if (it.contains(x, y)) {
				it.hover(x, y)
				return@forEach
			}
		}
	}

	override fun click(x: Float, y: Float) {
		Array.ArrayIterator(itemList).forEach {
			if (it.contains(x, y)) {
				it.click(x, y)
				return@forEach
			}
		}
	}

	override fun resize(width: Float, height: Float) {
		reposition(
			width - boundingBox.width - 50f,
			boundingBox.y
		)
	}

	override fun reposition(x: Float, y: Float) {
		boundingBox.setPosition(x, y)
		Array.ArrayIterator(itemList).forEach {
			it.reposition(x, y)
		}
	}

	override fun dispose() {
		Array.ArrayIterator(itemList).forEach {
			it.dispose()
		}
	}

	abstract fun back()
}
