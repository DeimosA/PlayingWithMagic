package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.ImmutableVector2
import com.badlogic.gdx.utils.Pool


class TransformComponent : Component, Pool.Poolable {

	var boundingBox: Rectangle = Rectangle(0f, 0f, 1f, 1f)
	var position: Vector2 = Vector2() // should be Centered position
	var scale: ImmutableVector2 = ImmutableVector2(1f, 1f)
	var rotation: Float = 0f


	/**
	 * Set center position of this entity
	 */
	fun setPosition(x: Float, y: Float) {
		position.set(x, y)
		boundingBox.setCenter(x, y)
	}

	override fun reset() {
		position.set(0f, 0f)
		boundingBox.setSize(1f)
		boundingBox.setCenter(position)
		scale = ImmutableVector2(1f, 1f)
		rotation = 0f
	}
}
