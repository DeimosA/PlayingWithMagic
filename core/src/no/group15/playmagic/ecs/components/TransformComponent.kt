package no.group15.playmagic.ecs.components

import com.badlogic.ashley.core.Component
import ktx.math.ImmutableVector2
import com.badlogic.gdx.utils.Pool


class TransformComponent : Component, Pool.Poolable {

	var position: ImmutableVector2 = ImmutableVector2(0f, 0f)
	var scale: ImmutableVector2 = ImmutableVector2(1f, 1f)
	var rotation: Float = 0f


	override fun reset() {
		position = ImmutableVector2(0f, 0f)
		scale = ImmutableVector2(1f, 1f)
		rotation = 0f
	}
}
