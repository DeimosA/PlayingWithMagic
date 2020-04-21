package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.ImmutableVector2
import com.badlogic.gdx.utils.Pool


class TransformComponent : Component, Pool.Poolable {

	var boundingBox: Rectangle = Rectangle()
	var position: Vector2 = Vector2() // should be Centered position
	var scale: ImmutableVector2 = ImmutableVector2(1f, 1f)
	var rotation: Float = 0f


	override fun reset() {
		boundingBox.set(0f, 0f, 0f, 0f)
		position.set(0f, 0f)
		scale = ImmutableVector2(1f, 1f)
		rotation = 0f
	}
}
